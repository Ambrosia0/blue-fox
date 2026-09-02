import { Box } from "@mui/material";
import { Direction, SearchType, SortOption } from "@services/user/userPostApi";
import { PostSelector } from "../post/PostSelector";
import { useState } from "react";
import { PostPreviewContainer } from "../post/PostPreviewContainer";

export const PostSearchContainer = (
    {
        searchString, 
        tags,
        authorId,
        communityId
    }: 
    {
        searchString: string, 
        tags?: string[],
        authorId?: string,
        communityId?: number
    }
) =>{
    const [direction, setDirection] = useState<Direction>("DESC");
    const [searchType, setSearchType] = useState<SearchType>("RELEVANCY")
    return(
        <Box>
            <PostSelector 
                searchType={searchType}
                direction={direction}
                availableSearchTypes={["RELEVANCY", "BEST", "LATEST"]}
                changeDirection={setDirection}
                changeSearchType={setSearchType}
            />
            <PostPreviewContainer postFilter={{
                searchType: searchType,
                direction: direction,
                authorId: authorId,
                communityId: communityId,
                tags: tags,
                searchString: searchString
            }} />
        </Box>
    )
}