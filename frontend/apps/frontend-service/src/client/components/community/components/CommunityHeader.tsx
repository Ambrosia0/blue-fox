import { Box, Button, Paper, Stack, Typography } from "@mui/material";
import { useTranslation } from "react-i18next";
import { Avatar } from "../../user/Avatar";

type CommunityHeaderProps = {
    avatarId?: string,
    displayedName: string,
    slug: string,
    followCount: number,
    createdAt: string,
    isPrivate: boolean
}

export const CommunityHeader: React.FC<CommunityHeaderProps> = ({
    avatarId, 
    slug, 
    displayedName,
    followCount,
    createdAt,
    isPrivate
}) =>{
    const { t } = useTranslation();
    return(
        <Paper elevation={24} sx={{borderRadius: 3}}>
            {/* Banner */}
            <Box
                sx={{
                    mt: 3,
                    height: 140,
                    borderRadius: 3,
                    background:
                        "linear-gradient(135deg, #3f71a3 0%, #35668f 100%)",
                }}
            />

            {/* Header */}
            <Box
                sx={{
                    display: "flex",
                    justifyContent: "space-between",
                    alignItems: {
                        xs: "flex-start",
                        md: "center",
                    },
                    flexDirection: {
                        xs: "column",
                        md: "row",
                    },
                    gap: 3,
                    mt: -5,
                    px: 2,
                }}
            >
                <Stack direction="row" spacing={3}>
                    <Avatar
                        avatarId={avatarId}
                        name={slug}
                        baseProps={{
                            sx: {
                                width: 80,
                                height: 80,
                                border: "4px solid",
                                borderColor: "background.default",
                                boxShadow: 2,
                            },
                        }}
                    />

                    <Box>
                        <Typography
                            variant="body2"
                            display="inline-flex"
                            fontWeight={700} 
                            sx={{
                                color: "text.secondary",
                                bgcolor: "action.hover",
                                borderRadius: 10,
                                px: 1,
                                py: 0.25,
                                transition: 'color 0.5s',
                                "&:hover": {
                                    color: 'text.primary',
                                    cursor: 'pointer',
                                    bgcolor: "action.selected"
                                }
                            }}    
                        >
                            
                            {`@${slug}`}
                        </Typography>
                        <Typography variant="h4" fontWeight={700}>
                            {displayedName}
                        </Typography>

                        <Typography
                            variant="body2"
                            color="text.secondary"
                            sx={{ mt: .5 }}
                        >
                            {followCount.toLocaleString()}{" "}
                            {t("community.followers")}
                            {" • "}
                            {new Date(createdAt).toLocaleDateString()}
                            {isPrivate &&
                                ` • ${t("community.private")}`}
                        </Typography>
                    </Box>
                </Stack>

                <Button
                    variant="contained"
                    size="large"
                >
                    {t("community.join")}
                </Button>
            </Box>
        </Paper>
    );
}