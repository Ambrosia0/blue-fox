import axios from "axios";
import { userManager } from "../auth";
import { enqueueSnackbar } from "notistack";

export const AVATAR_ENDPOINT = typeof window !== 'undefined'? 
    import.meta.env.AVATAR_ENDPOINT: 
    ""

type ProblemDetail = {
    status?: number,
    title?: string,
    detail?: string,
    instance?: string,
}

export const apiClient = typeof window !== 'undefined'?
    axios.create({
    baseURL: import.meta.env.VITE_API_URL,
    withCredentials: true,
    }):
    null;

export async function getAccessToken() {
    const user = await userManager.getUser();
    if(!user || user.expired){
        return null;
    }
    return user.access_token;
}

function parseRfc9457Error(
    error: { 
        response?: {
            data?: unknown
        }
    }
): ProblemDetail | undefined{
    return error.response?.data as ProblemDetail | undefined;
}

if(apiClient !== null){
    apiClient.interceptors.request.use(async (config) =>{
        const token = await getAccessToken();
        if(token){
            config.headers.Authorization = `Bearer ${token}`;
        }
        return config;
    });
    apiClient.interceptors.response.use(
        request => request,
        (error) =>{
            const parsedError = parseRfc9457Error(error);
            enqueueSnackbar({
                variant: 'error',
                message: parsedError.detail
            })
            return Promise.reject(error);
        }
    )
}

// export const apiClient = typeof window === 'undefined'?
//     null:
//     (await import("axios")).default.create({
//         baseURL:
//             typeof window !== 'undefined'? 
//                 location.origin:
//                 import.meta.env.PUBLIC_ORIGIN ?? 'http://localhost:44556',
//         withCredentials: true
//     })

// axios.create({
    // baseURL:
    //     typeof window !== 'undefined'? 
    //         location.origin:
    //         import.meta.env.PUBLIC_ORIGIN ?? 'http://localhost:3000'
// })

// apiClient.interceptors.request.use((config) => {
//     const token = localStorage.getItem('jwt');
//     if(token) config.headers.Authorization = `Bearer ${token}`;
//     return config;
// })