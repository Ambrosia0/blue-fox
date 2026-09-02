import { generateHTML } from "@tiptap/html";
import { TaskItem, TaskList } from "@tiptap/extension-list"
import Typography from "@tiptap/extension-typography"
import Superscript from "@tiptap/extension-superscript"
import Subscript from "@tiptap/extension-subscript"
import TextAlign from "@tiptap/extension-text-align"
import Highlight from "@tiptap/extension-highlight"
import Image from "@tiptap/extension-image"
import { Selection } from "@tiptap/extensions"
import { useMemo } from "react";
import StarterKit from "@tiptap/starter-kit";

const PostBody = ({doc}: {doc: string}) =>{
    const output = useMemo(() => {
        return generateHTML(JSON.parse(doc), [
            StarterKit,
            TaskItem,
            TaskList,
            Typography,
            Superscript,
            Subscript,
            TextAlign,
            Highlight,
            Image,
            Selection
        ])
    }, [])
    return(
        <div
            dangerouslySetInnerHTML={{__html: output}}
        />
    )
}

export default PostBody;