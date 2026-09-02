import { apiClient } from "@services/apiClient";
import { getUserInfo } from "./userProfileApi";
import { UserInfo } from "../../types/user";

export type TipTapDoc = {
    type: string;
    content: string
}

export type PostContentResponse = {
    id: number;
    authorId: string;
    title: string;
    content: string;
    isLiked?: boolean;
    commentCount: number;
    tags?: string[];
    likeCount: number;
    publishedAt: string;
    score?: number;
}

export type PostViewResponse = {
    id: number;
    authorId: string;
    title: string;
    preview: string;
    isLiked?: boolean;
    commentCount: number;
    likeCount: number;
    publishedAt: string;
    score?: number;
}

export type Post = {
    post: PostContentResponse | PostViewResponse;
    user: UserInfo;
}

export type SearchType = "POPULAR" | "RELEVANCY" | "LATEST" | "BEST" | "PERSONALIZED";
export type Direction = "ASC" | "DESC"
export type SortOption = "date" | "score";

export type PostFilter = {
    authorId?: string,
    searchString?: string,
    lastSeenId?: number,
    lastSeenInstant?: string,
    lastSeenLikeCount?: number,
    communityId?: number,
    tags?: string[],
    searchType?: SearchType,
    lastScore?: number,
    direction?: Direction,
    sortOption?: SortOption
}


export function isViewPost(val: any): val is PostViewResponse {
    return val && 'preview' in val;
};

export async function getPosts(filter?: PostFilter): Promise<Post[]> {
    const res = await apiClient.get<PostViewResponse[]>('/api/public/post', {
        params: {
            ...filter || {}
        }
    });
    if (res.data.length === 0) {
        return [];
    }
    const uniqueUsers = new Set<string>();
    res.data.forEach(val => uniqueUsers.add(val.authorId));

    return Promise.allSettled([getUserInfo(Array.from(uniqueUsers))])
        .then(([usersRes]) => {
            const users = usersRes.status === "fulfilled" ?
                usersRes.value :
                undefined;
            return res.data
                .map(val => {
                    return {
                        post: val,
                        user: 
                            users?.get(val.authorId) ?? 
                            { id: val.authorId, username: "", firstName: "", lastName: "" },
                    }
                })
        })
}

export async function getPostContent(id: number): Promise<PostContentResponse> {
    return (await apiClient.get<PostContentResponse>(`/api/public/post/${id}`)).data;
}

export async function likePost(id: number) {
    return apiClient.post(`/api/user/post/${id}/like`);
}

export async function unlikePost(id: number) {
    return apiClient.delete(`/api/user/post/${id}/like`);
}

export async function getLike(id: number) {
    return (await apiClient.get<boolean>(`/api/user/post/${id}/like`)).data;
}