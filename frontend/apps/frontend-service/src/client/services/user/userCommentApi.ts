import { apiClient } from "@services/apiClient"
import { getUserInfo } from "./userProfileApi"
import { UserInfo } from "../../types/user"

type CommentData = {
    commentId: number,
    postId: number,
    userId: string,
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

export type Comment = {
    comment: Omit<CommentData, 'userId'>,
    user: UserInfo,
}

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

export async function getRootCommentsForPost(postId: number, filter?: RootCommentFilter): Promise<Comment[]> {
    const res = await apiClient.get<CommentData[]>(`/api/public/post/${postId}/comments`, {
        params: {
            ...filter
        }
    });
    if (!res.data[0])
        return [];
    const users = new Set<string>();
    res.data.forEach((val) => users.add(val.userId));
    const userData = await getUserInfo(Array.from(users));
    return res.data
        .filter(isRootComment)
        .map((val) => {
            return {
                comment: val,
                user: userData.get(val.userId) ?? { id: "", avatarId: "", username: "" }
            }
        })
}

export async function getComment(commentId: number): Promise<Comment> {
    const res = await apiClient.get<CommentData>(`/api/public/comment/${commentId}`);
    return {
        comment: { ...res.data },
        user: { id: res.data.userId, username: "", avatarId: "" }
    };
}

export async function getCommentTree(postId: number, commentId: number): Promise<Comment[]> {
    const res = await apiClient.get<CommentData[]>(`/api/public/comment/${commentId}/tree`);
    if (!res.data[0])
        return [];
    const users = new Set<string>();
    res.data.forEach((val) => users.add(val.userId));
    const userData = await getUserInfo(Array.from(users));
    return res.data
        .filter(isTreeComment)
        .map((val) => {
            return {
                comment: val,
                user: userData.get(val.userId) ?? { id: "", avatarId: "", username: "" }
            }
        })
}

export async function createComment(createComment: CreateComment, file?: File): Promise<CommentCreateResponse> {
    const form = new FormData();
    form.append('comment',
        new Blob([JSON.stringify(createComment)], {
            type: 'application/json'
        }));
    if (file) form.append('attachment', file);
    return (await apiClient.post<CommentCreateResponse>('/api/user/comment', form)).data;
}

export async function likeComment(commentId: number) {
    return apiClient.post(`/api/user/comment/${commentId}/like`);
}

export async function unlikeComment(commentId: number) {
    return apiClient.delete(`/api/user/comment/${commentId}/like`);
}