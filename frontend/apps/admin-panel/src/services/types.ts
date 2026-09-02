export type ImageResponse = {
    id: number;
    userId: number;
    data: Blob;
    createdAt: string;
}

export interface Page<T>{
    content: T[],
    empty: boolean,
    first: boolean,
    last: boolean,
    number: number,
    numberOfElements: number,
    pageable: Pageable,
    size: number,
    totalElements: number,
    totalPages: number
}

export interface Slice<T>{
    content: T[];
    hasNext: boolean;
    hasPrevious: boolean;
    number: number;
    size: number;
}

export type Pageable = {
    page: number,
    sort?: string,
    size: number,
}