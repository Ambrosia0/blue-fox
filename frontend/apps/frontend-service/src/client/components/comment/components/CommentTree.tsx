import { Box, BoxProps, Typography } from "@mui/material"
import React, { forwardRef, useEffect, useRef, useState } from "react";
import { Comment } from "./Comment";
import { CommentWithReplies } from "../../../types/comment";
import { CommentWriter } from "./CommentWriter";
import { Comment as CommentEntity } from "@services/user/userCommentApi";

type CommentTreeProps = {
    responseId: number;
    nodeId: number;
    tree: Record<number, CommentWithReplies>;
    depth?: number;
    hidden?: boolean;
    collapse?: () => void;
    toggleLike: (id: number) => void;
    addComment: (comment: CommentEntity) => void;
    loadTree: (id: number) => void;
    setResponseId: (id: number) => void;
};

export const Divider = (props: BoxProps) =>
<Box 
    sx={{
        width: 32,
        borderLeft: "2px solid",
        borderColor: "action.hover",
        cursor: "pointer",
        transition: "all 0.2s",
        "&:hover": {
            borderColor: "primary.main",
            borderLeftWidth: "3px",
        },
    }}
    {...props}
/>


// recursive comment tree
const CommentTreeComponent = forwardRef<HTMLDivElement, CommentTreeProps>(({nodeId, tree, collapse, hidden = true, ...props}, ref) =>{
    const [isHidden, setIsHidden] = useState<boolean>(hidden);
    const hasChildren = tree[nodeId].comment.numberOfChildren > 0;
    const isPending = tree[nodeId].replies.length === 0 && hasChildren;

    const handleVisibility = (hidden?: boolean) =>{
        if(hidden === undefined)
            setIsHidden(true);
        else
            setIsHidden(hidden);
    }

    return(
        <Box ref={ref} sx={{display: 'flex', flexDirection: 'column'}}>
            <Comment 
                {...tree[nodeId].comment} 
                {...tree[nodeId].user}
                responseId={props.responseId}
                userId={tree[nodeId].user.id}
                setResponseId={props.setResponseId} 
                toggleLike={props.toggleLike}
            />
            {hasChildren && isHidden &&
                <Box sx={{
                    width: 'fit-content',
                    mt: "5px",
                    p: "5px",
                    borderRadius: "10%",
                    transition: 'background-color 0.3s ease',
                    cursor: 'pointer',
                    '&:hover':{
                        bgcolor: 'action.hover'
                    }
                }}>
                    <Typography
                        onClick={() => {
                            if(isPending)
                                props.loadTree(nodeId);
                            handleVisibility(false);
                        }}>
                        {`Show responses (${tree[nodeId].comment.numberOfChildren})`}
                    </Typography>
                </Box>
            }
            {props.responseId === tree[nodeId].comment.commentId && 
                <CommentWriter
                    postId={tree[nodeId].comment.postId}
                    responseId={tree[nodeId].comment.commentId}
                    addComment={props.addComment}
                    setResponseId={props.setResponseId}
                />
            }
            {hasChildren && tree[nodeId].replies.length !== 0 && !isHidden &&
                <Box display="flex" flexDirection="row">
                    <Divider onClick={() => handleVisibility(true)} />
                    <Box display="flex" flexDirection="column">
                        {
                            tree[nodeId].replies.map(reply =>
                                <CommentTreeComponent 
                                    nodeId={reply}
                                    tree={tree}
                                    collapse={handleVisibility}
                                    hidden={false}
                                    {...props}
                                />
                            )
                        }
                    </Box>
                </Box>
            }
        </Box>
    )
})


export const CommentTree = React.memo(CommentTreeComponent);