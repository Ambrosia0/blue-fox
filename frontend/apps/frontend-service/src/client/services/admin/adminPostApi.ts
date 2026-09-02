import { apiClient } from "@services/apiClient";

export async function deletePost(postId: number) {
    apiClient.delete(`/api/admin/post/${postId}`);
}

export async function getPost(postId: number) {
    apiClient.get(`/api/admin/post/${postId}`);
}