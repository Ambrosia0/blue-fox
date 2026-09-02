import {
    Box,
    Button,
    Collapse,
    Dialog,
    DialogActions,
    DialogContent,
    DialogContentText,
    DialogTitle,
    FormControl,
    FormHelperText,
    Input,
    InputLabel,
    Pagination,
    Stack,
    Typography,
    Avatar,
    Tooltip
} from "@mui/material";
import { ChangeEvent, useEffect, useState } from "react";
import { DraftPreview } from "../../post/DraftPreview";
import { createDraft, deletePost, Draft, getUnpublished } from "../../../services/user/userEditorApi";
import { TransitionGroup } from "react-transition-group";
import { Page, Pageable } from "../../../types/types";
import AddCircleOutlineIcon from '@mui/icons-material/AddCircleOutline';
import DeleteIcon from '@mui/icons-material/Delete';
import DraftIcon from '@mui/icons-material/Note';
import { useTranslation } from "react-i18next";

type DraftContainerProps = {
    setDraftCreation: (state: boolean) => void;
    draftCreationOpen: boolean;
}

export const DraftContainer: React.FC<DraftContainerProps> = ({setDraftCreation, draftCreationOpen}) =>{
    const [open, setOpen] = useState(false);
    const [dialogueValue, setDialogueValue] = useState<Draft | undefined>(undefined);
    const [error, setError] = useState<boolean>(false);
    const [title, setTitle] = useState<string>("");
    const [page, setPage] = useState<number>(0);
    const [drafts, setDrafts] = useState<Page<Draft>>();
    const { t } = useTranslation();

    const handleOpen = (draft: Draft) =>{
        setDialogueValue(draft);
        setOpen(true);
    }

    const handleClose = () =>{
        setDialogueValue(undefined);
        setOpen(false);
    }

    const handleDelete = async () =>{
        await deleteDraftFunc(dialogueValue!.id);
        setOpen(false);
    }

    const handlePageChange = async (event: ChangeEvent<unknown>, page: number) =>{
        fetchDrafts({
            page: page-1,
            size: 10
        })
    }

    async function deleteDraftFunc(postId: number){
        try {
            await deletePost(postId);
            fetchDrafts({
                page: drafts?.number ?? 0,
                size: drafts?.size ?? 10
            })
        } catch (error) {
            console.log(error);
        }
    }

    async function fetchDrafts(pageable: Pageable) {
        try {
            setDrafts(
                await getUnpublished(pageable));
        } catch (error) {
            console.log(error);
        }
    }

    const handleCreationOpen = () =>{
        setDraftCreation(true);
    }

    const handleDraftCreation = async () =>{
        try {
            if(title.length <= 5){
                return setError(true);
            }
            const resp = await createDraft(title);
            setError(false);
            setDrafts(prev =>
                    prev?
                        {
                            ...prev,
                            content: [
                                {id: resp.id, title: resp.title, updatedAt: resp.createdAt}, 
                                ...(prev.numberOfElements === prev.size? 
                                    prev.content.slice(0, prev.content.length-1):
                                    prev.content)
                            ]
                        }:
                        {
                            content: [{id: resp.id, title: resp.title, updatedAt: resp.createdAt}],
                            empty: false,
                            first: true,
                            last: true,
                            number: 0,
                            numberOfElements: 1,
                            pageable: {
                                page: 0,
                                size: 10
                            },
                            size: 10,
                            totalElements: 1,
                            totalPages: 1
                        }
            );
            setDraftCreation(false);
        } catch (error) {
            console.log(error);
        }
    }

    const handleCreationClose = () =>{
        setError(false);
        setDraftCreation(false);
    }

    useEffect(()=>{
        fetchDrafts({
            page: 0,
            size: 10
        });
    }, [])
    

    return(
        <Box sx={{
            display: "flex",
            flexDirection: "column",
            height: "100%",
        }}>
            {/* Header */}
            <Box sx={{
                display: "flex",
                alignItems: "center",
                justifyContent: "space-between",
                pb: 2,
                borderBottom: '1px solid',
                borderColor: "divider"
            }}>
                <Stack direction="row" alignItems="center" spacing={1.5}>
                    <Avatar sx={{ 
                        bgcolor: "primary.main", 
                        width: 36, 
                        height: 36,
                        fontSize: 16
                    }}>
                        <DraftIcon fontSize="small" />
                    </Avatar>
                    <Typography variant="h5" component="h2" sx={{ 
                        fontWeight: 600,
                        color: "text.primary"
                    }}>
                        {t('profile.draft.sectionTitle')}
                    </Typography>
                    {drafts?.totalElements !== undefined && (
                        <Typography variant="caption" sx={{
                            color: "text.secondary",
                            bgcolor: "action.hover",
                            px: 1.5,
                            py: 0.3,
                            borderRadius: 1
                        }}>
                            {drafts.totalElements}
                        </Typography>
                    )}
                </Stack>
                <Tooltip title={t('profile.draft.createTooltip')}>
                    <Button
                        variant="contained"
                        startIcon={<AddCircleOutlineIcon />}
                        onClick={handleCreationOpen}
                        sx={{
                            borderRadius: 2,
                            textTransform: "none",
                            px: 2.5,
                            boxShadow: 1,
                            "&:hover": {
                                boxShadow: 3
                            }
                        }}
                    >
                        {t('profile.draft.createNew')}
                    </Button>
                </Tooltip>
            </Box>

            {/* List */}
            <Box sx={{
                flexGrow: 1,
                overflowY: 'auto',
                py: 2
            }}>
                {drafts?.content && drafts.content.length > 0 ? (
                    <Stack spacing={1.5}>
                        <TransitionGroup>
                            {drafts.content.map((draft, idx) => (
                                <Collapse key={idx} in timeout={300}>
                                    <DraftPreview draft={draft} onDelete={handleOpen} />
                                </Collapse>
                            ))}
                        </TransitionGroup>
                    </Stack>
                ) : (
                    /* Пустое состояние */
                    <Box sx={{
                        display: "flex",
                        flexDirection: "column",
                        alignItems: "center",
                        justifyContent: "center",
                        py: 6,
                        color: "text.secondary"
                    }}>
                        <DraftIcon sx={{ 
                            fontSize: 64, 
                            color: "text.disabled",
                            mb: 2
                        }} />
                        <Typography variant="h6" sx={{ mb: 1, color: "text.primary" }}>
                            {t('profile.draft.emptyTitle')}
                        </Typography>
                        <Typography variant="body2" sx={{ mb: 2, textAlign: "center" }}>
                            {t('profile.draft.emptyMessage')}
                        </Typography>
                        <Button
                            variant="outlined"
                            startIcon={<AddCircleOutlineIcon />}
                            onClick={handleCreationOpen}
                        >
                            {t('profile.draft.emptyButton')}
                        </Button>
                    </Box>
                )}
            </Box>

            {/* Pagination */}
            {drafts && drafts.totalPages > 1 && (
                <Box sx={{
                    display: "flex",
                    justifyContent: "center",
                    pt: 2,
                    borderTop: '1px solid',
                    borderColor: "divider",
                    bgcolor: "background.paper"
                }}>
                    <Pagination 
                        count={drafts.totalPages} 
                        page={(drafts.number ?? 0) + 1}
                        onChange={handlePageChange} 
                        color="primary"
                        size="large"
                        sx={{
                            '& .MuiPaginationItem-root': {
                                borderRadius: 1
                            }
                        }}
                    />
                </Box>
            )}

            {/* Creation Dialog */}
            <Dialog 
                open={draftCreationOpen} 
                onClose={handleCreationClose}
                maxWidth="sm"
                fullWidth
                disableScrollLock={false}
            >
                <DialogTitle sx={{
                    pb: 1.5,
                    fontSize: "1.25rem"
                }}>
                    {t('profile.draft.createTitle')}
                </DialogTitle>
                <DialogContent sx={{
                    pt: 2,
                    display: "flex",
                    flexDirection: "column",
                    gap: 2
                }}>
                    <FormControl error={error} fullWidth variant="outlined">
                        <InputLabel htmlFor="title-input">{t('profile.draft.titleLabel')}</InputLabel>
                        <Input
                            id="title-input"
                            value={title}
                            onChange={(e) => setTitle(e.target.value)}
                            autoComplete="off"
                        />
                        <FormHelperText>
                            {error ? t('profile.draft.titleError') : t('profile.draft.titleHelper')}
                        </FormHelperText>
                    </FormControl>
                </DialogContent>
                <DialogActions sx={{ px: 3, pb: 2 }}>
                    <Button 
                        onClick={handleCreationClose}
                        color="inherit"
                    >
                        {t('profile.draft.cancelButton')}
                    </Button>
                    <Button 
                        onClick={handleDraftCreation}
                        variant="contained"
                        sx={{ borderRadius: 2 }}
                    >
                        {t('profile.draft.createButton')}
                    </Button>
                </DialogActions>
            </Dialog>

            {/* Deletion Dialog */}
            <Dialog 
                open={open} 
                onClose={handleClose}
                maxWidth="sm"
                fullWidth
            >
                <DialogTitle sx={{
                    display: "flex",
                    alignItems: "center",
                    gap: 1
                }}>
                    <DeleteIcon color="error" />
                    {t('profile.draft.deleteTitle')}
                </DialogTitle>
                <DialogContent>
                    <DialogContentText sx={{
                        fontSize: "1rem",
                        lineHeight: 1.6,
                        mt: 1
                    }}>
                        {dialogueValue && `${t('profile.draft.deleteConfirm')} «${dialogueValue.title}»?`}
                    </DialogContentText>
                    <Typography variant="body2" sx={{
                        color: "text.secondary",
                        mt: 2,
                        fontStyle: "italic"
                    }}>
                        {t('profile.draft.deleteWarning')}
                    </Typography>
                </DialogContent>
                <DialogActions sx={{ px: 3, pb: 2 }}>
                    <Button 
                        onClick={handleClose}
                        color="inherit"
                    >
                        {t('profile.draft.cancelButton')}
                    </Button>
                    <Button 
                        onClick={handleDelete}
                        variant="contained"
                        color="error"
                        sx={{ borderRadius: 2 }}
                    >
                        {t('profile.draft.deleteButton')}
                    </Button>
                </DialogActions>
            </Dialog>
        </Box>
    )
}
