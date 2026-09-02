export interface Slice<T>{
    content: T[];
    hasNext: boolean;
    hasPrevious: boolean;
    number: number;
    size: number;
}