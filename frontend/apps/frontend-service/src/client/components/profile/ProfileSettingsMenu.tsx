import { Box, Container, Tab, Tabs, Typography, Switch, FormControlLabel, Divider, Alert, Snackbar, LinearProgress, Button, Paper, List, ListItemButton, ListItemText } from "@mui/material"
import { useState, useCallback, useEffect } from "react"
import { useTranslation } from "react-i18next"
import { updateProfileSettings, CurrentUserProfile, ProfileSettings } from "@services/user/userProfileApi"
import { useLoaderData } from "react-router"
import { enqueueSnackbar, useSnackbar } from "notistack"

type SettingsTab = "general" | "privacy" | "notifications";

export const ProfileSettingsMenu = () => {
    const { t } = useTranslation();

    const user = useLoaderData<CurrentUserProfile>();
    const [activeTab, setActiveTab] = useState<SettingsTab>("general");

    const [isDirty, setIsDirty] = useState(false);
    const [saving, setSaving] = useState(false);

    const [settings, setSettings] = useState<ProfileSettings>(user.settings);

    const handleSaveSettings = async () => {
        setSaving(true);

        try {
            await updateProfileSettings(settings);
            enqueueSnackbar({
                variant: 'success',
                message: t("settings.messages.success")
            });
        } catch {
            enqueueSnackbar({
                variant: 'error',
                message: t("settings.messages.error")
            })
        } finally {
            setSaving(false);
        }
    };

    const Header = ({text}: {text: string}) => 
        <Box>
            <Box
                sx={{
                    display: "flex",
                    alignItems: "center",
                    justifyContent: "space-between",
                    mb: 2,
                }}
            >
                <Typography variant="h6">
                    {text}
                </Typography>

                <Button
                    variant="contained"
                    disabled={!isDirty || saving}
                    onClick={handleSaveSettings}
                >
                    {t("settings.save")}
                </Button>
            </Box>
        </Box>

    const renderContent = () => {
        switch (activeTab) {
            case "general":
                return (
                    <>
                        <Header text={t("settings.general.title")}/>

                        <Typography
                            variant="body2"
                            color="text.secondary"
                        >
                            {t("settings.general.language")}
                        </Typography>
                        <Divider sx={{ my: 2 }} />
                    </>
                );

            case "privacy":
                return (
                    <>
                        <Header text={t("settings.privacy.title")}/>

                        <Typography
                            variant="body2"
                            color="text.secondary"
                        >
                            {t("settings.privacy.description")}
                        </Typography>
                        
                        <Divider sx={{ my: 2 }} />
                        
                        <FormControlLabel
                            control={
                                <Switch
                                    checked={settings.displayEmail}
                                    onChange={(e) =>{
                                        setIsDirty(true);
                                        setSettings(prev => ({...prev, displayEmail: e.target.checked}));
                                    }}
                                />
                            }
                            label={t(
                                "settings.privacy.displayEmail"
                            )}
                        />
                        <FormControlLabel
                            control={
                                <Switch
                                    checked={settings.displayActivity}
                                    onChange={(e) =>{
                                        setIsDirty(true);
                                        setSettings(prev => ({...prev, displayActivity: e.target.checked}));
                                    }}
                                />
                            }
                            label={t(
                                "settings.privacy.displayActivity"
                            )}
                        />
                    </>
                );

            case "notifications":
                return (
                    <>
                        <Header  text={t("settings.notifications.title")}/>

                        <Typography
                            variant="body2"
                            color="text.secondary"
                        >
                            {t("settings.notifications.description")}
                        </Typography>
                        <Divider sx={{ my: 2 }} />
                    </>
                );
        }
    };

    return (
        <Container maxWidth="lg">
            <Box sx={{ display: "flex"}}>
                <List
                    sx={{
                        width: 240,
                        borderRight: 1,
                        borderColor: "divider",
                    }}
                >
                    <ListItemButton
                        selected={activeTab === "general"}
                        onClick={() => setActiveTab("general")}
                    >
                        <ListItemText primary={t("settings.general.title")} />
                    </ListItemButton>

                    <ListItemButton
                        selected={activeTab === "privacy"}
                        onClick={() => setActiveTab("privacy")}
                    >
                        <ListItemText primary={t("settings.privacy.title")} />
                    </ListItemButton>

                    <ListItemButton
                        selected={activeTab === "notifications"}
                        onClick={() => setActiveTab("notifications")}
                    >
                        <ListItemText primary={t("settings.notifications.title")} />
                    </ListItemButton>
                </List>
                <Box sx={{
                    p: 3,
                    minHeight: 300,
                    flex: 1,
                    display: 'flex',
                    flexDirection: 'column'
                }}>
                    {renderContent()}
                </Box>
            </Box>
        </Container>
    );
};