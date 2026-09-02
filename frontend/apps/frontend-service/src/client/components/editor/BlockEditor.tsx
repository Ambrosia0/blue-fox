import { Spacer } from "@editor/components/tiptap-ui-primitive/spacer"
import { Toolbar, ToolbarGroup, ToolbarSeparator } from "@editor/components/tiptap-ui-primitive/toolbar"
import { UndoRedoButton } from "@editor/components/tiptap-ui/undo-redo-button"
import { useIsMobile } from "@editor/hooks/use-mobile"
import { EditorContent, EditorContext, useEditor } from "@tiptap/react"

import StarterKit from "@tiptap/starter-kit"
import HorizontalRule from "@tiptap/extension-horizontal-rule"
import TextAlign from "@tiptap/extension-text-align"
import { TaskItem, TaskList } from "@tiptap/extension-list"
import Typography from "@tiptap/extension-typography"
import Superscript from "@tiptap/extension-superscript"
import Subscript from "@tiptap/extension-subscript"
import { ImageUploadNode } from "@editor/components/tiptap-node/image-upload-node"
import { handleImageUpload, MAX_FILE_SIZE } from "@editor/lib/tiptap-utils"

import { HighlighterIcon } from "@editor/components/tiptap-icons/highlighter-icon"
import {
  ColorHighlightPopover,
  ColorHighlightPopoverContent,
  ColorHighlightPopoverButton,
} from "@editor/components/tiptap-ui/color-highlight-popover"

import { useCursorVisibility } from "@editor/hooks/use-cursor-visibility"

import { useWindowSize } from "@editor/hooks/use-window-size"
import Highlight from "@tiptap/extension-highlight"
import Image from "@tiptap/extension-image"
import { Dropcursor, Selection } from "@tiptap/extensions"
import { ThemeToggle } from "@editor/components/tiptap-templates/simple/theme-toggle"
import { HeadingDropdownMenu } from "@editor/components/tiptap-ui/heading-dropdown-menu"
import { ListDropdownMenu } from "@editor/components/tiptap-ui/list-dropdown-menu"
import { BlockquoteButton } from "@editor/components/tiptap-ui/blockquote-button"
import { CodeBlockButton } from "@editor/components/tiptap-ui/code-block-button"
import { MarkButton } from "@editor/components/tiptap-ui/mark-button"
import { LinkButton, LinkContent, LinkPopover } from "@editor/components/tiptap-ui/link-popover"
import { TextAlignButton } from "@editor/components/tiptap-ui/text-align-button"
import { ImageUploadButton } from "@editor/components/tiptap-ui/image-upload-button"

import "@editor/components/tiptap-node/blockquote-node/blockquote-node.scss"
import "@editor/components/tiptap-node/code-block-node/code-block-node.scss"
import "@editor/components/tiptap-node/horizontal-rule-node/horizontal-rule-node.scss"
import "@editor/components/tiptap-node/list-node/list-node.scss"
import "@editor/components/tiptap-node/image-node/image-node.scss"
import "@editor/components/tiptap-node/heading-node/heading-node.scss"
import "@editor/components/tiptap-node/paragraph-node/paragraph-node.scss"

import "@editor/components/tiptap-templates/simple/simple-editor.scss"

import "./styles/block-bar.css"
import "./styles/_variables.scss"
import "./styles/_keyframe-animations.scss"

import { ContentSaver } from "./extensions/ContentSaver"
import { ContentSaverButton } from "./extensions/view/ContentSaverButton"
import { Button } from "@editor/components/tiptap-ui-primitive/button"
import { ArrowLeftIcon } from "@editor/components/tiptap-icons/arrow-left-icon"
import { LinkIcon } from "@editor/components/tiptap-icons/link-icon"
import ContentPublisher from "./extensions/ContentPublisher"
import ContentDeleter from "./extensions/ContentDeleter"
import React, { useEffect, useRef, useState } from "react"
import { useFetcher } from "react-router"
import { PostEditorContent } from "@services/user/userEditorApi"
import { enqueueSnackbar } from "notistack"
import { ImagePlusIcon } from "@editor/components/tiptap-icons/image-plus-icon"
import { ImageAlignButtonGroup } from "./extensions/view/ImageAlignButtonGroup"
import { ImageInsertButton } from "./extensions/view/ImageInsertButton"
import { ImageAlignmentExtension } from "./extensions/ImageAlignmentExtension"
import { AttachmentList } from "./extensions/ImageDropdown"
// import { ImageAddButton } from "./extensions/ImageAddButton"

const MainToolbarContent = ({
    onHighlighterClick,
    onLinkClick,
    isMobile,
    postId,
}: {
    onHighlighterClick: () => void
    onLinkClick: () => void
    isMobile: boolean
    postId: number
}) => {
    return (
        <>
            <ToolbarGroup>
                <ContentPublisher postId={postId} />
                <ContentDeleter />
            </ToolbarGroup>
            <Spacer />

            <ToolbarGroup>
                <UndoRedoButton action="undo" />
                <UndoRedoButton action="redo" />
            </ToolbarGroup>

            <ToolbarSeparator />

            <ToolbarGroup>
                <HeadingDropdownMenu levels={[1, 2, 3, 4]} portal={isMobile} />
                <ListDropdownMenu
                    types={["bulletList", "orderedList", "taskList"]}
                    portal={isMobile}
                />
                <BlockquoteButton />
                <CodeBlockButton />
            </ToolbarGroup>

            <ToolbarSeparator />

            <ToolbarGroup>
                <MarkButton type="bold" />
                <MarkButton type="italic" />
                <MarkButton type="strike" />
                <MarkButton type="code" />
                <MarkButton type="underline" />
                {!isMobile ? (
                    <ColorHighlightPopover />
                ) : (
                    <ColorHighlightPopoverButton onClick={onHighlighterClick} />
                )}
                {!isMobile ? <LinkPopover /> : <LinkButton onClick={onLinkClick} />}
            </ToolbarGroup>

            <ToolbarSeparator />

            <ToolbarGroup>
                <MarkButton type="superscript" />
                <MarkButton type="subscript" />
            </ToolbarGroup>

            <ToolbarSeparator />

            <ToolbarGroup>
                <TextAlignButton align="left" />
                <TextAlignButton align="center" />
                <TextAlignButton align="right" />
                <TextAlignButton align="justify" />
            </ToolbarGroup>

            <ToolbarSeparator />
            <ImageAlignButtonGroup />

            <ToolbarGroup>
                {/* <ImageAddButton /> */}
                <ImageInsertButton text="Insert"/>
                <ImageUploadButton text="Add" />
            </ToolbarGroup>

            <ToolbarSeparator />
            <AttachmentList postId={postId}/>

            <ContentSaverButton />
            <ThemeToggle />
            <Spacer />

            {isMobile && <ToolbarSeparator />}
        </>
    )
}

const MobileToolbarContent = ({
    type,
    onBack,
}: {
    type: "highlighter" | "link"
    onBack: () => void
}) => (
    <>
        <ToolbarGroup>
            <Button data-style="ghost" onClick={onBack}>
                <ArrowLeftIcon className="tiptap-button-icon" />
                {type === "highlighter" ? (
                    <HighlighterIcon className="tiptap-button-icon" />
                ) : (
                    <LinkIcon className="tiptap-button-icon" />
                )}
            </Button>
        </ToolbarGroup>

        <ToolbarSeparator />

        {type === "highlighter" ? (
            <ColorHighlightPopoverContent />
        ) : (
            <LinkContent />
        )}
    </>
)


 // list in the right side of the screen with uploaded files
export function BlockEditor(postData: PostEditorContent) {
    const isMobile = useIsMobile()
    const windowSize = useWindowSize()
    const [title, setTitle] = useState<string>(postData.title);
    const [editing, setEditing] = useState<boolean>(false);
    const [isSaved, setIsSaved] = useState<boolean>(true);
    const editorStateRef = useRef({
        title: postData.title,
        content: postData.content
    });

    const fetcher = useFetcher();
    const toolbarRef = React.useRef<HTMLDivElement>(null)
    const [mobileView, setMobileView] = React.useState<
        "main" | "highlighter" | "link"
    >("main")


    const editor = useEditor({
        immediatelyRender: false,
        shouldRerenderOnTransaction: false,
        editorProps: {
            attributes: {
                autocomplete: "off",
                autocorrect: "off",
                autocapitalize: "off",
                "aria-label": "Main content area, start typing to enter text.",
                class: "simple-editor",
            },
        },
        onUpdate: ({ transaction }) => {
            if (transaction.docChanged)
                setIsSaved(false);
        },
        extensions: [
            StarterKit.configure({
                horizontalRule: false,
                link: {
                    openOnClick: false,
                    enableClickSelection: true,
                },
            }),
            HorizontalRule,
            TextAlign.configure({ types: ["heading", "paragraph"] }),
            TaskList,
            TaskItem.configure({ nested: true }),
            Highlight.configure({ multicolor: true }),
            Image.configure({
                // inline: false,
                // resize: {
                //     enabled: true,
                //     directions: ['bottom', "left", "right", "top", "bottom-left", "bottom-right", "top-left", "top-right"]
                // }
            }),
            Typography,
            Superscript,
            Subscript,
            Selection,
            ContentSaver.configure({
                save: (content: string) =>{
                    const title = editorStateRef.current.title;
                    fetcher.submit(
                        {
                            contentTitle: title, 
                            content: content
                        },
                        {
                            method: 'PATCH', 
                            action: `/editor/${postData.id}`
                        }
                    )
                    .then(
                        _ => {setIsSaved(true); enqueueSnackbar("Content saved!", { variant: 'success'})}, 
                        _ => enqueueSnackbar("Can't save content!", { variant: 'error'})
                    );
                },
            }),
            ImageUploadNode.configure({
                accept: "image/*",
                maxSize: MAX_FILE_SIZE,
                limit: 3,
                upload: (file, onProgress, abortSignal) => handleImageUpload(file, postData.id, onProgress, abortSignal),
                onError: (error) => {
                    console.error(error);
                    enqueueSnackbar("Upload failed:", {variant: 'error'});
                },
            }),
            ImageAlignmentExtension,
            Dropcursor,
        ],
        content: (() => {
            try {
                return JSON.parse(postData.content);
            } catch {
                return ""
            }
        })(),
    })

    const bodyRect = useCursorVisibility({
        editor,
        overlayHeight: toolbarRef.current?.getBoundingClientRect().height ?? 0,
    })

    useEffect(() => {
        if (!isMobile && mobileView !== "main") {
            setMobileView("main")
        }
    }, [isMobile, mobileView])

    const handleTitleChange = (change: string) =>{
        editorStateRef.current.title = change;
        setTitle(change);
        setIsSaved(false);
    }

    return (
        <div className="simple-editor-wrapper" style={{ padding: '24px clamp(16px, 5vw, 48px)' }}>
            <EditorContext.Provider value={{ editor }}>

                <div
                    style={{ display: 'flex', alignItems: 'center', gap: '8px', marginBottom: '18px' }}
                    onDoubleClick={() => setEditing(true)}
                    onClick={() => isMobile? setEditing(true): ""}>
                {editing ? (
                    <input
                    value={title}
                    onChange={(e) => handleTitleChange(e.target.value)}
                    onBlur={() => setEditing(false)}
                    autoFocus
                    onKeyDown={(e) => {
                        if (e.key === 'Enter') e.currentTarget.blur();
                        if (e.key === 'Escape') setEditing(false);
                    }}
                    style={{
                        fontSize: '2rem',
                        fontWeight: 700,
                        color: 'var(--text-color)',
                        background: 'transparent',
                        border: 'none',
                        outline: 'none',
                        padding: 0,
                        margin: 0,
                        width: 'auto',
                        minWidth: '2ch',
                        fontFamily: 'inherit',
                        lineHeight: 1.2,
                    }}
                    />
                ) : (
                    <h1
                    style={{
                        fontSize: '2rem',
                        fontWeight: 700,
                        color: 'var(--text-color)',
                        margin: 0,
                        padding: 0,
                        cursor: 'text',
                        userSelect: 'text',
                        lineHeight: 1.2,
                    }}
                    >
                    {title}
                    </h1>
                )}

                {!isSaved && (
                    <span
                        title="Unsaved changes"
                        style={{
                            width: 6,
                            height: 6,
                            borderRadius: '50%',
                            background: '#ff9f0a',
                            flexShrink: 0,
                            alignSelf: 'center',
                        }}/>
                )}
                </div>

                <Toolbar
                    ref={toolbarRef}
                    style={
                        isMobile? 
                            { bottom: `calc(100% - ${windowSize.height - bodyRect.y}px)`}: 
                            {}
                    }>
                {mobileView === 'main'?
                    <MainToolbarContent
                        onHighlighterClick={() => setMobileView('highlighter')}
                        onLinkClick={() => setMobileView('link')}
                        isMobile={isMobile}
                        postId={postData.id}/>:
                    <MobileToolbarContent
                        type={mobileView === 'highlighter'? 'highlighter' : 'link'}
                        onBack={() => setMobileView('main')}/>
                }
                </Toolbar>

                <EditorContent
                    editor={editor}
                    role="presentation"
                    className="simple-editor-content"/>
            </EditorContext.Provider>
        </div>
    )
}

export default BlockEditor;