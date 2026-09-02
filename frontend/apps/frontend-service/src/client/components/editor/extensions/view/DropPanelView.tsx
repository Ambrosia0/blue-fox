import { Button, ButtonProps } from "@editor/components/tiptap-ui-primitive/button"
import { useTiptapEditor } from "@editor/hooks/use-tiptap-editor"
import type { Editor } from "@tiptap/react"
import React from "react"

export interface UseDropPanelConfig{
    editor?: Editor | null,
    hideWhenUnavailable?: boolean,
    onToggled?: () => void,
}

export interface DropPanelProps 
    extends Omit<ButtonProps, "type">,
            UseDropPanelConfig{
    showShortcut?: boolean,
    isToggled: boolean,
}

export const DropPanelButton = React.forwardRef<HTMLButtonElement, DropPanelProps>(
    ({
        editor: providedEditor,
        hideWhenUnavailable=false,
        showShortcut=false,
        isToggled,
        onToggled
        }, ref) =>{
        const {editor} = useTiptapEditor(providedEditor)
        
        return(
            <Button 
                type="button"
                data-style="ghost"
                role="button"
                tabIndex={-1}
                onClick={() => onToggled?.()}
                ref={ref}
            >
                <svg fillRule="evenodd" clipRule='evenodd' fill="currentColor" width="20px" height="20px" viewBox="0 0 48 48" >
                    <path d="M0 0h48v48H0z" fill="none" />
                    <g id="Shopicon" transform={isToggled? 'rotate(180 24 24)': undefined}>
                        <polygon points="24,29.172 9.414,14.586 6.586,17.414 24,34.828 41.414,17.414 38.586,14.586 	" />
                    </g>
                </svg>
            </Button>
        )
    }
)