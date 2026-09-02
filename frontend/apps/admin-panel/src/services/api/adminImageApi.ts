import { apiClient } from "../apiClient";

export type ImageResponse = {
    id: number;
    userId: string;
    data: Blob;
    createdAt: string;
}

export async function fetchImages(page: number = 0, pageSize: number = 20) {
    return apiClient.get<ImageResponse[]>('/api/admin/image', {
        params: {
            page: page,
            size: pageSize
        }
    });
}

export async function deleteImage(id: number) {
    return apiClient.delete(`/api/admin/image/${id}`);
}