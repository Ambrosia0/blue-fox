
import { useLoaderData } from "react-router";
import { BlockEditor } from "./BlockEditor"
import { SnackbarProvider} from 'notistack';
import { PostEditorContent } from "../../services/user/userEditorApi";

export const PostEditor = () =>{

  const postData = useLoaderData<PostEditorContent>();
  return(
    <SnackbarProvider 
      autoHideDuration={3000} 
      anchorOrigin={{vertical: 'top', horizontal: 'right'}}>
      <BlockEditor {...postData} />
    </SnackbarProvider>
  )
}