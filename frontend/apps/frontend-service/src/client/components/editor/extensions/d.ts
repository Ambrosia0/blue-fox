import '@tiptap/extension-image'

declare module '@tiptap/extension-image' {
  interface SetImageOptions {
    align?: 'left' | 'center' | 'right'
  }
}