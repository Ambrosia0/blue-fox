import { Extension } from '@tiptap/core'

declare module '@tiptap/core' {
  interface Commands<ReturnType> {
    contentSaver: {
      saveCommand: () => ReturnType;
    }
  }
}

export const ContentSaver = Extension.create(() => {

    return{
        name: 'contentSaver',
        addOptions() {
            return {
                save: (_content: string) => {}
            }
        },
        addCommands() {
            return{
                saveCommand: () => ({editor}) =>{
                    const content = JSON.stringify(editor.getJSON());
                    this.options.save(content);
                    return true;
                },
            }
        },
        addKeyboardShortcuts() {
            return{
                'Mod-s': () =>{
                    return this.editor.commands.saveCommand();
                },
            }
        },
    }
})
