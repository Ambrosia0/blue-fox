import { useEffect, useRef, useState } from "react";
import {
    Card,
    CardContent,
    Typography,
    Box,
    Avatar as MuiAvatar,
    Skeleton,
    List,
    ListItem,
    ListItemAvatar,
    ListItemButton,
    ListItemText,
    Chip,
} from "@mui/material";
import GroupIcon from "@mui/icons-material/Group";
import { getCommunities, CommunityPreview } from "../../services/user/userCommunityApi";
import { useNavigate } from "react-router";
import { useTranslation } from "react-i18next";
import { Avatar } from "../user/Avatar";

const avatarPlaceholder = (
    <MuiAvatar sx={{ bgcolor: "primary.main", width: 56, height: 56 }}>
        <GroupIcon fontSize="large" />
    </MuiAvatar>
);

export const CommunityList = () => {
    const navigate = useNavigate();
    const { t } = useTranslation();

    const [communities, setCommunities] = useState<CommunityPreview[]>([]);
    const [loading, setLoading] = useState<boolean>(false);
    const [hasMore, setHasMore] = useState<boolean>(true);
    const loadingRef = useRef<boolean>(false);

    const fetchCommunities = async () => {
        if (loadingRef.current || !hasMore) return;

        try {
            loadingRef.current = true;
            setLoading(true);

            const lastCommunity = communities[communities.length - 1];
            const filter: any = {
                direction: "DESC",
            };

            if (lastCommunity) {
                filter.lastSeenId = lastCommunity.id;
                filter.lastSeenScore = lastCommunity.score;
            }

            const data = await getCommunities(filter);

            if (data.length === 0) {
                setHasMore(false);
                return;
            }

            setCommunities((prev) => [...prev, ...data]);
        } catch (error) {
            console.error("Failed to fetch communities:", error);
        } finally {
            setLoading(false);
        }
    };

    useEffect(() => {
        fetchCommunities();
    }, []);

    const handleCommunityClick = (slug: string) => {
        navigate(`/community/${slug}`);
    };

    return (
        <Box sx={{ p: 2 }}>
            <Typography variant="h4" gutterBottom sx={{ mb: 3, fontWeight: 600 }}>
                {t("community.title")}
            </Typography>

            <List sx={{ width: "100%" }}>
                {communities.map((community) => (
                    <ListItem
                        key={community.id}
                        disablePadding
                        sx={{ mb: 2, borderRadius: 2, overflow: "hidden" }}
                    >
                        <ListItemButton
                            onClick={() => handleCommunityClick(community.slug)}
                            sx={{
                                borderRadius: 2,
                                border: 1,
                                borderColor: "divider",
                                "&:hover": {
                                    bgcolor: "action.hover",
                                    transform: "translateX(4px)",
                                    transition: "all 0.2s ease-in-out",
                                },
                            }}
                        >
                            <ListItemAvatar sx={{ minWidth: 72, mr: 2 }}>
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
                            </ListItemAvatar>
                            <ListItemText
                                primary={
                                    <Typography variant="h6" fontWeight={600}>
                                        {community.displayedName}
                                    </Typography>
                                }
                                secondary={
                                    <Box sx={{ mt: 0.5 }}>
                                        <Box sx={{ display: "flex", alignItems: "center", mb: 1 }}>
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
                                }
                                slotProps={{
                                    secondary: {
                                        component: 'div'
                                    }
                                }}
                            />
                        </ListItemButton>
                    </ListItem>
                ))}
            </List>

            {loading && (
                <>
                    {[1, 2].map((i) => (
                        <Card key={`skeleton-${i}`} sx={{ mb: 2, borderRadius: 2 }}>
                            <CardContent sx={{ display: "flex", alignItems: "center", p: 2 }}>
                                <Skeleton variant="rectangular" width={56} height={56} sx={{ mr: 2, borderRadius: 2 }} />
                                <Box sx={{ flex: 1 }}>
                                    <Skeleton width="60%" height={24} />
                                    <Skeleton width="40%" height={20} sx={{ mt: 1 }} />
                                </Box>
                            </CardContent>
                        </Card>
                    ))}
                </>
            )}

            {!hasMore && communities.length > 0 && (
                <Typography variant="body2" color="text.secondary" align="center" sx={{ mt: 2 }}>
                    {t("community.noMore")}
                </Typography>
            )}

            {communities.length === 0 && !loading && (
                <Typography variant="body1" color="text.secondary" align="center" sx={{ mt: 4 }}>
                    {t("community.empty")}
                </Typography>
            )}
        </Box>
    );
};