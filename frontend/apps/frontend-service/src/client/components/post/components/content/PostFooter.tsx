import { Box, Fade, Typography } from "@mui/material"
import CommentIcon from '@mui/icons-material/Comment';
import type { LikeSetterProp } from "../types"
import { BlankLike, FilledLike } from "../../../Like";

type FooterProps = {
    id: number;
    likeCount: number;
    commentCount: number;
    isLiked?: boolean;
    publishedAt: string;
}

export const PostFooter: React.FC<FooterProps & LikeSetterProp> = ({toggleLike, ...props}) =>{
    return (
        <Box
            sx={{
                mt: 4,
                pt: 2,
                borderTop: '1px solid',
                borderColor: 'divider',
                display: 'flex',
                alignItems: 'center',
                justifyContent: 'space-between',
            }}
        >
            <Box display="flex" alignItems="center" gap={2}>
                <Box sx={{
                        display: 'flex',
                        alignItems: 'center',
                        gap: 0.5,
                            cursor: 'pointer',
                        px: 1.5,
                        py: 0.75,
                        borderRadius: 2,
                        transition: 'background-color 0.2s ease, transform 0.15s ease',
                        '&:hover': {
                            bgcolor: 'action.hover',
                            transform: 'translateY(-1px)',
                        },
                    }}
                    onClick={() => toggleLike()}>
                    {props.isLiked?
                        <FilledLike fontSize="medium" />: 
                        <BlankLike fontSize="medium"/>}
                    <Fade timeout={1500} in>
                        <Typography fontWeight={500}>{props.likeCount}</Typography>
                    </Fade>
                </Box>
                <Box
                    onClick={
                        () =>{
                            const el = document.getElementById("comment-section");
                            el?.scrollIntoView({behavior: 'smooth'});
                        }
                    }
                    sx={{
                        display: 'flex',
                        alignItems: 'center',
                        gap: 0.5,
                        cursor: 'pointer',
                        px: 1.5,
                        py: 0.75,
                        borderRadius: 2,
                        transition: 'background-color 0.2s ease',
                        '&:hover': {
                            bgcolor: 'action.hover',
                        },
                    }}>
                    <CommentIcon />
                    <Fade timeout={1500} in>
                        <Typography fontWeight={500}>{props.commentCount}</Typography>
                    </Fade>
                </Box>
            </Box>

            <Typography variant="body2" color="text.secondary">
                {new Date(props.publishedAt).toLocaleDateString()}
            </Typography>
        </Box>
    );
}