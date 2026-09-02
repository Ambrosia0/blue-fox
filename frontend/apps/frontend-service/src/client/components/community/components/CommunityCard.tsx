import { Box, Button, Chip, Divider, Paper, Typography } from "@mui/material";
import { Avatar } from "../../user/Avatar";
import { useTranslation } from "react-i18next";

type CommunityCardProps = {
    name: string;
    avatadId?: string;
    tags: string[];
    followCount: number;
    isFollowed: boolean;
    postCount: number;
    description: string;
}

export const CommunityCard: React.FC<CommunityCardProps> = ({
    ...props
}) =>{
    const { t } = useTranslation();
    
    return(
        <Box display="flex" flexDirection="column">
            <Box
                sx={{
                    height: 220,
                    background: "linear-gradient(135deg, #667eea, #764ba2)",
                    borderRadius: 2
                }}
            />
            <Box>
                <Box 
                    display="flex" 
                    flexDirection="row" 
                    justifyContent="space-between" 
                    alignItems="center"
                    mt={-5}
                >
                    <Avatar />
                    <Button variant={props.isFollowed? "outlined": "contained"}>{props.isFollowed? t("community.join"): "Follow"}</Button>
                </Box>
                <Box>
                    <Typography>{props.description}</Typography>
                </Box>
                <Paper>
                    <Box display="flex" flexDirection="row">
                        <Box display="flex" flexDirection="column" flexGrow={1} flexShrink={1}>
                            <Typography>{props.followCount}</Typography>
                            <Typography>Followers</Typography>
                        </Box>
                        <Divider />
                        <Box display="flex" flexDirection="column" flexGrow={1} flexShrink={1}>
                            <Typography>{props.postCount}</Typography>
                            <Typography>Posts</Typography>
                        </Box>
                    </Box>
                </Paper>
                <Box display="flex" flexDirection="row">
                    {props.tags.map((tag, _) =>{
                        return(
                            <Chip label={tag} color="primary" />
                        )
                    })}
                </Box>
            </Box>
        </Box>
    )
}