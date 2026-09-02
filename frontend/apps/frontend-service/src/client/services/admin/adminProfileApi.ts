import { apiClient } from "@services/apiClient";

export async function banUser(userId: string){
    apiClient.post(`/api/admin/profile/${userId}/ban`);
}

export async function unbanUser(userId: string) {
    apiClient.post(`/api/admin/profile/${userId}/ban`);
}