import { Button } from "@editor/components/tiptap-ui-primitive/button";
import { useIsMobile } from "@editor/hooks/use-mobile";
import { getAttachedMedia,  deleteAttachment as apiDeleteAttachment, PostAttachment } from "@services/user/userEditorApi";
import { useCurrentEditor } from "@tiptap/react";
import { enqueueSnackbar } from "notistack";
import { useEffect, useRef, useState } from "react";
import { createPortal } from 'react-dom'

export const AttachmentList = ({postId}: {postId: number}) =>{
    const [open, setOpen] = useState<boolean>(false);
    const [attachments, setAttachments] = useState<PostAttachment[]>([]);
    const { editor } = useCurrentEditor();

    const [selectedId, setSelectedId] = useState<string | null>(null);
    const buttonRef = useRef<HTMLButtonElement>(null);
    const mobile = useIsMobile();

    const ArrowIcon = (props: React.SVGProps<SVGSVGElement>) =>(
        <svg
            viewBox="0 0 24 24"
            width="1em"
            height="1em"
            fill="none"
            xmlns="http://www.w3.org/2000/svg"
            {...props}
        >
            <path
            d="M6 9l6 6 6-6"
            stroke="currentColor"
            strokeWidth="2"
            strokeLinecap="round"
            strokeLinejoin="round"
            />
        </svg>
    );

    const TrashIcon = (props: React.SVGProps<SVGSVGElement>) => (
        <svg
            viewBox="0 0 24 24"
            width="16"
            height="16"
            fill="none"
            xmlns="http://www.w3.org/2000/svg"
            stroke="currentColor"
            strokeWidth="1.8"
            strokeLinecap="round"
            strokeLinejoin="round"
            {...props}
        >
            <path d="M4 7h16" />
            <path d="M9 4h6" />
            <path d="M6 7l1 13h10l1-13" />
            <path d="M10 11v6M14 11v6" />
        </svg>
    );

    async function fetchAttachments() {
        try {
            const data = await getAttachedMedia(postId);
            setAttachments(data);
        } catch (error) {
            console.error(error);
            enqueueSnackbar("Can't fetch media attachments!", {variant: 'error'});
        }
    }

    async function deleteAttachment(attachmentId: string) {
        try {
            await apiDeleteAttachment(postId, attachmentId);

            const state = editor!.state;
            const tr = editor!.state.tr;
            const positions: number[] = []
            state.doc.descendants((node, pos) =>{
                const url = location.origin + "/file/"+ attachmentId;
                if(node.type.name === 'image' && node.attrs.src === url){
                    positions.push(pos);
                }
            });

            positions
                .sort((a, b) => b - a)
                .forEach(pos => tr.delete(pos, pos + 1));
            editor?.view.dispatch(tr);
            setAttachments(attachments.filter(val => val.attachmentId === attachmentId))
            enqueueSnackbar("Attachment deleted!", {variant: 'success'});
        } catch (error) {
            console.error(error);
            enqueueSnackbar("Can't delete media!", {variant: 'error'});
        }
    }

    const handleClick = (e: React.MouseEvent<HTMLImageElement>, attachmentId: string) =>{
        if(attachmentId === selectedId){
            const src = location.origin + "/file/" + attachmentId
            editor?.chain().focus().setImage({src}).run();
            return setSelectedId(null);
        } else{
            return setSelectedId(attachmentId);
        }
    }

    useEffect(() =>{
        fetchAttachments()

        const handler = ({transaction}) => {
            const meta = transaction.getMeta('imageUpload')
            if(meta){
                setAttachments(prev => [...prev, {attachmentId: meta, postId: postId}])
            }
        }

        editor?.on('transaction', handler);

        return () =>{
            editor?.off('transaction', handler);
        }
    }, [])

    return(
        <div>
            <Button 
                type="button" 
                data-style={'ghost'} 
                ref={buttonRef}
                onClick={() => setOpen(!open)}
                tooltip="Show uploaded attachments">
                Attachments
                <div style={{
                    transform: `${open? "rotate(180deg)": "rotate(0deg)"}`, 
                    transition: "transform 0.2s ease",
                }}>
                    <ArrowIcon />
                </div>
            </Button>
            {open && createPortal(
                <div style={{
                    position: 'fixed', 
                    top: mobile? 
                        (window.innerHeight - 100)-buttonRef.current!.getBoundingClientRect().height: 
                        window.innerHeight - 200, 
                    left: 0, 
                    display: 'flex', 
                    flexDirection: 'row',
                    overflowX: 'scroll',
                    width: '100%',
                    zIndex: 9999}}>
                    {attachments.map((attach) => {
                        return (
                            <div
                                key={attach.attachmentId}
                                style={{
                                    position: 'relative',
                                    display: 'inline-block'
                                }}
                            >
                                {selectedId === attach.attachmentId && (
                                    <div
                                        style={{
                                            position: 'absolute',
                                            top: 4,
                                            right: 4,
                                            background: 'rgba(0,0,0,0.7)',
                                            borderRadius: 6,
                                            padding: 4,
                                            display: 'flex',
                                            alignItems: 'center',
                                            justifyContent: 'center'
                                        }}
                                    >
                                        <TrashIcon
                                            style={{ cursor: 'pointer' }}
                                            onClick={() =>
                                                deleteAttachment(attach.attachmentId)
                                            }
                                        />
                                    </div>
                                )}

                                <img
                                    style={{
                                        maxWidth: mobile? 150: 300,
                                        maxHeight: mobile? 100: 200,
                                        padding: 1,
                                        border: selectedId === attach.attachmentId
                                            ? '2px solid #1976d2'
                                            : '2px solid transparent',
                                        borderRadius: 6,
                                        transition: '0.15s'
                                    }}
                                    onClick={(e) =>
                                        handleClick(e, attach.attachmentId)
                                    }
                                    src={location.origin + "/file/"+attach.attachmentId}
                                />
                            </div>
                        );
                    })}
                </div>, document.body
            )}
        </div>
    )
}