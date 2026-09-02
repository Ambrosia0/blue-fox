import { DataGrid, type GridColDef } from '@mui/x-data-grid';
import { useEffect, useState } from 'react';
import { IconButton, Tooltip } from '@mui/material';
import DeleteIcon from '@mui/icons-material/Delete';
import { fetchComments, deleteComment, type CommentResponse } from '../../../services/api/adminCommentApi';

export const CommentControl = () => {
    const [comments, setComments] = useState<CommentResponse[]>([]);
    const [page, setPage] = useState(0);
    const [pageSize, setPageSize] = useState(20);
    const [loading, setLoading] = useState(false);

    const columns: GridColDef[] = [
        {field: 'id', headerName: 'ID', width: 80},
        {field: 'authorId', headerName: 'Author', width: 150, flex: 1},
        {field: 'content', headerName: 'Content', width: 200, flex: 1},
        {field: 'createdAt', headerName: 'Created at', width: 160, flex: 0.7},
        {
            field: 'actions',
            headerName: 'Actions',
            width: 80,
            sortable: false,
            renderCell: (params) => (
                <Tooltip title="Delete comment">
                    <IconButton size="small" color="error" onClick={() => handleDeleteComment(params.row.id)}>
                        <DeleteIcon fontSize="small"/>
                    </IconButton>
                </Tooltip>
            )
        }
    ];

    async function getComments() {
        setLoading(true);
        try {
            const result = await fetchComments(page, pageSize);
            const data = result.data as any;
            if (data && data.content) {
                setComments(data.content);
            }
        } catch (error) {
            console.error('Failed to fetch comments:', error);
        } finally {
            setLoading(false);
        }
    }

    async function handleDeleteComment(id: number) {
        if (!window.confirm('Are you sure you want to delete this comment?')) {
            return;
        }
        try {
            await deleteComment(id);
            setComments(prev => prev.filter(c => c.id !== id));
        } catch (error) {
            console.error('Failed to delete comment:', error);
        }
    }

    useEffect(() => {
        getComments();
    }, [page, pageSize]);

    return (
        <DataGrid
            rows={comments}
            columns={columns}
            loading={loading}
            pageSizeOptions={[10, 20, 50]}
            paginationMode="server"
            onPaginationModelChange={(newModel) => {
                setPage(newModel.page);
                setPageSize(newModel.pageSize);
            }}
            density='compact'
        />
    );
};