import { apiClient } from "../services/apiClient";
import { createContext, useContext, useEffect, useMemo, useState } from "react";
import { UserInfo } from "../types/user";
import { useAuth } from "./AuthContext";

type Notification = {
    eventTarget: EventTarget,
    setFilter: (filter: EventFilter) => void
}

type EventFilter = {
    postId: number | null;
}

export type Event = CommentNotification | PostNotification | CommentLikeNotification | PostLikeNotification;

export type CommentNotification = {
    type: typeof NotificationType.CommentNotification;
    id: number;
    postId: number;
    user: UserInfo;
    content: string;
    parentComment?: number,
    createdAt: string,
    attachmentUrl?: string
}

type PostNotification = {
    type: typeof NotificationType.PostNotification,
    user: UserInfo
}

type CommentLikeNotification = {
    type: typeof NotificationType.CommentLike
}

type PostLikeNotification = {
    type: typeof NotificationType.PostLike,
    likes: Record<number, number>
}

export const NotificationType = {
    CommentNotification: 'COMMENT_NOTIFICATION',
    PostNotification: 'POST_NOTIFICATION',
    CommentLike: 'COMMENT_LIKE',
    PostLike: 'POST_LIKE',
    CommentCreation: 'COMMENT_CREATION'
} as const;

export type NotificationType = typeof NotificationType[keyof typeof NotificationType];

export type NotificationPayload = {
    [NotificationType.CommentNotification]: CommentNotification,
    [NotificationType.CommentLike]: CommentLikeNotification,
    [NotificationType.PostLike]: PostLikeNotification,
    [NotificationType.PostNotification]: PostNotification
}


const NotificationContext = createContext<Notification | null>(null);

export const useNotification = () => useContext(NotificationContext);

export const NotificationProvider: React.FC<{children: React.ReactNode}> = ({children}) =>{
    const auth = useAuth();
    const [filter, setFilter] = useState<EventFilter | undefined>(undefined);
    const [eventSource, setEventSource] = useState<EventSource>();
    const [initId, setInitId] = useState<string | undefined>(undefined);
    const eventTarget = useMemo(() => new EventTarget(), []);

    useEffect(() =>{
        const endpoint = auth.user? "user": "public";
        const url = `api/${endpoint}/notification`;
        setEventSource(new EventSource(`${import.meta.env.VITE_API_URL}/${url}`));
        if(!eventSource){
            return;
        }
        eventSource.addEventListener('notification', (event) =>{
            const data = event.data as Event;
            eventTarget.dispatchEvent(new MessageEvent(data.type, {data: data}));
        })

        eventSource.addEventListener('init', (event) => setInitId(event.data));
        eventSource.addEventListener('ping', () =>{});

        eventSource.onerror = (error) =>{
            console.error("Sse error!", error);
        }
        return () => eventSource.close();
    }, [auth.user, eventTarget]);

    useEffect(() =>{
        const endpoint = auth.user? "user": "public";
        const url = `/api/${endpoint}/notification`
        
        apiClient.patch(url, {
            params:{
                userId: auth.user? undefined: initId,
                ...filter
            }
        });
    }, [filter])

    return(
        <NotificationContext.Provider value={{eventTarget: eventTarget, setFilter: setFilter}}>
            {children}
        </NotificationContext.Provider>
    );
}