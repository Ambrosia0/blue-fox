import { defineConfig } from "vite"
import react from '@vitejs/plugin-react';
import path, { resolve } from "path";

export default defineConfig(({isSsrBuild}) => ({
    plugins: [
        react({
            jsxRuntime: "automatic"
        }),
    ],
    resolve:{
        alias:{
            '@services': path.resolve(__dirname, 'src/client/services'),
            '@editor': path.resolve(__dirname, 'src/client/components/editor/@'),
            axios: isSsrBuild? "/src/empty.ts": "axios"
        },
        extensions: ['.mjs', '.js', '.ts', '.jsx', '.tsx', '.json'],
        dedupe: ["react", "react-dom", "react-router"]
    },
    optimizeDeps: {
        include: ['@mui/icons-material']
    },
    ssr:{
        noExternal: true,
        // [
        //     // 'hono', 
        //     // '@hono/node-server', 
        //     // 'react', 
        //     // 'react-dom', 
        //     // 'react-router', 
        //     // '@mui/material', 
        //     // '@mui/icons-material',
        //     // 'react-oidc-context'
        // ],
        external: [
            // "@tiptap/core",
            // "@tiptap/extension-document",
            // "@tiptap/extension-highlight",
            // "@tiptap/extension-horizontal-rule",
            // "@tiptap/extension-image",
            // "@tiptap/extension-list",
            // "@tiptap/extension-paragraph",
            // "@tiptap/extension-subscript",
            // "@tiptap/extension-superscript",
            // "@tiptap/extension-text",
            // "@tiptap/extension-text-align",
            // "@tiptap/extension-typography",
            // "@tiptap/extensions",
            // "@tiptap/html",
            // "@tiptap/pm",
            // "@tiptap/react",
            // "@tiptap/starter-kit",
        ]
        // noExternal: true
    },
    build: {
        ssr: isSsrBuild,
        outDir: isSsrBuild? 'dist/server': 'dist/client',
        copyPublicDir: !isSsrBuild,
        emptyOutDir: true,
        rollupOptions:{
            input: isSsrBuild?
                { server: resolve(__dirname, 'src/server.ts')}:
                { main: resolve(__dirname, 'index.html')},
            output:{
                entryFileNames: isSsrBuild? '[name].js': "assets/main.js",
                assetFileNames: "assets/[name].[ext]",
            },
            external:['fsevents']
        }
    }
}))