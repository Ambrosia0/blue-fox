import { Box, Typography } from "@mui/material";
import { PostDropdownMenu } from "../PostDropdownMenu";
import { useNavigate } from "react-router";
import { Avatar } from "../../../user/Avatar";

type HeadProps = {
    id: number;
    username: string;
    userId: string;
    title: string;
    avatarId?: string;
    publishedAt: string;
    deleteCallback?: (postId: number) => void;
}

export const PostHeadPreview: React.FC<HeadProps> = ({ 
    ...props 
}) => {
    const navigate = useNavigate();

    return (
        <Box display="flex" flexDirection="column">
            <Box display="flex" gap={5} justifyContent="space-between">
                <Box display="flex" gap={1}>
                    <Avatar {...props} />
                    <Box>
                        <Typography
                            variant="caption"
                            color="text.secondary">
                            {new Date(props.publishedAt).toLocaleString()}
                        </Typography>
                        <Typography 
                            variant="subtitle2"
                            sx={{
                                cursor: 'pointer',
                                transition: 'color 0.3s ease',
                                '&:hover':{
                                    color: 'text.secondary'
                                }
                            }} 
                            onClick={() => navigate(`/profile/${props.username}`)} 
                            fontWeight={500}>
                            {props.username.length !== 0? props.username: props.userId}
                        </Typography>
                    </Box>  
                </Box>

                <Box>
                    <PostDropdownMenu {...props}/>
                </Box>
            </Box>
            <Box>
                <Typography 
                    variant="h4" sx={{ cursor: "pointer" }} 
                    onClick={() => navigate(`/post/${props.id}`)}>
                        {props.title}
                </Typography>
            </Box>
        </Box>
    )
}