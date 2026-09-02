import { apiClient } from "@services/apiClient";

export type CommunityCreate = {
    slug: string;
    displayedName: string;
    tags: string[];
    isPrivate: boolean;
};

export type CommunityEdit = Partial<{
    tags: string[];
    rules: string[];
    displayedName: string;
    description: string;
}>;

export type CommunityPreview = {
    id: number;
    displayedName: string;
    slug: string;
    followCount: number;
    avatarId?: string;
    tags?: string[];
    score?: number;
    createdAt?: string;
};

export type CommunityResponse = {
    id: number;
    displayedName: string;
    slug: string;
    ownerId?: string;
    avatarId?: string;
    description: string;
    tags: string[];
    rules: string[];
    communityModerators: string[];
    isPrivate: boolean;
    followCount: number;
    isFollowed?: boolean;
    scopes: Scope[];
    createdAt: string;
};

export type CommunityEventFilter = {
    searchString?: string;
    lastSeenId?: number;
    lastSeenScore?: number;
    lastSeenInstant?: number;
    tags?: string[];
    direction?: 'ASC' | 'DESC';
};

export type ScopePair = {
    userId: string;
    scopes: Scope[];
};

export type UserScope = {
    userId: string;
    scopeType: Scope;
    communityId: number;
};

export const SCOPES = [
    "POST_DELETE", 
    "COMMENT_DELETE",
    "USER_BAN",
    "USER_UNBAN", 
    "FOLLOW_MANAGE"
] as const;

export type Scope = typeof SCOPES[number];

export async function getCommunities(
    filter?: CommunityEventFilter
): Promise<CommunityPreview[]> {
    const params = new URLSearchParams();
    if (filter?.searchString) params.append('searchString', filter.searchString);
    if (filter?.lastSeenId) params.append('lastSeenId', String(filter.lastSeenId));
    if (filter?.lastSeenScore) params.append('lastSeenScore', String(filter.lastSeenScore));
    if (filter?.lastSeenInstant) params.append('lastSeenInstant', String(filter.lastSeenInstant));
    if (filter?.tags) filter.tags.forEach(tag => params.append('tags', tag));
    if (filter?.direction) params.append('direction', filter.direction);

    const queryString = params.toString();
    const url = queryString ? `/api/public/community?${queryString}` : '/api/public/community';
    return (await apiClient.get<CommunityPreview[]>(url)).data;
}

export async function getCommunity(
    communityId: number
): Promise<CommunityResponse> {
    return (await apiClient.get<CommunityResponse>(`/api/public/community/${communityId}`)).data;
}

export async function getCommunityModerators(
    id: number
): Promise<string[]> {
    return (await apiClient.get<string[]>(`/api/public/community/${id}/moderators`)).data;
}

export async function createCommunity(
    data: CommunityCreate
): Promise<CommunityResponse> {
    return (await apiClient.post<CommunityResponse>("/api/user/community", data)).data;
}

export async function editCommunity(
    id: number,
    data: CommunityEdit
): Promise<CommunityResponse> {
    return (await apiClient.patch<CommunityResponse>(`/api/user/community/${id}`, data)).data;
}

export async function uploadCommunityAvatar(
    id: number,
    file: File
): Promise<CommunityResponse> {
    const formData = new FormData();
    formData.append("file", file);
    return (await apiClient.post<CommunityResponse>(`/api/user/community/${id}/avatar`, formData, {
        headers: {
            "Content-Type": "multipart/form-data",
        },
    })).data;
}

export async function banUser(
    communityId: number,
    userId: string,
    beforeDate: string
): Promise<void> {
    await apiClient.post(`/api/user/community/${communityId}/ban/${userId}`, beforeDate);
}

export async function unbanUser(
    communityId: number,
    userId: string
): Promise<void> {
    await apiClient.delete(`/api/user/community/${communityId}/ban/${userId}`);
}

export async function editCommunityScopes(
    id: number,
    scopes: ScopePair[]
): Promise<void> {
    await apiClient.put<void>(`/api/user/community/${id}/scopes`, scopes);
}

export async function getMyScopes(
    id: number
): Promise<UserScope[]> {
    return (await apiClient.get<UserScope[]>(`/api/user/community/${id}/me/scopes`)).data;
}

export async function getCommunityScopes(
    id: number
): Promise<ScopePair[]>{
    return (await apiClient.get<ScopePair[]>(`/api/user/community/${id}/scopes`)).data;
}

export async function deletePost(
    id: number
): Promise<void>{
    return (await apiClient.delete(`/api/user/post/${id}`)).data;
}

export async function getCommunityScopesList(): Promise<Scope[]> {
    return (await apiClient.get<Scope[]>("/api/user/community/scopes")).data;
}

export async function checkSlug(slug: string) {
    return (await apiClient.post<boolean>(`/api/user/community/slugcheck`,
        {
            slug: slug
        }
    )).data;
}