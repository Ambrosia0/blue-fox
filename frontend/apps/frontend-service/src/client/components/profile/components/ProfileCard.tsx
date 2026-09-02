import { Box, Card, Typography } from "@mui/material"
import { ProfileDropdownMenu } from "./ProfileDropdownMenu";
import { CurrentUserProfile, PublicUserProfile } from "@services/user/userProfileApi";
import { useTranslation } from "react-i18next";
import { Avatar } from "../../user/Avatar";

type ProfileCardProps = (PublicUserProfile | CurrentUserProfile) & 
    {isUserProfile: boolean} 

export const ProfileCard: React.FC<ProfileCardProps> = ({ ...props }) => {
    const {t} = useTranslation();
    return (
        <Card
            elevation={2}
            sx={{
                p: 3,
                display: 'flex',
                flexDirection: 'column',
                alignItems: 'center',
                gap: 2,
                '&:hover .actions': {
                    opacity: 1
                }
            }}>

            {/* <ProfileDropdownMenu {...props} /> */}
            <Box
                display="flex"
                flexDirection="row">
                <Avatar {...props}
                    baseProps={{
                        sx: {
                            width: 120,
                            height: 120,
                            fontSize: 36,
                        },
                    }} />
                <Box className="actions"
                    sx={{
                        position: "absolute",
                        right: {xs: 8, sm: 16},
                        top: {xs: 8, sm: 12},
                        opacity: 0,
                        transition: "opacity 0.3s ease"
                    }}>
                {!props.isUserProfile && <ProfileDropdownMenu {...props} />}
                </Box>
            </Box>

            <Typography variant="h6" fontWeight={600}>
                {props.username}
            </Typography>

            <Box width="100%">
                <Typography variant="caption" color="text.secondary" display="block">{t('profile.about')}</Typography>
                <Typography fontSize={14}>{props.about || t('profile.noDescription')}</Typography>
            </Box>

            <Box width="100%">
                <Typography variant="caption" color="text.secondary" display="block">
                    {t('profile.registered')}
                </Typography>
                <Typography fontSize={13}>{new Date(props.createdAt).toLocaleDateString()}</Typography>
            </Box>
        </Card>
    )
}