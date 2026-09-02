import { Box } from "@mui/material"
import { OptionDrawer } from "./components/OptionsDrawer"
import { FunctionalWindow } from "./components/FunctionalWindow"

export const AdminPanel = () =>{
    return(
        <Box display={"flex"} flexDirection={"row"} width="100%" height="100%">
            <OptionDrawer />
            <FunctionalWindow />
        </Box>
    )
}