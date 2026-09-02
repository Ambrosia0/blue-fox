import { useEffect, useState } from "react"
import { IconButton, ImageList, ImageListItem, ImageListItemBar, CircularProgress, Box } from "@mui/material";
import DeleteIcon from '@mui/icons-material/Delete';
import { fetchImages, deleteImage as apiDeleteImage, type ImageResponse } from '../../../services/api/adminImageApi';

export const ImageControl = () =>{
    const [images, setImages] = useState<ImageResponse[]>([]);
    const [page, setPage] = useState(0);
    const [pageSize] = useState(20);
    const [loading, setLoading] = useState(false);
    const [hasMore, setHasMore] = useState(true);

    async function getImages(page: number, pageSize: number = 20) {
        setLoading(true);
        try {
            const result = await fetchImages(page, pageSize);
            const data = result.data as any;
            const newImages = Array.isArray(data) ? data : (data && data.content) ? data.content : [];
            
            if (newImages.length === 0) {
                setHasMore(false);
            } else {
                setImages(prev => [...prev, ...newImages]);
            }
        } catch (error) {
            console.error('Failed to fetch images:', error);
        } finally {
            setLoading(false);
        }
    }

    async function handleDeleteImage(id: number) {
        if (!window.confirm('Are you sure you want to delete this image?')) {
            return;
        }
        try {
            await apiDeleteImage(id);
            setImages(prev => prev.filter(img => img.id !== id));
        } catch (error) {
            console.error('Failed to delete image:', error);
        }
    }

    useEffect(() => {
        getImages(page, pageSize);
    }, [])

    const loadMore = () => {
        if (!loading && hasMore) {
            const nextPage = page + 1;
            setPage(nextPage);
            getImages(nextPage, pageSize);
        }
    };

    return (
        <Box>
            <ImageList variant="masonry" cols={6} gap={20}>
                {images.map(item => (
                    <ImageListItem key={item.id}>
                        <img 
                            src={item.data ? URL.createObjectURL(item.data) : ''} 
                            alt="" 
                            loading="lazy"
                            style={{ width: '100%', height: 'auto', objectFit: 'cover' }}
                        />
                        <ImageListItemBar
                            title={item.id.toString()}
                            subtitle={item.userId}
                            actionIcon={
                                <IconButton size="small" onClick={() => handleDeleteImage(item.id)}>
                                    <DeleteIcon />
                                </IconButton>
                            }
                        />
                    </ImageListItem>
                ))}
            </ImageList>
            {loading && (
                <Box display="flex" justifyContent="center" p={2}>
                    <CircularProgress size={20} />
                </Box>
            )}
            {hasMore && !loading && (
                <Box display="flex" justifyContent="center" p={2}>
                    <IconButton onClick={loadMore}>Load More</IconButton>
                </Box>
            )}
            {!hasMore && images.length > 0 && (
                <Box display="flex" justifyContent="center" p={2} color="text.secondary">
                    No more images
                </Box>
            )}
        </Box>
    )
}