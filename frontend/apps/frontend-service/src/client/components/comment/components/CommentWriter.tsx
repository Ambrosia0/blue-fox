import { Box, Button, CircularProgress, IconButton, TextField, Typography } from "@mui/material"
import type React from "react"
import { useState } from "react"
import AttachFileIcon from '@mui/icons-material/AttachFile';
import SendIcon from '@mui/icons-material/Send';
import { Comment, createComment } from "../../../services/user/userCommentApi"
import { useAuth } from "../../../context/AuthContext";
import { Avatar } from "../../user/Avatar";

type WriterProps = {
    postId: number;
    responseId: number | null;
    addComment: (comment: Comment) => void;
    setResponseId?: (commentId: number) => void;
}

export const CommentWriter: React.FC<WriterProps> = ({setResponseId, addComment, ...props }) => {
    const [commentText, setCommentText] = useState<string>("");
    const auth = useAuth();
    const [isLocked, setIsLocked] = useState<boolean>(false);

    async function sendComment() {
        try {
            setIsLocked(true);
            const resp = await createComment(
                props.responseId? 
                {
                    postId: props.postId, 
                    content: commentText, 
                    parentComment: props.responseId
                }:
                {
                    postId: props.postId, 
                    content: commentText
                }
            );
            addComment({
                comment:{
                    ...resp,
                    likeCount: 0,
                    numberOfChildren: 0,
                    score: 0
                },
                user: {
                    id: auth.user.profile.sub,
                    username: auth.user.profile.preferred_username,
                    avatarId: (auth.user.profile["attributes"] as Record<string, string>)?.["avatarId"]?.[0]
                }
            });
            setCommentText("");
            setResponseId?.(null);
        } catch (error) {
            console.log(error);
        } finally{
            setIsLocked(false)
        }
    }

    return (
        <Box sx={{
            mt: {
                md: 1,
                xl: 2
            },
            ml: {
                md: 2,
                xl: 4
            },
            display: "flex",
            gap: 1.5,
            alignItems: "flex-start",
            width: "100%",
        }}
        >
            <Avatar
                username={auth.user.profile.preferred_username}
                avatarId={(auth.user.profile["attributes"] as Record<string, string>)?.["avatarId"]?.[0]}
                baseProps={{
                    sx: {
                        width: 36,
                        height: 36,
                        mt: "2px",
                        cursor: 'pointer'
                    },
                }}
            />

            <Box sx={{
                flex: 1,
                maxWidth: 600,
                border: "1px solid",
                borderColor: "divider",
                borderRadius: 2,
                px: 1.5,
                py: 1,
                transition: "border-color 0.2s ease, box-shadow 0.2s ease",
                "&:focus-within": {
                    borderColor: "primary.main",
                    boxShadow: theme =>
                        `0 0 0 2px ${theme.palette.primary.main}20`,
                },
            }}>
                {props.responseId && (
                    <Typography
                        variant="caption"
                        color="text.secondary"
                        sx={{
                            mb: 0.5,
                            display: "block"
                        }}
                    >
                        Replying to comment
                    </Typography>
                )}

                <TextField
                    multiline
                    minRows={2}
                    maxRows={6}
                    placeholder="Write a comment…"
                    variant="standard"
                    fullWidth
                    slotProps={{
                        input: {
                            disableUnderline: true,
                            sx: {
                                fontSize: "clamp(0.75rem, 1vw, 1rem)",
                            },
                        }
                    }}
                    value={commentText}
                    onChange={(e) => setCommentText(e.target.value)}
                />

                <Box
                    sx={{
                        display: "flex",
                        justifyContent: "space-between",
                        alignItems: "center",
                        mt: 0.5,
                    }}
                >
                    <IconButton size="small">
                        <AttachFileIcon fontSize="small" />
                    </IconButton>

                    <Button
                        size="small"
                        variant="contained"
                        endIcon={(!isLocked && <SendIcon />) || <CircularProgress size="24px" aria-label="Loading..." />}
                        onClick={() => sendComment()}
                        sx={{
                            textTransform: "none",
                            borderRadius: 999,
                            px: 2,
                        }}
                    >
                        Send
                    </Button>
                </Box>
            </Box>
        </Box>
    )
}