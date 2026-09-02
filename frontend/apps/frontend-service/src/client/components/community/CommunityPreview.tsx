import { Box, Typography, Card, CardContent, Chip, Avatar as MuiAvatar } from "@mui/material";
import GroupIcon from "@mui/icons-material/Group";
import { CommunityPreview as CommunityPreviewType } from "@services/user/userCommunityApi";
import { useNavigate } from "react-router";
import { useTranslation } from "react-i18next";
import { Avatar } from "../user/Avatar";

interface CommunityPreviewProps {
    community: CommunityPreviewType;
}

export const CommunityPreview: React.FC<CommunityPreviewProps> = ({ community }) => {
    const navigate = useNavigate();
    const { t } = useTranslation();

    const handleClick = () => {
        navigate(`/community/${community.slug}`);
    };

    return (
        <Card
            sx={{
                mb: 2,
                borderRadius: 2,
                overflow: "hidden",
                cursor: "pointer",
                transition: "all 0.2s ease-in-out",
                "&:hover": {
                    bgcolor: "action.hover",
                    transform: "translateX(4px)",
                    boxShadow: "0 4px 12px rgba(0,0,0,0.1)",
                },
            }}
            onClick={handleClick}
        >
            <CardContent sx={{ display: "flex", alignItems: "center", p: 2, py: 1.5 }}>
                <Box sx={{ minWidth: 72, mr: 2 }}>
                    <Avatar
                        avatarId={community.avatarId}
                        baseProps={{
                            sx: { 
                                width: 56, 
                                height: 56,
                                bgcolor: "primary.main"
                            },
                            alt: community.slug
                        }}
                        iconProps={{
                            fontSize: 'large'
                        }}
                    />
                </Box>
                <Box sx={{ flex: 1 }}>
                    <Typography variant="h6" fontWeight={600} gutterBottom>
                        {community.displayedName}
                    </Typography>
                    <Box sx={{ display: "flex", alignItems: "center", mb: 0.5 }}>
                        <GroupIcon fontSize="small" sx={{ mr: 0.5, fontSize: 16 }} />
                        <Typography variant="body2" color="text.secondary">
                            {community.followCount ?? 0} {t("community.followers")}
                        </Typography>
                    </Box>
                    {community.tags && community.tags.length > 0 && (
                        <Box sx={{ display: "flex", flexWrap: "wrap", gap: 0.5 }}>
                            {community.tags.slice(0, 3).map((tag) => (
                                <Chip
                                    key={tag}
                                    label={tag}
                                    size="small"
                                    variant="outlined"
                                    sx={{ fontSize: "0.7rem", height: 20 }}
                                />
                            ))}
                        </Box>
                    )}
                </Box>
            </CardContent>
        </Card>
    );
};