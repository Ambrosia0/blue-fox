import { apiClient } from "@services/apiClient"
import { getUserInfo } from "./userProfileApi"
import { UserInfo } from "../../types/user"
import { UserResponse } from "@services/types"

type CommentData = {
    commentId: number,
    postId: number,
    user: UserResponse,
    content: string,
    likeCount: number,
    parentComment?: number,
    numberOfChildren: number,
    createdAt: string,
    isLiked?: boolean,
    score?: number,
    attachmentUrl?: string
}

type CreateComment = {
    postId: number;
    content: string;
    parentComment?: number;
}

type CommentCreateResponse = Omit<CommentData, 'likeCount' & 'numberOfChildren' & 'isLiked' & 'score'>

export type SortField = "DATE" | "LIKES" | "HOT";


export type RootCommentFilter = {
    sortField?: SortField,
    lastSeenId?: number,
    lastSeenCount?: number,
    lastSeenInstant?: string,
    direction?: "ASC" | "DESC"
}

export function isRootComment(val: any): val is CommentData {
    return val && (val.parentComment === undefined || val.parentComment === null);
};

export function isTreeComment(val: any): val is CommentData {
    return val && typeof val.parentComment === 'number';
};

export async function getRootCommentsForPost(postId: number, filter?: RootCommentFilter) {
    return await apiClient.get<CommentData[]>(`/api/v1/public/post/${postId}/comments`, {
        params: {
            ...filter
        }
    });
}

export async function getComment(commentId: number) {
    return await apiClient.get<CommentData>(`/api/v1/public/comment/${commentId}`);
}

export async function getCommentTree(commentId: number) {
    return await apiClient.get<CommentData[]>(`/api/v1/public/comment/${commentId}/tree`);
}

export async function createComment(createComment: CreateComment, file?: File): Promise<CommentCreateResponse> {
    const form = new FormData();
    form.append('comment',
        new Blob([JSON.stringify(createComment)], {
            type: 'application/json'
        }));
    if (file) form.append('attachment', file);
    return (await apiClient.post<CommentCreateResponse>('/api/v1/comment', form)).data;
}

export async function likeComment(commentId: number) {
    return apiClient.post(`/api/v1/comment/${commentId}/like`);
}

export async function unlikeComment(commentId: number) {
    return apiClient.delete(`/api/v1/comment/${commentId}/like`);
}

export async function deleteComment(commentId: number) {
    return await apiClient.delete(`/api/v1/comment/${commentId}`);
}