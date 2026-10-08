import { apiClient } from "@services/apiClient";
import { UserInfo } from "../../types/user";

export type AuthorizedInfo = {
    id: string;
    username: string;
    about: string;
    email: string;
    avatarId: string;
    createdAt: string;
}

type Status = "ONLINE" | "OFFLINE";

export type PublicUserProfile = {
    id: string;
    username: string;
    firstName: string;
    lastName: string;
    about: string;
    avatarId?: string;
    email?: string;
    status?: Status;
    lastActivity?: number;
    followCount: number;
    createdAt: string;
}

export type ProfileSettings = {
    displayEmail: boolean;
    displayActivity: boolean;
}

export type CurrentUserProfile = {
    id: string;
    username: string;
    firstName: string;
    lastName: string;
    about: string;
    avatarId?: string;
    email?: string;
    status: Status;
    lastActivity: number;
    followCount: number;
    createdAt: string;
    settings: ProfileSettings;
}

export function isUserInfo(val: any): val is UserInfo {
    return val && 'username' in val;
};

export async function getProfileInfo(username: string) {
    return (await apiClient.get<PublicUserProfile>(`/api/v1/public/profile/${username}`)).data;
}

export async function getAutenticationInfo() {
    return (await apiClient.get<AuthorizedInfo>("/api/v1/me/profile")).data;
}

export async function setAboutText(text: string) {
    return apiClient.patch('/api/v1/me/about', {
        text: text
    })
}

export async function getUserInfo(ids: string[]) {
    const users = new Map<string, UserInfo>();
    (await apiClient.post<UserInfo[]>("/api/v1/public/profile/info", ids)).data.forEach(val => users.set(val.id, val));
    return users;
}

export async function getMyProfile(){
    return (await apiClient.get<CurrentUserProfile>("/api/v1/me/profile")).data;
}

export async function updateProfileSettings(settings: ProfileSettings){
    return (await apiClient.put<{}>("/api/v1/me/profile/settings", settings)).data;
}

export async function searchUsers(text: string) {
    return (await apiClient.get<UserInfo[]>("/api/v1/public/profile", {
        params: {
            searchString: text
        }
    })).data;
}