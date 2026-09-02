import { DataGrid, type GridColDef } from '@mui/x-data-grid';
import { useEffect, useState } from 'react';
import { Link, IconButton, Tooltip } from '@mui/material';
import DeleteIcon from '@mui/icons-material/Delete';
import { fetchPosts, deletePost, type PostViewResponse } from '../../../services/api/adminPostApi';

export interface PaginatedResponse<T> {
    content?: T[];
    totalElements?: number;
    totalPages?: number;
    number?: number;
    size?: number;
}

export const PostControl = () => {
    const [posts, setPosts] = useState<PostViewResponse[]>([]);
    const [page, setPage] = useState(0);
    const [pageSize, setPageSize] = useState(20);
    const [loading, setLoading] = useState(false);

    const columns: GridColDef[] = [
        {field: 'id', headerName: 'ID', width: 80},
        {field: 'authorId', headerName: 'Author', width: 120, flex: 1},
        {field: 'title', headerName: 'Title', width: 200, flex: 1, hideable: true},
        {field: 'likeCount', headerName: 'Likes', width: 80},
        {field: 'publishedAt', headerName: 'Published', width: 140, flex: 0.8},
        {
            field: 'actions',
            headerName: 'Actions',
            width: 80,
            sortable: false,
            renderCell: (params) => (
                <Tooltip title="Delete post">
                    <IconButton size="small" color="error" onClick={() => handleDeletePost(params.row.id)}>
                        <DeleteIcon fontSize="small"/>
                    </IconButton>
                </Tooltip>
            )
        }
    ];

    async function getPosts() {
        setLoading(true);
        try {
            const result = await fetchPosts(page, pageSize);
            const data = result.data as PaginatedResponse<PostViewResponse> | PostViewResponse[];
            if (Array.isArray(data)) {
                setPosts(data);
            } else if (data && 'content' in data && data.content) {
                setPosts(data.content);
            }
        } catch (error) {
            console.error('Failed to fetch posts:', error);
        } finally {
            setLoading(false);
        }
    }

    async function handleDeletePost(postId: number) {
        if (!window.confirm('Are you sure you want to delete this post?')) {
            return;
        }
        try {
            await deletePost(postId);
            setPosts(prev => prev.filter(p => p.id !== postId));
        } catch (error) {
            console.error('Failed to delete post:', error);
        }
    }

    useEffect(() => {
        getPosts();
    }, [page, pageSize]);

    return (
        <DataGrid
            rows={posts}
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