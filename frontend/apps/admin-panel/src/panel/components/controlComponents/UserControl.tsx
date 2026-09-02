import { Button, Tooltip } from "@mui/material";
import { DataGrid, type GridColDef } from "@mui/x-data-grid";
import { fetchUsers, banUser, unbanUser, type UserInfoResponse } from '../../../services/api/adminProfileApi';
import { useState, useEffect } from "react";

export const UserControl = () =>{
    const [users, setUsers] = useState<UserInfoResponse[]>([]);
    const [page, setPage] = useState(0);
    const [pageSize, setPageSize] = useState(20);
    const [loading, setLoading] = useState(false);

    const columns: GridColDef[] = [
        {field: 'id', headerName: 'ID', width: 100, flex: 0.5},
        {field: 'username', headerName: 'Username', width: 150, flex: 0.8},
        {field: 'email', headerName: 'Email', width: 200, flex: 1},
        {field: 'createdAt', headerName: 'Created at', width: 160, flex: 0.7},
        {field: 'isEnabled', headerName: 'Enabled', width: 100, flex: 0.4,
            renderCell: (params) => params.row.isEnabled ? '✓' : '✗'
        },
        {
            field: 'isActive',
            headerName: 'Status',
            width: 150,
            flex: 0.5,
            renderCell: (params) => {
                if (params.row.isActive) {
                    return (
                        <Tooltip title="Ban user">
                            <Button size="small" color="warning" onClick={() => handleBanUser(params.row.id)}>
                                Ban
                            </Button>
                        </Tooltip>
                    );
                } else {
                    return (
                        <Tooltip title="Unban user">
                            <Button size="small" color="success" onClick={() => handleUnbanUser(params.row.id)}>
                                Unban
                            </Button>
                        </Tooltip>
                    );
                }
            }
        }
    ];

    async function fetchUsersList() {
        setLoading(true);
        try {
            const result = await fetchUsers(page, pageSize);
            const data = result.data;
            if (data && data.content) {
                setUsers(data.content);
            } else if (Array.isArray(data)) {
                setUsers(data);
            }
        } catch (error) {
            console.error('Failed to fetch users:', error);
        } finally {
            setLoading(false);
        }
    }

    async function handleBanUser(userId: string) {
        try {
            await banUser(userId);
            setUsers(prev => prev.map(u => u.id === userId ? { ...u, isActive: false } : u));
        } catch (error) {
            console.error('Failed to ban user:', error);
        }
    }

    async function handleUnbanUser(userId: string) {
        try {
            await unbanUser(userId);
            setUsers(prev => prev.map(u => u.id === userId ? { ...u, isActive: true } : u));
        } catch (error) {
            console.error('Failed to unban user:', error);
        }
    }

    useEffect(() => {
        fetchUsersList();
    }, [page, pageSize]);

    return (
        <DataGrid
            rows={users}
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
}