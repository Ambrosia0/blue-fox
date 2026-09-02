import { Box, Fade } from "@mui/material"
import { useEffect, useState } from "react";
import { useInView } from "react-intersection-observer";
import { CommentWriter } from "./components/CommentWriter";
import { Comment, getCommentTree, getRootCommentsForPost, likeComment, SortField, unlikeComment } from "../../services/user/userCommentApi";
import { CommentNotification, NotificationType, useNotification } from "../../context/NotificationEvent";
import { CommentTree } from "./components/CommentTree";
import { CommentStore, CommentWithReplies } from "../../types/comment";
import { TransitionGroup } from "react-transition-group";
import { enqueueSnackbar } from "notistack";
import { useAuth } from "../../context/AuthContext";

type CommentContaierProps = {
    postId: number;
}

export const CommentContainer: React.FC<CommentContaierProps> = ({...props}) =>{
    const auth = useAuth();
    const attributes = auth.user?.profile["attributes"];
    const [responseId, setReponseId] = useState<number | null>(null);
    const [sort, setSort] = useState<SortField>("HOT");
    const notification = useNotification();

    // flat tree, where byId contains all comment entities, rootIds contains root comments ids
    const [comments, setComments] = useState<CommentStore>( 
        {byId: {}, rootIds:[]}
    );

    const {ref, inView} = useInView({
        triggerOnce: true
    });

    async function getComments() {
        try {
            const last = comments.byId[comments.rootIds[comments.rootIds.length - 1]];
            const modyfing = (
                await getRootCommentsForPost(
                    props.postId, 
                    comments.rootIds.length === 0?
                        {}:
                        {
                            lastSeenCount: last.comment.numberOfChildren,
                            lastSeenId: last.comment.commentId,
                            lastSeenInstant: last.comment.createdAt
                        }
                ))
                .reduce<CommentStore>((acc, curr) => {
                    acc.byId[curr.comment.commentId] = {
                        ...curr,
                        replies: []
                    };
                    acc.rootIds = [...acc.rootIds, curr.comment.commentId];
                    return acc;
                }, {byId: {...comments.byId}, rootIds: [...comments.rootIds]})
            setComments(modyfing);
        } catch (error) {
            console.log(error);
        }    
    }

    async function loadTree(commentId: number) {
        try {
            const data = (await getCommentTree(props.postId, commentId))
                .reduce<CommentStore>((acc, curr) =>{
                    acc.byId[curr.comment.commentId] = {
                        ...curr, 
                        replies: []
                    }
                    const parent = acc.byId[curr.comment.parentComment!];
                    if(parent)
                        parent.replies = [...parent.replies, curr.comment.commentId];
                    return acc;
                }, {byId: {...comments.byId}, rootIds: [...comments.rootIds]})
            setComments(data);
        } catch (error) {
            console.log(error);
        }
    }
    
    async function toggleLike(commentId: number) {
        if(!auth.user)
            await auth.login();
        const prevCommentState = comments.byId[commentId];
        const isCurrentlyLiked = prevCommentState.comment.isLiked;
        setComments(prev =>{
            return {
                ...prev,
                byId: {
                    ...prev.byId,
                    [commentId]: {
                        ...prev.byId[commentId],
                        comment: {
                            ...prev.byId[commentId].comment,
                            likeCount: isCurrentlyLiked?
                                prev.byId[commentId].comment.likeCount - 1:
                                prev.byId[commentId].comment.likeCount + 1,
                            isLiked: !isCurrentlyLiked
                        }
                    }
                }    
            }
        })
        try {
            isCurrentlyLiked?
                await unlikeComment(commentId):
                await likeComment(commentId);
        } catch (error) {
            setComments(prev =>({
                ...prev,
                byId: {
                    ...prev.byId,
                    [commentId]: prevCommentState
                }
            }));
        }
    }

    const handleCommentCreation = (comment: Comment) =>{
        const parent = comment.comment.parentComment;
        const parentComment = comments.byId[parent];
        setComments(prev => ({
            rootIds:[
                ...(parent? []: [comment.comment.commentId]),
                ...prev.rootIds
            ],
            byId: {
                ...prev.byId,
                ...(parent? 
                    {
                        [parent]:{
                            ...parentComment,
                            comment: {
                                ...parentComment.comment,
                                numberOfChildren: parentComment.comment.numberOfChildren + 1
                            },
                            replies: 
                                parentComment.replies.length !== 0? 
                                [comment.comment.commentId, ...parentComment.replies]:
                                parentComment.comment.numberOfChildren === 0? 
                                    [comment.comment.commentId]:
                                    []
                        }
                    }:
                    {}
                ),
                [comment.comment.commentId]: {
                    ...comment,
                    replies: [],
                }
            }
        }))
        setReponseId(null);
        notification.eventTarget.dispatchEvent(new MessageEvent(NotificationType.CommentCreation));
        enqueueSnackbar("Comment created!", {variant: "success"})
    }

    const handleSortChange = (sort: SortField) =>{

    }
    
    useEffect(() =>{
        getComments();
        notification?.eventTarget.addEventListener(NotificationType.CommentNotification, (event) =>{
            const notificationData = ((event as MessageEvent).data);
            const commentData = (notificationData as CommentNotification);
            setComments(prev => {
                const next = {
                    byId: {...prev.byId},
                    rootIds: [...prev.rootIds]
                }
                const transformedComment: CommentWithReplies = {
                    comment: {
                        commentId: commentData.id,
                        likeCount: 0,
                        numberOfChildren: 0,
                        isLiked: false,
                        ...commentData
                    },
                    user: commentData.user,
                    replies: []
                }
                // if received comment is not root
                if(commentData.parentComment && prev.byId[commentData.parentComment]){
                    // if parent doesn't have children
                    if(prev.byId[commentData.parentComment].comment.numberOfChildren === 0){
                        next.byId[commentData.id] = transformedComment;
                        next.byId[commentData.parentComment].replies = 
                            [...next.byId[commentData.parentComment].replies, commentData.id];
                        let curr = transformedComment;
                        while(curr.comment.parentComment != undefined){
                            const parent = next.byId[curr.comment.parentComment];
                            parent.comment.numberOfChildren++;
                            curr = parent;
                        }
                    }else{ // if has children
                        next.byId[commentData.parentComment].comment.numberOfChildren++;
                    }
                }
                else if(!commentData.parentComment){ // if root
                    next.byId[commentData.id] = transformedComment;
                    next.rootIds = [...next.rootIds, commentData.id];
                }
                return next;
            })
        });
    }, [inView])

    // create writer on specific comment when choosing to reply
    return(
        <Box id="comment-section" ref={ref}>
            {auth.user &&
                <CommentWriter 
                    responseId={null}
                    postId={props.postId}
                    addComment={handleCommentCreation}
                />
            }
            <TransitionGroup>
                {comments.rootIds && comments.rootIds.map((comment) =>
                    <Fade key={comment} timeout={1500} in>
                        <Box>
                            <CommentTree // children elements are rendered in single collapse
                                setResponseId={setReponseId}
                                responseId={responseId}
                                addComment={handleCommentCreation}
                                toggleLike={toggleLike}
                                loadTree={loadTree}
                                tree={comments.byId}
                                nodeId={comment}
                            />
                        </Box>
                    </Fade>
                )}
            </TransitionGroup>
        </Box>
    )
}