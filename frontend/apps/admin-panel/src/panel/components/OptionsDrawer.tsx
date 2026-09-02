import { Avatar, Box, Collapse, Divider, Drawer, IconButton, List, ListItemButton, ListItemIcon, ListItemText, Typography } from "@mui/material"
import { useControlOption, type ControlOption } from "../../context/ControlOptionContext"
import LogoutIcon from '@mui/icons-material/Logout';
import GroupIcon from '@mui/icons-material/Group';
import CollectionsIcon from '@mui/icons-material/Collections';
import ArticleIcon from '@mui/icons-material/Article';
import ExpandLess from '@mui/icons-material/ExpandLess';
import ExpandMore from '@mui/icons-material/ExpandMore';
import ViewModuleIcon from '@mui/icons-material/ViewModule';
import CommentIcon from '@mui/icons-material/Comment';
import FlagIcon from '@mui/icons-material/Flag';
import { useState } from "react";
import { AVATAR_ENDPOINT } from "@services/apiClient";
import { useAuth } from "../../context/AuthContext";


export const OptionDrawer = () => {
    const {option, changeOption} = useControlOption();
    const [open, setOpen] = useState(false);

    const { user } = useAuth();
    
    const attributes = user?.profile["attributes"] as Record<string, string>;
    const avatarId = attributes?.["avatarId"]?.[0];

    const drawerWidth = 240

    return (
        <Drawer variant="permanent" anchor="left"
            sx={{
                width: drawerWidth,
                height: '100vh',
                flexShrink: 0,
                [`& .MuiDrawer-paper`]: {
                    width: drawerWidth,
                    boxSizing: "border-box",
                    backgroundColor: 'background.paper'
                },
            }}>
            <Box display={'flex'} alignItems={'center'} justifyContent={'space-between'} px={2} py={1.5}>
                {avatarId? 
                    <Avatar alt="" src={`${AVATAR_ENDPOINT}/${avatarId}`}/>:
                    <Avatar alt="" >{user?.profile.preferred_username?.substring(0, 2)}</Avatar>
                }
                <Typography sx={{
                    ml: 2, 
                    flexGrow: 1, 
                    fontWeight: 500, 
                    overflow: "hidden", 
                    textOverflow: "ellipsis", 
                    whiteSpace: "nowrap"}}>
                    {user?.profile.preferred_username ?? "Unknown"}
                </Typography>
                <IconButton size="small">
                    <LogoutIcon fontSize="small"/>
                </IconButton>
            </Box>
            <Typography px={2} py={1} variant="subtitle2" color="text.secondary">General</Typography>
            <Divider/>
            
            <List sx={{p: 1}}>
                {[
                    { key: "posts", icon: <ArticleIcon />, label: "Posts" },
                    { key: "users", icon: <GroupIcon />, label: "Users" },
                    { key: "images", icon: <CollectionsIcon />, label: "Images" },
                ].map(({ key, icon, label }) => 
                    <ListItemButton key={key} selected={option === key} onClick={() => {changeOption(key as ControlOption)}}
                        sx={{
                            borderRadius: 1,
                            mb: 0.5
                        }}>
                            <ListItemIcon sx={{ minWidth: 36 }}>{icon}</ListItemIcon>
                            <ListItemText primary={label} />
                    </ListItemButton>
                )}
                <ListItemButton key="view" onClick={() => {setOpen(!open)}}
                    sx={{
                            borderRadius: 1,
                            mb: 0.5
                        }}>
                            <ListItemIcon sx={{ minWidth: 36 }}><ViewModuleIcon /></ListItemIcon>
                            <ListItemText primary="App view" />
                            {open? <ExpandLess />: <ExpandMore />}
                </ListItemButton>
                <Collapse in={open} timeout={"auto"} unmountOnExit>
                    <List disablePadding>
                        <ListItemButton key="comments" selected={option === "comments"} onClick={() => changeOption("comments")}>
                            <ListItemIcon sx={{ pl: 2}}>
                                <CommentIcon />
                            </ListItemIcon>
                            <ListItemText primary="Comments" />
                        </ListItemButton>
                        <ListItemButton key="reports" selected={option === "reports"} onClick={() => changeOption("reports")}>
                            <ListItemIcon sx={{ pl: 2}}>
                                <FlagIcon />
                            </ListItemIcon>
                            <ListItemText primary="Reports" />
                        </ListItemButton>
                    </List>
                </Collapse>
            </List>
            <Divider />
        </Drawer>
    )
}