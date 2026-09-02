import { Box } from "@mui/material";
import { useControlOption } from "../../context/ControlOptionContext";
import { ImageControl } from "./controlComponents/ImageControl";
import { PostControl } from "./controlComponents/PostControl";
import { UserControl } from "./controlComponents/UserControl";
import { CommentControl } from "./controlComponents/CommentControl";
import { ReportControl } from "./controlComponents/ReportControl";

export const FunctionalWindow = () =>{
    const {option} = useControlOption();

    return(
        <Box width='100%' height='100%' sx={{backgroundColor: 'background.paper'}}>
            {option === "images" && <ImageControl />}
            {option === "posts" && <PostControl />}
            {option === "users" && <UserControl />}
            {option === "comments" && <CommentControl />}
            {option === "reports" && <ReportControl />}
        </Box>
    )
}
