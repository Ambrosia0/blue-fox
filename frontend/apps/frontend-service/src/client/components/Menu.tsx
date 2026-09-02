import { Box, List, ListItemButton, Typography, SxProps } from '@mui/material';
import type { ReactElement } from "react";
import { useLocation, useNavigate } from 'react-router';
import ArticleIcon from '@mui/icons-material/Article';
import WhatshotIcon from '@mui/icons-material/Whatshot';
import PeopleIcon from '@mui/icons-material/People';
import DynamicFeedIcon from '@mui/icons-material/DynamicFeed';
import { useTranslation } from 'react-i18next';

export type MenuOption = {
    key: string;
    icon: ReactElement;
    label: string;
    onClick: () => void;
}

export type MenuProps = {
    options: MenuOption[];
}

const menuButtonStyle: SxProps = {
    borderRadius: 2,
    px: 2,
    py: 1,
    mb: 0.5,
    transition: 'all 0.2s ease-in-out',
    '&.Mui-selected': {
        backgroundColor: 'rgba(255, 255, 255, 0.1)',
        '&:hover': {
            backgroundColor: 'rgba(255, 255, 255, 0.15)',
        },
    },
    '&:hover': {
        backgroundColor: 'rgba(255, 255, 255, 0.05)',
        transform: 'translateX(4px)',
    },
};

const iconStyle: SxProps = {
    mr: 1.5,
    fontSize: 20,
};

const textStyle: SxProps = {
    typography: 'h6',
    fontWeight: 500,
    fontSize: '0.95rem',
};

export const Menu = () =>{
    const location = useLocation();
    const navigate = useNavigate();
    const {t} = useTranslation();

    const Item: React.FC<{path: string, menuKey: string, icon: ReactElement, sx?: SxProps}> = ({path, menuKey, icon, sx}) =>
        <ListItemButton 
            onClick={() => navigate(path)} 
            selected={location.pathname === path}
            sx={{...menuButtonStyle, ...sx}}>
                <Box sx={iconStyle}>
                    {icon}
                </Box>
                <Typography variant='h6' sx={textStyle}>
                    {t(`menu.${menuKey}`)}
                </Typography>
        </ListItemButton>

    return(
        <Box
            sx={{
                height: '100%',
                p: 1,
            }}>
            <List>
                <Item menuKey='popular' path='/' icon={<WhatshotIcon />} sx={{mt: 1}} />
                <Item menuKey='latest' path='/latest' icon={<ArticleIcon />} />
                <Item menuKey='communities' path='/communities' icon={<PeopleIcon />} sx={{mt: 1}} />
                <Item menuKey='personal' path='/personal' icon={<DynamicFeedIcon />} />
            </List>
        </Box>
    )
}
