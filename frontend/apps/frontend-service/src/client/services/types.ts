export interface Slice<T>{
    content: T[];
    hasNext: boolean;
    hasPrevious: boolean;
    number: number;
    size: number;
}

export type UserResponse = {
    id: string;
    username: string;
    firstName: string;
    lastName: string;
    avatarId: string;
}