import { Box, Button, IconButton, Paper } from "@mui/material"
import { UserInfo } from "../../../../types/user"
import { useState } from "react";
import { CommunitySideBarView } from "./CommunitySideBarView";
import { CommunitySideBarEdit } from "./CommunitySideBarEdit";
import { CommunityEdit, ScopePair } from "@services/user/userCommunityApi";

import EditIcon from '@mui/icons-material/Edit';
import AddModeratorIcon from '@mui/icons-material/AddModerator';
import { ModeratorEditDialog } from "./ModeratorEditDialog";

type CommunitySideBarProps = {
    ownerId: string,
    communityId: number,
    description?: string,
    rules?: string[],
    tags?: string[],
    communityModerators: ({id: string} | UserInfo)[];
    canEdit: boolean;
    save: (patch: CommunityEdit) => Promise<void>;
    editScopes: (scopes: ScopePair[], loadedInfo: (UserInfo | {id: string})[]) => Promise<void>;
}

export const CommunitySideBar: React.FC<CommunitySideBarProps> = ({
    canEdit,
    save,
    ...props
}) => {
    const [editMode, setEditMode] = useState<boolean>(false);
    const [isOpen, setIsOpen] = useState<boolean>(false);
    const [patch, setPatch] = useState<CommunityEdit>({});

    const cancel = () =>{
        setEditMode(false);
    }

    const editInfo = () =>{
        setEditMode(true);
    }

    const openDialog = () =>{
        setIsOpen(true);
    }

    const closeDialog = () =>{
        setIsOpen(false);
    }

    const confirmSave = () =>{
        save(patch);
        setEditMode(false);
    }

    return (
        <Paper
            variant="outlined"
            sx={{
                p: 3,
                minWidth: 280,
                maxWidth: 280,
                borderRadius: 4,
                bgcolor: "background.paper",
                "&:hover": {
                    ".edit-action": {
                        opacity: canEdit? 1: 0
                    }
                }
            }}
        >
            {isOpen && 
                <ModeratorEditDialog
                    isOpen={isOpen}
                    close={closeDialog}
                    {...props}
                />
            }
            {canEdit && 
                ((editMode &&
                    <Box 
                        sx={{
                            position: 'relative'
                        }}
                    >
                        <Box 
                            display="flex" 
                            justifyContent="flex-end"
                            sx={{
                                position: 'absolute',
                                right: 0
                            }}
                        >
                            <Button 
                                onClick={confirmSave}
                            >
                                Save
                            </Button>
                            <Button 
                                onClick={cancel}
                            >
                                Cancel
                            </Button>
                        </Box>
                    </Box>
                )
                || (
                    <Box sx={{
                        display: 'flex',
                        position: 'relative'
                    }}>
                        <Box 
                            className="edit-action"
                            sx={{
                                position: "absolute",
                                opacity: 0,
                                transition: 'opacity 0.2s ease',
                                left: 'auto',
                                right: 0
                            }}
                        >
                            <IconButton
                                aria-label="Edit moderators"
                                title="Edit moderators"
                                size='small'
                                onClick={openDialog}
                            >
                                <AddModeratorIcon />
                            </IconButton>
                            <IconButton
                                aria-label="Edit community info"
                                title="Edit community info"
                                onClick={editInfo}
                                size="small"
                            >
                                <EditIcon />
                            </IconButton>
                        </Box>
                    </Box>
                ))
            }

            {editMode?
                <CommunitySideBarEdit setPatch={setPatch} {...props} />:
                <CommunitySideBarView {...props} />
            }
        </Paper>
    );
};