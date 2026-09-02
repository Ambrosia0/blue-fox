import { Box, IconButton, Paper, Tooltip, Typography } from "@mui/material"
import DeleteIcon from '@mui/icons-material/Delete';
import EditIcon from '@mui/icons-material/Edit';
import { Draft } from "../../services/user/userEditorApi";
import { useNavigate, useNavigation } from "react-router";
import { useTranslation } from 'react-i18next';

type DraftProps = {
    draft: Draft;
    onDelete: (draft: Draft) => void;
}

export const DraftPreview: React.FC<DraftProps> = ({draft, onDelete}) => {
    const navigate = useNavigate();
    const { t } = useTranslation();

    return(
        <Paper sx={{ p: 2, mb: 2 }}>
            <Box sx={{display: 'flex', flexDirection: 'row', justifyContent: 'space-between'}}>
                <Box>
                    <Typography variant="h6">
                        {draft.title || t('profile.draft.untitled')}
                    </Typography>

                    <Typography
                        variant="body2"
                        color="text.secondary"
                    >
                        {t('profile.draft.lastUpdated')}: {new Date(draft.updatedAt).toLocaleDateString()}
                    </Typography>
                </Box>
                <Box>
                    <Tooltip title={t('profile.draft.editPost')}>
                        <IconButton onClick={() => navigate(`/post/${draft.id}/editor`)}>
                            <EditIcon />
                        </IconButton>
                    </Tooltip>
                    <Tooltip title={t('profile.draft.deletePost')}>
                        <IconButton onClick={() => onDelete(draft)}>
                            <DeleteIcon />
                        </IconButton>
                    </Tooltip>
                </Box>
            </Box>
        </Paper>
    )
}
