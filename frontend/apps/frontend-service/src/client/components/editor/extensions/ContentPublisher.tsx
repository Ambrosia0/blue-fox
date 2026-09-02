import { useSnackbar } from "notistack";
import { Button } from "@editor/components/tiptap-ui-primitive/button";
import { useCurrentEditor } from "@tiptap/react";
import { useNavigate } from "react-router";
import { publishPost } from "../../../services/user/userEditorApi";

const ContentPublisher = ({postId}: {postId: number}) =>{
    const {editor} = useCurrentEditor();
    const navigate = useNavigate();
    const snackbar = useSnackbar();
    

    return(
        <Button 
            tooltip="Publish" 
            data-style="ghost" 
            type="button" 
            tabIndex={-1} 
            onClick={async () => {
                if(editor?.getHTML()!==undefined){
                    try {
                        await publishPost(postId);
                        snackbar.enqueueSnackbar("Published!");
                        editor.setEditable(false);
                        navigate(`/post/${postId}`)
                    } catch (error) {
                        snackbar.enqueueSnackbar("Can't publish post!");
                    }
                }
            }}>
            <svg fillRule="evenodd" clipRule='evenodd' fill="currentColor" width="25px" height="25px" viewBox="0 0 1920 1920" xmlns="http://www.w3.org/2000/svg">
                <path d="M860.16 1373.227 490.773 1003.84 641.6 853.013l218.56 218.56 453.653-453.653 150.827 150.827-604.48 604.48ZM960 0C429.76 0 0 429.76 0 960s429.76 960 960 960c530.133 0 960-429.76 960-960S1490.133 0 960 0Z" fill-rule="evenodd"/>
            </svg>
        </Button>
    )
}

export default ContentPublisher;