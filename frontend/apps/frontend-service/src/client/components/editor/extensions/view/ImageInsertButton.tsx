import { Button, ButtonProps } from "@editor/components/tiptap-ui-primitive/button"
import { useListDropdownMenu } from "@editor/components/tiptap-ui/list-dropdown-menu/use-list-dropdown-menu"
import { useTiptapEditor } from "@editor/hooks/use-tiptap-editor"
import type { Editor } from "@tiptap/react"
import React, { useCallback } from "react"

export interface AlignGroupConfig{
    editor?: Editor | null,
    hideWhenUnavailable?: boolean,
    onToggled?: () => void,
    text?: string
}

export interface AlignGroupProps 
    extends Omit<ButtonProps, "type">,
            AlignGroupConfig{
    showShortcut?: boolean,
}

export const ImageInsertButton = React.forwardRef<
  HTMLDivElement,
  AlignGroupProps
>(({
  editor: providedEditor,
  text="",
  hideWhenUnavailable = false,
  showShortcut = false,
  ...buttonProps
}, ref) => {
    const { editor } = useTiptapEditor(providedEditor);
    const { canToggle } = useListDropdownMenu({})

  if (!editor || !editor.isEditable) return null

  const addImage = () =>{
    const url = window.prompt('URL');
    if(url){
        editor.chain().focus().setImage({ src: url, align: 'center' }).run();
    }
  };

  return (
    <Button
      type="button"
      data-style={'ghost'}
      onClick={addImage}
      disabled={!canToggle}
      {...buttonProps}
    >
      {text}
    </Button>
  )
})