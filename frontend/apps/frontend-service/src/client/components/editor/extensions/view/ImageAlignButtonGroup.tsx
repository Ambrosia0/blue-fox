import { Button, ButtonProps } from "@editor/components/tiptap-ui-primitive/button";
import { useTiptapEditor } from "@editor/hooks/use-tiptap-editor";
import type { Editor } from "@tiptap/react"
import React from "react";

export interface AlignGroupConfig{
    editor?: Editor | null,
    hideWhenUnavailable?: boolean,
    onToggled?: () => void,
}

export interface AlignGroupProps 
    extends Omit<ButtonProps, "type">,
            AlignGroupConfig{
    showShortcut?: boolean,
}

export const ImageAlignButtonGroup = React.forwardRef<
  HTMLDivElement,
  AlignGroupProps
>(({
  editor: providedEditor,
  hideWhenUnavailable = false,
  showShortcut = false,
  ...buttonProps
}, ref) => {

  const { editor } = useTiptapEditor(providedEditor)
  const isActive = editor?.isActive('image')

  if (!editor || !isActive) return null

  const isAlign = (align: 'left' | 'center' | 'right') =>
    editor.isActive('image', { align })

  const setAlign = (align: 'left' | 'center' | 'right') =>
    editor.chain().focus().updateAttributes('image', { align }).run()


  return (
    <div style={{ display: "flex", gap: 6 }} ref={ref}>

      {(["left","center","right"] as const).map(a => (
        <Button
          key={a}
          type="button"
          data-style={isAlign(a) ? "primary" : "ghost"}
          {...buttonProps}
          onClick={(e) => {
            buttonProps.onClick?.(e)
            setAlign(a)
          }}
        >
          {a}
        </Button>
      ))}

    </div>
  )
})