import { Box, Button, Chip, CircularProgress, Dialog, DialogActions, DialogContent, DialogTitle, IconButton, List, ListItem, ListItemAvatar, ListItemButton, ListItemText, Menu, MenuItem, Paper, Popper, Stack, TextField, Typography } from "@mui/material"
import { UserInfo } from "../../../../types/user"
import { useEffect, useMemo, useRef, useState } from "react"
import { getCommunityScopes, Scope, ScopePair, SCOPES } from "@services/user/userCommunityApi"
import { Avatar } from "../../../user/Avatar"
import { isInfoLoaded, missingScopes } from "../../utils/utils"
import { useTranslation } from "react-i18next"

import AddIcon from '@mui/icons-material/Add';
import SearchIcon from '@mui/icons-material/Search';
import DeleteIcon from '@mui/icons-material/Delete';
import { searchUsers } from "@services/user/userProfileApi"
import { UserView } from "./UserView"

type ModeratorEditDialogProps = {
    communityId: number,
    ownerId: string,
    communityModerators: (UserInfo | {id: string})[],
    isOpen: boolean,
    close: () => void,
    editScopes: (userScopes: ScopePair[], loadedInfo: (UserInfo | {id: string})[]) => Promise<void>
}

export const ModeratorEditDialog: React.FC<ModeratorEditDialogProps> = ({
    communityModerators,
    ownerId,
    communityId,
    isOpen,
    close,
    editScopes
}) =>{
    const [info, setInfo] = useState<(UserInfo | {id: string})[]>(
        communityModerators.filter(user => user.id !== ownerId)
    );
    const [userScopes, setUserScopes] = useState<ScopePair[]>([]);

    const [isLoading, setIsLoading] = useState<boolean>(false);
    const [searchedUsers, setSearchedUsers] = useState<UserInfo[]>([]);
    const [searchString, setSearchString] = useState<string>("");

    const inputRef = useRef(null);

    const [userAnchorEl, setUserAnchorEl] = useState<null | HTMLElement>(null);
    const [selectedUserIdx, setSelectedUserIdx] = useState<number | null>();

    const { t } = useTranslation();

    async function getCurrentScopes() {
        try {
            const data = await getCommunityScopes(communityId);
            setUserScopes(data.filter(user => user.userId !== ownerId));
        } catch (error) {
            console.log(error);
        }
    }

    const handleSearch = async () =>{
        if(searchString.length < 3)
            return;
        try {
            setSearchedUsers(await searchUsers(searchString));
        } catch (error) {
            console.log(error);
        }
    }

    const handleScopeRemove = (userIdx: number, scopeIdx: number) => {
        setUserScopes(prev => {
            const next = [...prev];
            next[userIdx].scopes = next[userIdx].scopes.filter((_, index) => index !== scopeIdx);
            return next;
        })
    }

    const handleScopeAdd = (userIdx: number, scope: Scope) => {
        setUserScopes(prev => {
            const next = [...prev];
            next[userIdx].scopes.push(scope);
            return next;
        })
    }

    const handleClose = (event?: {}, reason?: "backdropClick" | "escapeKeyDown") => {
        if(reason && reason === 'backdropClick'){
            return;
        }
        close();
    }

    const handleEdit = async () =>{
        try {
            setIsLoading(true);
            await editScopes(userScopes, info);
            close();
        } finally{
            setIsLoading(false);
        }
    }

    const handleUserAdd = (user: UserInfo) =>{
        if(userScopes.length >= 5)
            return;
        if(user.id === ownerId || userScopes.find(foundUser => foundUser.userId === user.id)){
            setSearchedUsers([]);
            setSearchString("");
            return;
        }
        setInfo(prev => [...prev, user]);
        setUserScopes(prev => [...prev, {
            userId: user.id,
            scopes: []
        }]);
        setSearchString("");
        setSearchedUsers([]);
    }

    const handleUserRemove = (idx: number) =>{
        setInfo(prev => prev.filter(user => user.id !== userScopes[idx].userId));
        setUserScopes(prev => prev.filter((_, index) => index !== idx));
    }

    const handleScopeMenuOpen = (event: React.MouseEvent<HTMLElement>, userIdx: number) => {
        setSelectedUserIdx(userIdx);
        setUserAnchorEl(event.currentTarget);
    }

    const handleScopeMenuClose = () =>{
        setSelectedUserIdx(null);
        setUserAnchorEl(null);
    }

    useEffect(() => {
        getCurrentScopes();
    }, [])

    useEffect(() => {
        const timeout = setTimeout(
            () => {
                handleSearch();
            }, 500);
        return () => clearTimeout(timeout);
    }, [searchString]);
    
    const scopesByUser = useMemo(
        () => new Map(userScopes.map(scope => [scope.userId, scope.scopes])),
        [userScopes]
    );

    return(
        <Dialog
            disableScrollLock
            onClose={handleClose}
            open={isOpen}
            maxWidth="sm"
            fullWidth
        >
            <DialogTitle>
                Edit moderators
            </DialogTitle>

            {userAnchorEl &&
                <Menu
                    anchorEl={userAnchorEl}
                    open={userAnchorEl !== null}
                    onClose={handleScopeMenuClose}
                >
                    {
                        missingScopes(userScopes[selectedUserIdx].scopes).map(scope =>
                            <MenuItem
                                key={scope}
                                onClick={() => handleScopeAdd(selectedUserIdx, scope)}
                            >
                            {t(`community.scopes.${scope}`)}
                            </MenuItem>
                        )
                    }
                </Menu>
            }

            <DialogContent dividers>
                <Box>
                    <Box>
                        <TextField
                            ref={inputRef}
                            placeholder={t('search.users')}
                            slotProps={{
                                input: {
                                    startAdornment: <SearchIcon />
                                }
                            }}
                            value={searchString}
                            onChange={(e) => {
                                setSearchedUsers([]);
                                setSearchString(e.target.value)
                            }}
                        />
                        {searchedUsers.length !== 0 &&
                            <Popper
                                open={searchedUsers.length !== 0}
                                anchorEl={inputRef.current}
                                sx={{
                                    zIndex: 3333
                                }}
                                placement="bottom-start"
                            >
                                <Paper sx={{
                                    width: inputRef.current?.clientWidth,
                                    maxHeight: 300,
                                    overflowY: "auto"
                                }}>
                                    <List>
                                        {searchedUsers.map(user => (
                                            <ListItemButton
                                                key={user.id}
                                                onClick={() => handleUserAdd(user)}
                                            >
                                                <ListItemAvatar>
                                                    <Avatar 
                                                        avatarId={user.avatarId}
                                                        name={user.username}
                                                        baseProps={{
                                                            alt: user.username
                                                        }}
                                                    />
                                                </ListItemAvatar>
                                                <ListItemText 
                                                    primary={`${user.firstName} ${user.lastName}`} 
                                                    secondary={`@${user.username}`}
                                                />
                                            </ListItemButton>
                                        ))}
                                    </List>
                                </Paper>
                            </Popper>
                        }
                    </Box>
                    <Box>
                        <List>
                            {info.map((user, userIndex) => {
                                const isLoaded = isInfoLoaded(user);
                                const username = isLoaded ? user.username : undefined;
                                const scopes = scopesByUser.get(user.id) ?? [];
                                return(
                                    <ListItem 
                                        key={user.id}
                                        divider
                                    >
                                        <ListItemAvatar>
                                            <Avatar 
                                                avatarId={isLoaded? 
                                                    user.avatarId:
                                                    undefined
                                                }
                                                baseProps={{
                                                    alt: user.id
                                                }}
                                                name={isLoaded? user.firstName: username}
                                            />
                                        </ListItemAvatar>
                                        <Box
                                            sx={{
                                                display: 'flex',
                                                flexDirection: 'row',
                                                justifyContent: 'space-between'
                                            }}
                                        >
                                            <Box
                                                sx={{
                                                    display: "flex",
                                                    alignItems: "center",
                                                    gap: 2,
                                                    flex: 1,
                                                }}
                                            >
                                                <UserView 
                                                    user={user}
                                                />

                                                <Stack
                                                    direction="row"
                                                    spacing={1}
                                                    useFlexGap
                                                    flexWrap="wrap"
                                                >
                                                    {scopes.map((scope, scopeIndex) => (
                                                        <Chip 
                                                            key={scopeIndex} 
                                                            label={t(`community.scopes.${scope}`)} 
                                                            size="small"
                                                            onDelete={() => handleScopeRemove(userIndex, scopeIndex)}
                                                        />
                                                    ))}
                                                </Stack>
                                            </Box>
                                            <Box
                                                flexShrink={2}
                                            >
                                                {SCOPES.length !== scopes.length &&
                                                    <IconButton onClick={(e) => handleScopeMenuOpen(e, userIndex)}>
                                                        <AddIcon />
                                                    </IconButton>
                                                }
                                                <IconButton onClick={() => handleUserRemove(userIndex)}>
                                                    <DeleteIcon />
                                                </IconButton>
                                            </Box>
                                        </Box>
                                    </ListItem>
                                )
                            })}
                        </List>
                    </Box>
                </Box>
            </DialogContent>
            <DialogActions>
                {isLoading?
                    <CircularProgress
                        size="medium"
                    />:
                    <>
                        <Button onClick={handleEdit}>{t('actions.edit')}</Button>
                        <Button onClick={close}>{t('actions.close')}</Button>
                    </>
                }
            </DialogActions>
        </Dialog>
    )
}