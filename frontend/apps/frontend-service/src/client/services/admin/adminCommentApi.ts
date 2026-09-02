import { apiClient } from "@services/apiClient";

export async function deleteComment(postId: number, commentId: number) {
    return apiClient.delete('/comment', {
        params:{
            postId: postId,
            commentId: commentId
        }
    })
}