import { Box, Paper, Toolbar, useTheme } from "@mui/material"
import { Outlet } from "react-router"
import { UpperBar } from "../components/upperBar/UpperBar"
import { ThemeProvider } from "../context/ThemeContext"
import { SnackbarProvider } from "notistack"
import { AuthProvider } from "../context/AuthContext"
import { NotificationProvider } from "../context/NotificationEvent"
import { SearchOptionProvider } from "../context/SearchContext"


export const Root = () =>{
    const theme = useTheme();
    return(
        <ThemeProvider>
            <AuthProvider>
                <NotificationProvider>
                    <SnackbarProvider 
                        autoHideDuration={3000} 
                        anchorOrigin={{vertical: 'top', horizontal:'right'}}>
                        <SearchOptionProvider>
                            <Box sx={{
                                display: 'flex',
                                flexDirection: 'column',
                                minHeight: '100vh'
                            }}>
                                <UpperBar />
                                <Toolbar />
                                <Paper elevation={0} sx={{
                                    flex: 1,
                                    borderRadius: 0,
                                    overflow: "auto"
                                }}>
                                    <Outlet />
                                </Paper>
                            </Box>
                        </SearchOptionProvider>
                    </SnackbarProvider>
                </NotificationProvider>
            </AuthProvider>
        </ThemeProvider>
    )
}