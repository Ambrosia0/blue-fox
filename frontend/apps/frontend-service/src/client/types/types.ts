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

export type Pageable = {
    page: number,
    sort?: string,
    size: number,
}