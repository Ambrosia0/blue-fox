import { Box } from "@mui/material"
import { PostHead } from "./content/PostHead"
import PostBody from "./content/PostBody"
import { PostFooter } from "./content/PostFooter"
import { CommentContainer } from "../../comment/CommentContainer"
import type { LikeSetterProp } from "./types"
import { isViewPost, type Post as PostType } from "../../../services/user/userPostApi"

export const Post: React.FC<PostType & LikeSetterProp> = ({...props}) =>{
    return(
        <Box>
            <Box mb={2}>
                <PostHead {...props.post} username={props.user.username} />
                <PostBody doc={isViewPost(props.post)? props.post.preview: props.post.content}/>
                <PostFooter {...props.post} {...props}/>
            </Box>
            <CommentContainer postId={props.post.id}/>
        </Box>
    )
}