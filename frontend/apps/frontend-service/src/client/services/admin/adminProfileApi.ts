import { apiClient } from "@services/apiClient";

export async function banUser(userId: string){
    apiClient.post(`/api/v1/profile/${userId}/ban`);
}

export async function unbanUser(userId: string) {
    apiClient.post(`/api/v1/profile/${userId}/ban`);
}