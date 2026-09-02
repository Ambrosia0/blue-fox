import { Box } from "@mui/material";
import { Direction, SortOption } from "@services/user/userPostApi"
import { useState } from "react"
import { PostSelector } from "./PostSelector";
import { PostPreviewContainer } from "./PostPreviewContainer";

export const PersonalFeedContainer = () =>{
    const [sortOption, setSortOption] = useState<SortOption>("score");
    const [direction, setDirection] = useState<Direction>("DESC");

    return(
        <Box>
            <PostSelector 
                searchType={"PERSONALIZED"}
                direction={direction}
                sortOption={sortOption}
                changeSortOption={setSortOption}
                changeDirection={setDirection}
            />
            <PostPreviewContainer postFilter={{
                searchType: "PERSONALIZED",
                sortOption: sortOption,
                direction: direction
            }} />
        </Box>
    )
}