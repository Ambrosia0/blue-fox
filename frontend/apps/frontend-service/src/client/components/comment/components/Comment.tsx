import { Box, Typography } from "@mui/material"
import ReplyIcon from '@mui/icons-material/Reply';
import { ActionMenu } from "./ActionMenu";
import { useNavigate } from "react-router";
import { BlankLike, FilledLike } from "../../Like";
import { useAuth } from "../../../context/AuthContext";
import { Avatar } from "../../user/Avatar";

type CommentProps = {
    commentId: number;
    userId: string;
    username: string;
    content: string;
    likeCount: number;
    createdAt: string;
    isLiked?: boolean;
    attachmentUrl?: string;
    avatarId?: string;
    responseId: number;
    toggleLike: (id: number) => void;
    setResponseId: (id: number) => void;
}

export const Comment: React.FC<CommentProps> = ({toggleLike, setResponseId, ...props }) => {
    const navigate = useNavigate();
    const auth = useAuth();

    const handleResponseSet = (commentId: number) =>{
        if(commentId === props.responseId)
            setResponseId(null);
        else
            setResponseId(commentId);
    }

    return (
        <Box
            sx={{
                display: 'flex',
                flexDirection: 'column',
                gap: 1,
                py: 1.5,
                '&:hover .action': {
                    opacity: 1
                }
            }}
        >
            <Box 
                display="flex" 
                flexDirection="row" 
                gap={1.5}>
                <Avatar {...props} />

                <Box>
                    <Typography variant="caption" color="text.secondary">
                        {new Date(props.createdAt).toLocaleString()}
                    </Typography>

                    <Typography
                        variant="subtitle2"
                        sx={{
                            cursor: 'pointer',
                            transition: 'color 0.2s ease',
                            '&:hover': {
                                color: 'text.secondary',
                            },
                        }}
                        onClick={() => navigate(`/profile/${props.username}`)}
                    >
                        {props.username}
                    </Typography>
                </Box>
                {auth && 
                    <Box
                        className="action"
                        sx={{
                            cursor: 'pointer',  
                            color: 'text.secondary',
                            transition: 'color 0.2s ease, transform 0.15s ease, opacity 0.3s ease',
                            opacity: 0,
                            '&:hover': {
                                color: 'text.primary',
                                transform: 'scale(1.05)'
                            }
                        }}
                    >
                        <ReplyIcon onClick={() => handleResponseSet(props.commentId)}></ReplyIcon>
                    </Box>
                }
            </Box>
            <Typography lineHeight={1.5}
                sx={{
                    pl: 5.5,
                    wordBreak: 'break-word',
                }}
            >
                {props.content}
            </Typography>

            <Box sx={{
                    pl: 5.5,
                    display: 'flex',
                    alignItems: 'center',
                    gap: 2
            }}>
                <Box sx={{
                        display: 'flex',
                        alignItems: 'center',
                        gap: 0.5,
                        cursor: 'pointer',
                        color: 'text.secondary',
                        transition: 'color 0.2s ease, transform 0.15s ease',
                        '&:hover': {
                            color: 'text.primary',
                            transform: 'scale(1.05)',
                        },
                        '&:active .active':{
                            transform: 'scale(1.4)'
                        }
                    }}
                    onClick={() => toggleLike(props.commentId)}
                >
                    { (props.isLiked && <FilledLike />) ||
                        <BlankLike />
                    }

                    <Typography variant="body2">
                        {props.likeCount}
                    </Typography>
                </Box>

                <ActionMenu {...props} />
            </Box>
        </Box>
    );
}