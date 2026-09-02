import axios from "axios";
import { userManager } from "../App";

export const AVATAR_ENDPOINT = typeof window !== 'undefined'? (location.origin).concat("/api/file") : ""

export const apiClient = axios.create({
  baseURL: import.meta.env.VITE_API_URL,
  withCredentials: true,
});

export async function getAccessToken() {
    const user = await userManager.getUser();
    if(!user || user.expired){
        return null;
    }
    return user.access_token;
}

apiClient.interceptors.request.use(async (config) =>{
    const token = await getAccessToken();
    if(token){
        config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
})