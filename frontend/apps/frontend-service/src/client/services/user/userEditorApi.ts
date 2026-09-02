import { apiClient } from "@services/apiClient";
import { Page, Pageable } from "../../types/types";

export type Draft = {
    id: number;
    title: string;
    updatedAt: string;
}

export type PostEditorContent = {
    id: number;
    title: string;
    tags?: string[];
    content: string;
    updatedAt: string;
}

type PostEditorCreate = {
    id: number;
    authorId: number;
    title: string;
    createdAt: string
}

export type PostAttachment = {
    attachmentId: string,
    postId: number;
}


export function isDraft(val: any): val is Draft {
    return val && 'updatedAt' in val;
};

export async function getUnpublished(pageable?: Pageable) {
    return (await apiClient.get<Page<Draft>>('/api/me/post', {
        params: {
            ...pageable || {}
        }
    })).data;
}

export async function deletePost(postId: number) {
    return (await apiClient.delete(`/api/me/post/${postId}`));
}

export async function getEditablePostContent(postId: number) {
    return (await apiClient.get<PostEditorContent>(`/api/me/editor/${postId}`)).data;
}

export async function publishPost(postId: number) {
    return (await apiClient.post(`/api/me/post/${postId}/publish`));
}

export async function saveContent(postId: number, title: string, post: string) {
    return (await apiClient.patch(`/api/me/post/${postId}`, {
        title: title,
        post: post
    }))
}

export async function createDraft(title: string) {
    return (await apiClient.post<PostEditorCreate>('/api/me/post', {
        title
    })).data;
}

export async function attachMedia(
    postId: number,
    file: FormData,
    onProgress?: (event: { progress: number }) => void,
    abortSignal?: AbortSignal
) {
    if (!file.get("attachment"))
        return;
    return (await apiClient.post<string>(`/api/me/post/${postId}/attachment`, file, {
        signal: abortSignal,
        onUploadProgress: (progressEvent) => {
            if (progressEvent.total) {
                const progress = Math.round((progressEvent.loaded / progressEvent.total) * 100);
                onProgress?.({ progress })
            }
        }
    })).data;
}

export async function getAttachedMedia(postId: number) {
    return (await apiClient.get<PostAttachment[]>(`/api/me/post/${postId}/attachment`)).data;
}

export async function deleteAttachment(postId: number, attachmentId: string) {
    return (await apiClient.delete(`/api/me/post/${postId}/attachment/${attachmentId}`));
}