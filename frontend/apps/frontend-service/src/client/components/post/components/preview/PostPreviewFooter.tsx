import { Box, Typography } from "@mui/material";
import CommentIcon from '@mui/icons-material/Comment';
import type { PreviewLikeSetterProp } from "../types";
import { useNavigate } from "react-router";
import { BlankLike, FilledLike } from "../../../Like";
// import FlagIcon from '@mui/icons-material/Flag';

type FooterProps = {
    id: number;
    likeCount: number;
    commentCount: number;
    isLiked?: boolean;
    publishedAt: string;
}

export const PostFooterPreview: React.FC<FooterProps & PreviewLikeSetterProp> = ({toggleLike, ...props }) => {
    const navigate = useNavigate();

    return (
        <Box display="flex" flexDirection="row" borderTop="1px solid" borderColor="divider" justifyContent='space-between'>
            <Box display="flex" flexDirection="row" gap="10px">
                <Box sx={{
                    display: 'flex', 
                    flexDirection:'row', 
                    alignItems: 'center', 
                    cursor: "pointer",
                    gap: '3px',
                    }} 
                    onClick={() => toggleLike(props.id)}>
                    {props.isLiked ?
                        <FilledLike />:
                        <BlankLike
                            sx={{
                                transition: 'transform 0.3s ease',
                                '&:hover':{
                                    transform: 'scale(1.2)'
                                }
                            }}/>
                    }
                    <Typography>{props.likeCount}</Typography>
                </Box>
                <Box 
                    sx={{
                        display: 'flex', 
                        flexDirection:'row', 
                        alignItems: 'center', 
                        cursor: "pointer",
                        gap: '3px'
                    }}
                    onClick={() => navigate(`/post/${props.id}?section=comments`)}
                >
                    <CommentIcon sx={{
                        transition: 'color 0.3s ease',
                        '&:hover':{
                            color: 'text.secondary'
                        }
                    }}
                    titleAccess="Comments"/>
                    <Typography>{props.commentCount}</Typography>
                </Box>
            </Box>
            <Box>
                <Typography variant="overline" color="text.secondary">{new Date(props.publishedAt).toLocaleString()}</Typography>
            </Box>
        </Box >
    )
}