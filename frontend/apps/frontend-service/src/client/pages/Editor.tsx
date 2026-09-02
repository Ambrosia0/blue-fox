import { Outlet } from "react-router";
import { ThemeProvider } from "../context/ThemeContext";
import { AuthProvider, useAuth } from "../context/AuthContext";
import { PostEditor } from "../components/editor/PostEditor";
import { Loading } from "./Loading";
import { useClient } from "../context/ClientContext";

export const Editor = () =>{
    return (
        <ThemeProvider>
            <AuthProvider>
                <PostEditor />
            </AuthProvider>
        </ThemeProvider>
    )
}
