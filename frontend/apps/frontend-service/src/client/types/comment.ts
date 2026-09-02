import { Comment } from "@services/user/userCommentApi";

export type CommentWithReplies = Comment & {replies: number[]}

export type CommentStore = {
    byId: Record<number, CommentWithReplies>; // plain structure with ids as key and 
    rootIds: number[];
}