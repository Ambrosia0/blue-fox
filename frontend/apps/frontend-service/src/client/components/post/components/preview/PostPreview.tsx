import { Box } from "@mui/material"
import { PostHeadPreview } from "./PostPreviewHead"
import PostBody from "../content/PostBody"
import { PostFooterPreview } from "./PostPreviewFooter"
import type { PreviewLikeSetterProp } from "../types"
import { forwardRef } from "react"
import { isViewPost, Post } from "@services/user/userPostApi"

type PostPreviewProps = Post & PreviewLikeSetterProp & {
    deleteCallback?: (postId: number) => void;
}

export const PostPreview = forwardRef<HTMLDivElement, PostPreviewProps>(
    (props, ref) =>{
        return(
            <Box
                ref={ref}
                key={props.post.id}
                display="flex"
                flexDirection="column"
                justifySelf="center"
                sx={{
                    minWidth: {
                        sm: 360,
                        md: 480,
                        lg: 600,
                    },
                    justifyContent: 'center',
                    p: 2,
                    m: 2,
                    borderRadius: 2,
                    transition: 'background-color 0.3s ease',
                    '&:hover': {
                        backgroundColor: 'action.hover'
                    },
                    overflow: 'hidden',
                }}
            >
                <PostHeadPreview userId={props.user.id} {...props.user} {...props.post} />
                <PostBody doc={isViewPost(props.post)? props.post.preview: props.post.content} />
                <PostFooterPreview {...props.post} commentCount={props.post.commentCount} toggleLike={props.toggleLike} />
            </Box>
        )
    }
);