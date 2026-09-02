import { createBrowserRouter, data, LoaderFunctionArgs } from "react-router";
import { ErrorDisplay } from "../components/ErrorDisplay";
import axios, { HttpStatusCode } from "axios";
import { editorPostSave, postLikeAction } from "./actions";
import { getLike, getPostContent } from "../services/user/userPostApi";
import { getMyProfile, getProfileInfo } from "../services/user/userProfileApi";
import { getEditablePostContent } from "../services/user/userEditorApi";
import { Root } from "../pages/Root";
import { Home } from "../pages/Home";
import { CircularLoading, Loading } from "../pages/Loading";
import { getCommunity } from "@services/user/userCommunityApi";
import { Login } from "../pages/Login";
import { userManager } from "../auth";
import { AuthCallback } from "../authCallback";

export let router = createBrowserRouter([
    {
        id: "root",
        path: "/",
        Component: Root,
        HydrateFallback: Loading,
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
                        async lazy() {
                            const { PostPreviewContainer } = await import ("../components/post/PostPreviewContainer");
                            return { Component: () => <PostPreviewContainer postFilter={{searchType: "POPULAR"}}/> }
                        },
                        HydrateFallback: CircularLoading,
                    },
                    {
                        id: "latest",
                        path: "/latest",
                        async lazy() {
                            const { PostPreviewContainer } = await import ("../components/post/PostPreviewContainer");
                            return { Component: () => <PostPreviewContainer postFilter={{searchType: "LATEST"}}/> }
                        },
                        HydrateFallback: CircularLoading,
                    },
                    {
                        id: "search",
                        path: "/search",
                        async lazy() {
                            const { SearchResult } = await import ("../components/search/SearchResult");
                            return { Component: SearchResult}
                        },
                        HydrateFallback: CircularLoading,
                        loader: searchLoader
                    },
                    {
                        id: "post",
                        path: "/post/:postId",
                        async lazy() {
                            const { PostContainer } = await import ("../components/post/PostContainer");
                            return { Component: PostContainer }
                        },
                        HydrateFallback: CircularLoading,
                        loader: postLoader,
                        ErrorBoundary: ErrorDisplay,
                        shouldRevalidate: () => false,
                        children: [
                            {
                                path: "like",
                                action: postLikeAction
                            }
                        ]
                    },
                    {
                        id: "communities",
                        path: "/communities",
                        async lazy() {
                            const { CommunityList } = await import("../components/community/CommunityList");
                            return { Component: CommunityList }
                        },
                        HydrateFallback: CircularLoading,
                    },
                    {
                        id: "community",
                        path: "/community/:slug",
                        async lazy() {
                            const { CommunityDetail } = await import("../components/community/CommunityDetail");
                            return { Component: CommunityDetail }
                        },
                        HydrateFallback: CircularLoading,
                        loader: communityLoader,
                        ErrorBoundary: ErrorDisplay,
                        shouldRevalidate: () => false
                    },
                    {
                        id: "personalFeed",
                        path: "/personal",
                        middleware: [authMiddleware],
                        async lazy(){
                            const { PersonalFeedContainer } = await import ("../components/post/PersonalFeedContainer");
                            return { Component: () => <PersonalFeedContainer /> }
                        },
                        HydrateFallback: CircularLoading
                    },
                    {
                        id: "community-follows",
                        path: "/follows/communities",
                        middleware: [authMiddleware],
                        HydrateFallback: CircularLoading
                    },
                    {
                        id: "user-follows",
                        path: "/follows/users",
                        middleware: [authMiddleware],
                        HydrateFallback: CircularLoading
                    }
                ]
            },
            {
                id: "profile",
                path: "/profile/:username",
                async lazy() {
                    const { ProfileDataDisplay } = await import ("../components/profile/Profile");
                    return { Component: ProfileDataDisplay }
                },
                HydrateFallback: CircularLoading,
                loader: userInfoLoader,
                shouldRevalidate: () => false,
                ErrorBoundary: ErrorDisplay,
            },
            {
                id: "profile-settings",
                path: "/profile/settings",
                async lazy() {
                    const { ProfileSettingsMenu } = await import("../components/profile/ProfileSettingsMenu");
                    return { Component: ProfileSettingsMenu }
                },
                loader: profileLoader,
                middleware: [authMiddleware],
                HydrateFallback: CircularLoading,
                ErrorBoundary: ErrorDisplay,
                shouldRevalidate: () => false,
            },
            {
                id: "fallback",
                path: "*",
                Component: () => <ErrorDisplay status={HttpStatusCode.NotFound} statusText="Not found!" />
            }
        ],
        
    },
    {
        id: "editor-route",
        path: "/post/:postId/editor",
        async lazy() {
            const { Editor } = await import ("../pages/Editor");
            return { Component: Editor}
        },
        HydrateFallback: Loading,
        ErrorBoundary: ErrorDisplay,
        loader: editorInfoLoader,
        action: editorPostSave,
        shouldRevalidate: () => true,
        middleware: [authMiddleware]
    },
    {
        id: "login",
        Component: Login,
        path: "/login"
    },
    {
        id: "auth-callback",
        path: "/auth/callback",
        Component: AuthCallback
    },
], 
{
    hydrationData: window.__staticRouterHydrationData
});

async function searchLoader({request}: LoaderFunctionArgs){
    const url = new URL(request.url);
    const query = url.searchParams.get('q') || '';
    const tags = url.searchParams.get('tags')?.split(',').filter(Boolean) || [];
    const communityId = url.searchParams.get('ci') || undefined;
    const authorId = url.searchParams.get('ui') || undefined;
    return {
        query,
        tags,
        communityId,
        authorId,
    };
}

async function authMiddleware({}){
    const user = await userManager.getUser();
    if(!user || user.expired){
        await userManager.signinRedirect({
            state: {
                returnUrl: window.location.pathname+window.location.search
            }
        });
    }
}

async function profileLoader(){
    try {
        return (await getMyProfile())
    } catch (error) {
        if(axios.isAxiosError(error)){
            const errorData = error.response?.data;
            throw data(JSON.stringify(errorData), {status: error.response?.status ?? 500, statusText: error.response?.statusText});
        }
    }
}

async function postLoader({params}){
    try {
        return (await getPostContent(Number.parseInt(params.postId!)));
    } catch (error) {
        if(axios.isAxiosError(error)){
            const errorData = error.response?.data;
            throw data(JSON.stringify(errorData), {status: error.response?.status ?? 500, statusText: error.response?.statusText});
        }
    }
}

async function communityLoader({params}){
    try {
        return await getCommunity(params.slug);
    } catch (error) {
        if(axios.isAxiosError(error)){
            const errorData = error.response?.data;
            throw data(JSON.stringify(errorData), {status: error.response?.status ?? 500, statusText: error.response?.statusText});
        }
    }
}

async function clientLoader({params, serverLoader}) {
    const serverData = await serverLoader();
    try {
        const isLiked = (await getLike(params.postId!));
        return {
            ...serverData,
            isLiked
        };
    } catch (error) {
        console.log(error);
        return serverData;
    }
}
clientLoader.hydrate = true;

async function userInfoLoader({params}){
    try {
        const user = await userManager.getUser();
        if(user && user.profile?.preferred_username === params.username)
            return (await getMyProfile());
        return (await getProfileInfo(params.username));
    } catch (error) {
        if(axios.isAxiosError(error)){
            const errorData = error.response?.data;
            throw data(JSON.stringify(errorData), {status: error.response?.status ?? 500, statusText: error.response?.statusText});
        }
    }
}

async function editorInfoLoader({params}) {
    try {
        const postId = params.postId;
        return await getEditablePostContent(Number.parseInt(postId!));
    } catch (error) {
        if(axios.isAxiosError(error)){
            const errorData = error.response?.data;
            throw data(JSON.stringify(errorData), {status: error.response?.status ?? 500, statusText: error.response?.statusText});
        }
    }
}