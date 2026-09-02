import { useState } from "react";
import { IconButton, Menu, MenuItem } from "@mui/material"
import MenuIcon from '@mui/icons-material/Menu';
import { useTranslation } from 'react-i18next';
import { useAuth } from "../../../context/AuthContext";
import { Scope } from "@services/user/userCommunityApi";

type DropdownMenuProps = {
    id: number;
    scopes?: Set<Scope>;
    deleteCallback?: (postId: number) => void;
}

export const PostDropdownMenu: React.FC<DropdownMenuProps> = ({
    id,
    scopes,
    deleteCallback
}) =>{
    const auth = useAuth();
    const [anchorEl, setAnchorEl] = useState<null | HTMLElement>(null);
    const open = Boolean(anchorEl);
    const { t } = useTranslation();

    const handleClick = (event: React.MouseEvent<HTMLButtonElement>) => {
        setAnchorEl(event.currentTarget);
    }

    const handleClose = () =>{
        setAnchorEl(null);
    }

    const handleDelete = () =>{
        deleteCallback(id);
        setAnchorEl(null);
    }

    return(
        <div>
            <IconButton onClick={handleClick} title={t('postMenu.options')}>
                <MenuIcon sx={{color: 'text.secondary'}} />
            </IconButton>
            <Menu
                anchorEl={anchorEl}
                open={open}
                onClose={handleClose}
                disableScrollLock
            >
                {
                    scopes && scopes.has('POST_DELETE') &&
                    <MenuItem onClick={handleDelete}></MenuItem>
                }
                {auth.user && auth.user?.scope?.includes("admin") && 
                    <MenuItem onClick={handleClose}>{t('postMenu.hide')}</MenuItem>
                }
                {auth.user && auth.user?.scope?.includes("user") && 
                    <MenuItem onClick={handleClose}>{t('postMenu.test')}</MenuItem>
                }
            </Menu>
        </div>
    )
}
