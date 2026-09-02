import { createStaticHandler, createStaticRouter, Outlet, Params, redirect, StaticRouterProvider, useNavigate, type LoaderFunctionArgs, type RouteObject } from "react-router";
import { renderToString } from "react-dom/server";
import { Root } from "./client/pages/Root";
import { ErrorDisplay } from "./client/components/ErrorDisplay";
import { Home } from "./client/pages/Home";
import { Login } from "./client/pages/Login";
import { PostContainer } from "./client/components/post/PostContainer";
import { PostPreviewContainer } from "./client/components/post/PostPreviewContainer";
import { CommunityList } from "./client/components/community/CommunityList";
import { CommunityDetail } from "./client/components/community/CommunityDetail";
import { ProfileDataDisplay } from "./client/components/profile/Profile";
import { Loading } from "./client/pages/Loading";
import { PersonalFeedContainer } from "./client/components/post/PersonalFeedContainer";
import { useEffect } from "react";
import { AuthCallback } from "./client/authCallback";
// import { CacheProvider } from '@emotion/react';

// replace loading block with component
export const routes: RouteObject[] = [
    { 
        id: "root",
        path: "/",
        Component: Root,
        ErrorBoundary: ErrorDisplay,
        children:[
            {
                id: "home",
                path: "/",
                Component: Home,
                ErrorBoundary: ErrorDisplay,
                children: [
                    {
                        id: "preview",
                        path: "/",
                        Component: () => <PostPreviewContainer postFilter={{searchType: "POPULAR"}} />
                    },
                    {
                        id: "latest",
                        path: "/latest",
                        Component: () => <PostPreviewContainer postFilter={{searchType: "LATEST"}}/>
                    },
                    {
                        id: "search",
                        path: "/search",
                        Component: () => <PostPreviewContainer postFilter={{searchType: "RELEVANCY"}} />
                    },
                    {
                        id: "post",
                        path: "/post/:postId",
                        Component: PostContainer,
                        loader: postPageLoader
                    },
                    {
                        id: "communities",
                        path: "/communitiest",
                        Component: CommunityList
                    },
                    {
                        id: "community",
                        path: "/community/:slug",
                        Component: CommunityDetail,
                        loader: communityLoader
                    },
                    {
                        id: "personalFeed",
                        path: "/personal",
                        Component: PersonalFeedContainer
                    },
                    {
                        id: "follows",
                        path: "/follows"
                    }
                ]
            },
            {
                id: "profile",
                path: "/profile/:username",
                ErrorBoundary: ErrorDisplay,
                Component: ProfileDataDisplay,
                loader: userDataLoader
            }
        ]
    },
    {
        id: "auth-callback",
        path: "/auth/callback",
        Component: Loading
    },
    {
        id: "editor-route",
        path: "/post/:postId/editor",
        Component: Loading,
    },
    {
        id: "login",
        path: "/login",
        Component: Login
    },
];

async function userDataLoader({request, params}: LoaderFunctionArgs) {
    const headers = request.headers;
    headers.delete('host');
    const resp = await fetch(`${process.env.SERVICES_PROFILE}/api/public/profile/${params.username}`, {
        headers: headers,
    });
    if(resp.status == 401)
        throw redirect("/login");
    return resp;
}

async function postPageLoader({request, params}: LoaderFunctionArgs){
    const headers = request.headers;
    headers.delete('host');
    const resp = await fetch(`${process.env.SERVICES_CONTENT}/api/public/post/${params.postId}`, {
        headers: headers
    });
    if(resp.status === 401)
        throw redirect("/login");
    return resp;
}

async function communityLoader({request, params}: LoaderFunctionArgs){
    const resp = await fetch(`${process.env.SERVICES_COMMUNITY}/api/public/community${params.slug}`, {
        headers: request.headers
    });
    if(resp.status === 401)
        throw redirect("/login");
    return resp;
}

async function authenticateMiddleware({request}: {request: Request}) {
    try {
        const cookieHeader = request.headers.get("cookie") ?? "";
        const authCookie = Object.fromEntries(cookieHeader?.split(";").map(c => {
            const [k, ...rest] = c.split("=");
            return [k?.trim(), rest.join("=")];
        })).authorization ?? null;

        if(authCookie){
            throw redirect("/");
        }
        return null;
    } catch (error) {
        throw redirect(`${new URL(request.url).pathname}`);
    }
}

async function authMiddleware({request}: {request: Request}){
    try {
        const userAgent = request.headers.get('User-Agent');
        const cookieHeader = request.headers.get("cookie") ?? "";
        const authCookie = Object.fromEntries(cookieHeader?.split(";").map(c => {
            const [k, ...rest] = c.split("=");
            return [k?.trim(), rest.join("=")];
        })).authorization ?? null;

        if(!authCookie || !userAgent){
            throw redirect(`/login?redirect=${new URL(request.url).pathname}`);
        }
        return null;
    } catch (error) {
        throw redirect(`/login?redirect=${new URL(request.url).pathname}`);
    }
}

async function editorContentLoader({request, params}: {request: Request, params: Params})  {
    const headers = request.headers;
    headers.delete('host');
    const resp = await fetch(`${process.env.SERVICES_CONTENT}/api/user/editor/${params.postId}`, {
        headers: headers
    });
    if(resp.status === 401)
        throw redirect("/login");
    return resp;
}


let {query, dataRoutes} = createStaticHandler(routes);

export async function handler(request: Request){
    let context = await query(request);
    if(context instanceof Response){
        return context;
    }

    let router = createStaticRouter(dataRoutes, context);
    let html = renderToString(
        <StaticRouterProvider
            router={router}
            context={context}
        />
    );
    let leaf = context.matches[context.matches.length - 1];
    let actionHeaders = context.actionHeaders[leaf!.route.id];
    let loaderHeaders = context.loaderHeaders[leaf!.route.id];
    let headers = new Headers(actionHeaders);
    if (loaderHeaders) {
        loaderHeaders.forEach((val, key) => headers.append(key, val))
    }

    headers.set("Content-Type", "text/html; charset=utf-8");

    return({
        html,
        status: context.statusCode,
        headers,
    });
}