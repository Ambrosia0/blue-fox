import { DataGrid, type GridColDef, type GridRenderCellParams } from '@mui/x-data-grid';
import { useEffect, useState } from 'react';
import { IconButton, Tooltip, Button, Box, Typography } from '@mui/material';
import CheckCircleIcon from '@mui/icons-material/CheckCircle';
import FilterListIcon from '@mui/icons-material/FilterList';
import {
    getReports,
    closeRequest,
    getReasons,
    createReason,
    type Report,
    type ReportFilter,
    type ReportReason,
} from '../../../services/api/adminReportApi';

type SortOrder = 'ASC' | 'DESC';

export const ReportControl = () => {
    const [reports, setReports] = useState<Report[]>([]);
    const [page, setPage] = useState(0);
    const [pageSize, setPageSize] = useState(20);
    const [loading, setLoading] = useState(false);
    const [filters, setFilters] = useState<ReportFilter>({});
    const [showFilters, setShowFilters] = useState(false);
    const [reasons, setReasons] = useState<ReportReason[]>([]);
    const [reasonLoading, setReasonLoading] = useState(false);

    const targetTypeMap: Record<string, string> = {
        POST: 'Пост',
        USER: 'Пользователь',
        COMMENT: 'Комментарий',
        COMMUNITY: 'Сообщество',
    };

    const statusMap: Record<string, string> = {
        OPEN: 'Открыт',
        CLOSE: 'Закрыт',
    };

    const statusColorMap: Record<string, string> = {
        OPEN: 'warning',
        CLOSE: 'success',
    };

    const columns: GridColDef[] = [
        {
            field: 'id',
            headerName: 'ID',
            width: 100,
            flex: 0.5,
        },
        {
            field: 'username',
            headerName: 'Заявитель',
            width: 150,
            flex: 0.8,
        },
        {
            field: 'reportReasonId',
            headerName: 'Причина',
            width: 120,
            flex: 0.6,
            valueGetter: (_: unknown, row: Report) => row.reportReasonId,
            renderCell: (params: GridRenderCellParams<Report>) => {
                const reason = reasons.find((r) => r.id === params.row.reportReasonId);
                return reason ? `#${reason.id}` : String(params.row.reportReasonId);
            },
        },
        {
            field: 'reportContent',
            headerName: 'Содержание',
            minWidth: 250,
            flex: 1.5,
            renderCell: (params: GridRenderCellParams<Report>) => (
                <Tooltip title={String(params.value)}>
                    <Typography variant="body2" noWrap sx={{ cursor: 'pointer' }}>
                        {String(params.value)}
                    </Typography>
                </Tooltip>
            ),
        },
        {
            field: 'targetType',
            headerName: 'Цель',
            width: 120,
            flex: 0.6,
            valueGetter: (_: unknown, row: Report) => targetTypeMap[row.targetType] || row.targetType,
        },
        {
            field: 'reportedContentKey',
            headerName: 'Контент',
            minWidth: 150,
            flex: 1,
            renderCell: (params: GridRenderCellParams<Report>) => (
                <Tooltip title={String(params.value)}>
                    <Typography variant="body2" sx={{ color: 'primary.main', textDecoration: 'underline' }}>
                        {String(params.value)}
                    </Typography>
                </Tooltip>
            ),
        },
        {
            field: 'status',
            headerName: 'Статус',
            width: 110,
            flex: 0.5,
            renderCell: (params: GridRenderCellParams<Report>) => (
                <Button
                    size="small"
                    color={statusColorMap[String(params.value)] as 'warning' | 'success' | undefined}
                    variant="outlined"
                    sx={{ textTransform: 'none', minWidth: 0 }}
                >
                    {statusMap[String(params.value)] || String(params.value)}
                </Button>
            ),
        },
        {
            field: 'createdAt',
            headerName: 'Создан',
            width: 150,
            flex: 0.7,
            valueGetter: (_: unknown, row: Report) => new Date(row.createdAt).toLocaleString('ru-RU'),
        },
        {
            field: 'actions',
            headerName: 'Действия',
            width: 80,
            flex: 0.4,
            sortable: false,
            renderCell: (params: GridRenderCellParams<Report>) => (
                <Tooltip title="Закрыть отчет">
                    <IconButton
                        size="small"
                        color="success"
                        onClick={() => handleCloseReport(params.row.id)}
                        disabled={params.row.status === 'CLOSE'}
                    >
                        <CheckCircleIcon fontSize="small" />
                    </IconButton>
                </Tooltip>
            ),
        },
    ];

    async function getReportsList() {
        setLoading(true);
        try {
            const result = await getReports({ page, size: pageSize }, filters);
            const data = result as any;
            if (data && data.content) {
                setReports(data.content);
            }
        } catch (error) {
            console.error('Failed to fetch reports:', error);
        } finally {
            setLoading(false);
        }
    }

    async function getReasonsList() {
        setReasonLoading(true);
        try {
            const result = await getReasons();
            setReasons(result);
        } catch (error) {
            console.error('Failed to fetch reasons:', error);
        } finally {
            setReasonLoading(false);
        }
    }

    async function handleCloseReport(id: string) {
        if (!window.confirm('Вы уверены, что хотите закрыть этот отчет?')) {
            return;
        }
        try {
            await closeRequest(id);
            setReports((prev) => prev.map((r) => (r.id === id ? { ...r, status: 'CLOSE' } : r)));
        } catch (error) {
            console.error('Failed to close report:', error);
        }
    }

    function handleFilterChange(filterKey: keyof ReportFilter, value: string | undefined) {
        setFilters((prev) => ({ ...prev, [filterKey]: value || undefined }));
        setPage(0);
    }

    function handleResetFilters() {
        setFilters({});
        setPage(0);
    }

    useEffect(() => {
        getReportsList();
    }, [page, pageSize, filters]);

    useEffect(() => {
        getReasonsList();
    }, []);

    return (
        <Box sx={{ width: '100%', height: '100%' }}>
            {/* Filters */}
            <Box sx={{ display: 'flex', gap: 1, mb: 2, p: 1, alignItems: 'center' }}>
                <Button
                    size="small"
                    variant={showFilters ? 'contained' : 'outlined'}
                    startIcon={<FilterListIcon />}
                    onClick={() => setShowFilters((prev) => !prev)}
                >
                    Фильтры
                </Button>

                {showFilters && (
                    <>
                        <Box sx={{ display: 'flex', alignItems: 'center', gap: 1 }}>
                            <Typography variant="body2">Статус:</Typography>
                            <select
                                value={filters.status || ''}
                                onChange={(e) => handleFilterChange('status', e.target.value)}
                                style={{ padding: '4px 8px', borderRadius: '4px', border: '1px solid #ccc' }}
                            >
                                <option value="">Все</option>
                                <option value="OPEN">Открыт</option>
                                <option value="CLOSE">Закрыт</option>
                            </select>
                        </Box>

                        <Box sx={{ display: 'flex', alignItems: 'center', gap: 1 }}>
                            <Typography variant="body2">Цель:</Typography>
                            <select
                                value={filters.targetType || ''}
                                onChange={(e) => handleFilterChange('targetType', e.target.value)}
                                style={{ padding: '4px 8px', borderRadius: '4px', border: '1px solid #ccc' }}
                            >
                                <option value="">Все</option>
                                <option value="POST">Пост</option>
                                <option value="USER">Пользователь</option>
                                <option value="COMMENT">Комментарий</option>
                                <option value="COMMUNITY">Сообщество</option>
                            </select>
                        </Box>

                        <Box sx={{ display: 'flex', alignItems: 'center', gap: 1 }}>
                            <Typography variant="body2">Сортировка:</Typography>
                            <select
                                value={filters.direction || 'ASC'}
                                onChange={(e) => handleFilterChange('direction', e.target.value)}
                                style={{ padding: '4px 8px', borderRadius: '4px', border: '1px solid #ccc' }}
                            >
                                <option value="ASC">По возрастанию</option>
                                <option value="DESC">По убыванию</option>
                            </select>
                        </Box>

                        <Button size="small" color="secondary" onClick={handleResetFilters}>
                            Сбросить
                        </Button>
                    </>
                )}
            </Box>

            {/* Data Grid */}
            <DataGrid
                rows={reports}
                columns={columns}
                loading={loading || reasonLoading}
                pageSizeOptions={[10, 20, 50]}
                paginationMode="server"
                onPaginationModelChange={(newModel) => {
                    setPage(newModel.page);
                    setPageSize(newModel.pageSize);
                }}
                sortingMode="server"
                onSortModelChange={() => {}}
                density="compact"
                disableColumnResize
                sx={{
                    '& .MuiDataGrid-cell': {
                        borderBottom: '1px solid rgba(224, 224, 224, 1)',
                    },
                }}
            />
        </Box>
    );
};