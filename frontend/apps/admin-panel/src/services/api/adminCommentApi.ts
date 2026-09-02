import { apiClient } from "../apiClient";

export type CommentResponse = {
    id: number;
    authorId: string;
    content: string;
    createdAt: string;
}

export async function fetchComments(page: number = 0, pageSize: number = 20) {
    return apiClient.get<{ content: CommentResponse[]; totalElements: number; totalPages: number }>(`/api/admin/comment?page=${page}&size=${pageSize}`);
}

export async function deleteComment(id: number) {
    return apiClient.delete(`/api/admin/comment/${id}`);
}