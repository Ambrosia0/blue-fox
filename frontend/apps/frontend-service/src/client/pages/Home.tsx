import { Outlet } from "react-router"
import { Box, Container } from "@mui/material"
import { Menu } from "../components/Menu"
export const Home = () =>{
    const menuWith = 260;
    return(
        <Container 
            sx={{
                display: 'flex', 
                flexDirection: 'row',
            }}
            maxWidth="xl">
            <Box sx={{position: 'fixed'}}>
                <Menu />
            </Box>
            <Box sx={{width: menuWith, flexShrink: 0}}></Box>
            <Box sx={{width: "100%", p: 3}}>
                <Outlet />
            </Box>
            <Box>
            </Box>
        </Container>
    )
}