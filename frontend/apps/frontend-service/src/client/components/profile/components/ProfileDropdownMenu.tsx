import { IconButton, Menu, MenuItem } from "@mui/material"
import MenuIcon from '@mui/icons-material/Menu';
import { useState } from "react";
import { useAuth } from "../../../context/AuthContext";
import { ReportDialog } from "../../report";
import { enqueueSnackbar } from "notistack";
import { CurrentUserProfile, PublicUserProfile } from "@services/user/userProfileApi";

export const ProfileDropdownMenu: React.FC<CurrentUserProfile | PublicUserProfile> = ({...props}) =>{
    const auth = useAuth();
    
    const [isOpen, setIsOpen] = useState<boolean>(false);
    const [option, setOption] = useState<"report" | null>(null);

    const [anchorEl, setAnchorEl] = useState<null | HTMLElement>(null);
    const open = Boolean(anchorEl);

    const handleClick = (event: React.MouseEvent<HTMLButtonElement>) => {
        setAnchorEl(event.currentTarget);
    }

    const handleClose = (option: "report" | null) =>{
        setAnchorEl(null);
        setOption(option);
        if(option)
            setIsOpen(true);
    }

    const handleOptionClose = () =>{
        setOption(null);
        setIsOpen(false);
    }
    return(
        <>
            <IconButton onClick={handleClick} title="Options">
                <MenuIcon sx={{color: 'text.secondary'}} />
            </IconButton>
            <Menu
                anchorEl={anchorEl}
                open={open}
                onClose={handleClose}
                disableScrollLock
                disablePortal
            >
                {auth.user && auth.user?.scope?.includes("admin") && 
                    <MenuItem onClick={() => handleClose(null)}>Ban</MenuItem>
                }
                <MenuItem onClick={() => handleClose("report")}>Report</MenuItem>
            </Menu>

            {isOpen && option === "report" &&
                <ReportDialog 
                    open={isOpen}
                    onClose={handleOptionClose}
                    targetId={props.id}
                    targetType="user"
                    onSuccess={() => enqueueSnackbar("Report sended!", { variant: 'success'})}
                />
            }
        </>
    )
}