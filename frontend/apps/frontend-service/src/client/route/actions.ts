import axios from "axios";
import type { ActionFunctionArgs } from "react-router";
import { likePost, unlikePost } from "../services/user/userPostApi";
import { saveContent } from "../services/user/userEditorApi";

export async function postLikeAction({params, request}: ActionFunctionArgs){
    const formData = await request.formData();
    const postId = Number(params.postId);
    const state = Boolean(formData.get("state"));

    try {
        if(state){
            await unlikePost(postId);
        }else{
            await likePost(postId);
        }
        return JSON.stringify({ liked: !state});
    } catch (error) {
        if(axios.isAxiosError(error)){
            throw new Response(error.response?.data, {status: error.response?.status ?? 500})
        }
    }
}

export async function editorPostSave({params, request}: ActionFunctionArgs) {
    const formData = await request.formData();
    const postId = Number(params.postId);
    try {
        const title = formData.get("contentTitle") as string;
        const content = formData.get("content") as string;
        await saveContent(postId, title, content);
        return JSON.stringify({id: postId, title: title, content: content, updatedAt: new Date().toISOString()});
    } catch (error) {
        if(axios.isAxiosError(error)){
            const data = error.response?.data;
            throw new Response(JSON.stringify(data), {status: error.response?.status ?? 500});
        }
    }
}