import { Avatar as MuiAvatar } from "@mui/material"
import { AVATAR_ENDPOINT } from "@services/apiClient";
import type { ComponentProps } from "react";
import { useNavigate } from "react-router";
import GroupIcon from "@mui/icons-material/Group";

type AvatarComponentProps = ComponentProps<typeof MuiAvatar>

type IconProps = ComponentProps<typeof GroupIcon>

interface AvatarProps{
    name?: string;
    avatarId?: string;
    baseProps?: AvatarComponentProps;
    iconProps?: IconProps;
    navLink?: string;
}

export const Avatar: React.FC<AvatarProps> = ({
    name, 
    avatarId,
    navLink,
    baseProps,
    iconProps
}) =>{
    const navigate = useNavigate();
    return(
        <>
            {avatarId? 
                <MuiAvatar 
                    sx={{cursor: 'pointer', ...baseProps?.sx}}
                    onClick={() => {navLink? navigate(navLink): null}} 
                    src={`${AVATAR_ENDPOINT}/${avatarId}`}
                    {...baseProps}
                    />:
                <MuiAvatar
                    sx={{cursor: 'pointer', ...baseProps?.sx}}
                    onClick={() => {navLink? navigate(navLink): null}}
                    {...baseProps}>{(name && name.substring(0, 2)) || <GroupIcon {...iconProps}/>}</MuiAvatar> 
            }
        </>
    )
}