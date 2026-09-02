import { apiClient } from "../apiClient";

export type PostViewResponse = {
    id: number;
    authorId: string;
    title: string;
    preview: string;
    isLiked?: boolean;
    likeCount: number;
    publishedAt: string;
    score?: number;
}

export async function fetchPosts(page: number = 0, pageSize: number = 20) {
    return apiClient.get<PostViewResponse[]>('/api/public/post', {
        params: {
            page: page,
            size: pageSize
        }
    });
}

export async function deletePost(postId: number) {
    return apiClient.delete(`/api/admin/post/${postId}`);
}