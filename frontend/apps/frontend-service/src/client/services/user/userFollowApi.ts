import { apiClient } from "@services/apiClient";
import { Slice } from "@services/types";

export type UserFollowResponse = {
    followedUserId: string;
    followedAt: number;
}

export async function getUserFollows(page?: number) {
    return (await apiClient.get<Slice<UserFollowResponse>>(`/api/me/follow/user`, {
        params: {
            page
        }
    })).data;
}

export async function followUser(userId: string): Promise<void> {
    return (await apiClient.post<Promise<void>>(`/api/me/follow/user/${userId}`)).data;
}

export async function removeFollow(userId: string): Promise<void> {
    return (await apiClient.delete(`/api/me/follow/user/${userId}`)).data;
}