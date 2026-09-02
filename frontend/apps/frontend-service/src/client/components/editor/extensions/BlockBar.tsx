import { useEffect, useState } from 'react'
import { NodeType } from 'prosemirror-model';
import { useCurrentEditor } from '@tiptap/react';

import "../../styles/block-bar.css"

const BlockBar = () =>{
    const [nodes, setNodes] = useState<NodeType[]>();
    const {editor} = useCurrentEditor();


    useEffect(() =>{
        if(!editor){return}
        const nodeTypes = Object.values(editor.schema.nodes).filter((nodeType) =>{
            return nodeType.spec.group?.split(' ').includes("building-block");
        });
        setNodes(nodeTypes);
    },[editor?.schema.nodes])

    return(
        <div className='block-bar'>
            {nodes?.map((node, index) =>{
                return(
                <div key={index} onClick={() => editor?.commands.insertContent(node.create())}>
                </div>)
            })}
        </div>
    )
}

export default BlockBar;