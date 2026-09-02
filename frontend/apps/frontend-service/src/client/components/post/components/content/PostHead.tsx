import { Box, Typography } from "@mui/material"
import { Avatar } from "../../../user/Avatar";
import { PostDropdownMenu } from "../PostDropdownMenu";
import { useNavigate } from "react-router";

type HeadProps = {
    id: number;
    username: string;
    title: string;
    avatarId?: string;
    publishedAt: string;
}

export const PostHead: React.FC<HeadProps> = ({...props}) =>{
    const navigate = useNavigate();

    return (
        <Box display="flex" flexDirection="column" gap={2}>
            {/* Top meta */}
            <Box
            display="flex"
            justifyContent="space-between"
            alignItems="flex-start"
            >
            <Box display="flex" gap={1.5}>
                <Avatar {...props} />

                <Box>
                <Typography
                    color="text.secondary"
                    variant="caption"
                >
                    {new Date(props.publishedAt).toLocaleString()}
                </Typography>

                <Typography
                    fontWeight={500}
                    sx={{
                        cursor: 'pointer',
                        transition: 'color 0.2s ease',
                        '&:hover': {
                            color: 'text.secondary',
                        },
                    }}
                    variant="subtitle2"
                    onClick={() => navigate(`/profile/${props.username}`)}
                >
                    {props.username}
                </Typography>
                </Box>
            </Box>

            <PostDropdownMenu id={props.id} />
            </Box>

            {/* Title */}
            <Typography
                variant="h3"
                fontWeight={600}
                lineHeight={1.2}
                sx={{
                    wordBreak: 'break-word',
                }}
            >
            {props.title}
            </Typography>
        </Box>
    );
}