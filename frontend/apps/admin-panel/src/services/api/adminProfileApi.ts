import { apiClient } from "../apiClient";

export type UserInfoResponse = {
    id: string;
    username: string;
    email: string;
    avatarUrl?: string;
    isActive: boolean;
    isEnabled: boolean;
    createdAt: string;
}

export async function fetchUsers(page: number = 0, pageSize: number = 20) {
    return apiClient.get<{ content: UserInfoResponse[]; totalElements: number; totalPages: number }>(`/api/admin/profile?page=${page}&size=${pageSize}`);
}

export async function searchUsers(username: string) {
    return apiClient.get<UserInfoResponse[]>(`/api/admin/profile?username=${username}`);
}

export async function banUser(userId: string) {
    return apiClient.post(`/api/admin/profile/${userId}/ban`);
}

export async function unbanUser(userId: string) {
    return apiClient.post(`/api/admin/profile/${userId}/unban`);
}