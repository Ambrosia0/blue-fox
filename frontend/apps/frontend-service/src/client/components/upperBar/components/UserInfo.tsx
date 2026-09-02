import { Box, Button, Divider, IconButton, ListItemIcon, Menu, MenuItem, Typography } from '@mui/material';
import { useNavigate } from 'react-router';
import LogoutIcon from '@mui/icons-material/Logout';
import LoginIcon from '@mui/icons-material/Login';
import { useClient } from '../../../context/ClientContext';
import { useTranslation } from 'react-i18next';
import { useAuth } from '../../../context/AuthContext';
import { useRef, useState } from 'react';
import AccountCircleIcon from '@mui/icons-material/AccountCircle';
import PeopleIcon from '@mui/icons-material/People';
import SettingsIcon from '@mui/icons-material/Settings';
import ForumIcon from '@mui/icons-material/Forum';
import AddIcon from '@mui/icons-material/Add';
import { Avatar } from '../../user/Avatar';
import { CommunityCreateDialog } from '../../community/CommunityCreateDialog';


export const UserInfo = () => {
    const isClient = useClient();
    const auth = useAuth();
    const attributes = auth?.user?.profile["attributes"] as Record<string, string>;
    const username = auth?.user?.profile.preferred_username;
    const avatarId = attributes?.["avatarId"]?.[0];
    const navigate = useNavigate();
    const { t } = useTranslation();

    const ref = useRef(null);

    const [isOpen, setIsOpen] = useState<boolean>(false);

    const [isCommunityCreateOpen, setIsCommunityCreateOpen] = useState<boolean>(false);

    const handleLogin = () =>{
        if(typeof window !== 'undefined' && auth)
            auth.login();
    }

    const LoginButton = ({callback}: {callback: () => void}) =>{
        return(
            <Box>
                <Button aria-label="Login" onClick={() => callback()} 
                    size='large'
                    variant='text'
                    startIcon={
                        <LoginIcon fontSize='small' />
                    }>
                    {t("login")}
                </Button>
            </Box>
        )
    }

    if(!isClient || !auth.user)
        return <LoginButton callback={handleLogin} />

    const handlePopoverOpen = () =>{
        setIsOpen(true);
    }

    const handlePopoverClose = () =>{
        setIsOpen(false);
    }

    const handleCommunityCreateOpen = () => {
        setIsCommunityCreateOpen(true);
    }

    const handleCommunityCreateClose = () => {
        setIsCommunityCreateOpen(false);
    }

    return (
        <Box>
            <Box sx={{
                display: 'flex',
                alignItems: 'center',
                justifyContent: 'space-between',
                px: 1,
            }}> 
                <Box ref={ref} onClick={handlePopoverOpen}
                    sx={theme => ({
                        display: 'flex',
                        alignItems: 'center',
                        gap: 1,
                        px: 1,
                        py: 0.5,
                        borderRadius: 1,
                        cursor: 'pointer',
                        transition: theme.transitions.create(
                            ['background-color', 'color'],
                            { duration: theme.transitions.duration.short }
                        ),

                        '&:hover': {
                            bgcolor: 'action.hover',
                        },
                    })}>
                        <Avatar 
                            avatarId={avatarId} 
                            baseProps={{
                                alt: username,
                                sx: {width: 32, height: 32}
                            }} 
                            name={username?.substring(0, 2).toLocaleLowerCase()}
                        />
                        <Typography fontSize={14} fontWeight={500} noWrap> {username}</Typography>
                </Box>

            {/* Logout */}
            <IconButton
                aria-label="Logout"
                size="small"
                sx={theme => ({
                transition: theme.transitions.create(
                    ['color', 'background-color', 'transform'],
                    { duration: theme.transitions.duration.short }
                ),
                '&:hover': {
                    color: 'error.main',
                    bgcolor: 'action.hover',
                    transform: 'scale(1.1)',
                },
                })}
                onClick={() => auth.logout()}
            >
                <LogoutIcon fontSize="small" />
            </IconButton>
            </Box>
        <Menu
            anchorEl={ref.current}
            open={isOpen}
            onClose={handlePopoverClose}
            disableScrollLock
        >
            <Box
                sx={{
                    px: 2,
                    py: 1.5,
                    display: 'flex',
                    alignItems: 'center',
                    justifyContent: 'space-between',
                    gap: 2,
                    minWidth: 260
                }}
            >
                <Box
                    sx={{
                        display: 'flex',
                        alignItems: 'center',
                        gap: 1.5
                    }}
                >
                    <Avatar 
                        avatarId={avatarId}
                        baseProps={{
                            sx: { width: 40, height: 40 },
                            alt: username
                        }}
                        name={username?.slice(0, 2).toUpperCase()}
                        navLink={`/profile/${username}`}
                    />
                    <Box>
                        <Typography
                            variant="body2"
                            fontWeight={600}
                            noWrap
                        >
                            {username}
                        </Typography>

                        <Typography
                            variant="caption"
                            color="text.secondary"
                            noWrap
                        >
                            @{auth.user?.profile.preferred_username}
                        </Typography>
                    </Box>
                </Box>

                <IconButton
                    size="small"
                    aria-label="Logout"
                    onClick={() => auth.logout()}
                >
                    <LogoutIcon fontSize="small" />
                </IconButton>
            </Box>

            <Divider />

            <MenuItem
                onClick={() => {
                    handlePopoverClose();
                    navigate(
                        `profile/${auth.user?.profile.preferred_username}`
                    );
                }}
            >
                <ListItemIcon>
                    <AccountCircleIcon />
                </ListItemIcon>
                Profile
            </MenuItem>

            <MenuItem
                onClick={() => {
                    handlePopoverClose();
                    navigate('follows/users');
                }}
            >
                <ListItemIcon>
                    <PeopleIcon />
                </ListItemIcon>
                Followed users
            </MenuItem>

            <MenuItem
                onClick={() =>{
                    handlePopoverClose();
                    navigate('follows/communities')
                }}
            >
                <ListItemIcon>
                    <ForumIcon />
                </ListItemIcon>
                My communities
            </MenuItem>

            <MenuItem
                onClick={() => {
                    handlePopoverClose();
                    navigate(`profile/settings`);
                }}
            >
                <ListItemIcon>
                    <SettingsIcon />
                </ListItemIcon>
                Settings
            </MenuItem>

            <MenuItem
                onClick={handleCommunityCreateOpen}
            >
                <ListItemIcon>
                    <AddIcon />
                </ListItemIcon>
                Create community
            </MenuItem>
        </Menu>
        {isCommunityCreateOpen && 
            <CommunityCreateDialog 
                open={isCommunityCreateOpen} 
                closeDialog={handleCommunityCreateClose} 
            />
        }
        </Box>
    )
}