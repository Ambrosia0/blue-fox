import { useEffect, useState } from "react";
import {
    Box,
    Divider,
    type SelectChangeEvent,
} from "@mui/material";
import { useLoaderData } from "react-router";
import { CommunityResponse, editCommunity, CommunityEdit, ScopePair, editCommunityScopes } from "@services/user/userCommunityApi";
import { PostPreviewContainer } from "../post/PostPreviewContainer";
import { useTranslation } from "react-i18next";
import { SearchType, SortOption, Direction } from "@services/user/userPostApi";
import { PostSelector } from "../post/PostSelector";
import { useSearchOption } from "../../context/SearchContext";
import { CommunityHeader } from "./components/CommunityHeader";
import { CommunitySideBar } from "./components/sidebar/CommunitySideBar";
import { getUserInfo } from "@services/user/userProfileApi";
import { UserInfo } from "../../types/user";
import { useAuth } from "../../context/AuthContext";
import { enqueueSnackbar } from "notistack";

export const CommunityDetail = () => {
    const { t } = useTranslation();

    const { user } = useAuth();
    const communityData = useLoaderData() as CommunityResponse;

    const [community, setCommunity] = useState<
        (Omit<CommunityResponse, 'communityModerators'> & {communityModerators: ({id: string} | UserInfo)[]})
        >(() => ({
            ...communityData,
            communityModerators: communityData.communityModerators.map(id => ({
                id
            }))
        }));


    const [searchType, setSearchType] = useState<SearchType>("POPULAR");
    const [sortOption, setSortOption] = useState<SortOption>("date");
    const [direction, setDirection] = useState<Direction>("DESC");
    const {setSearchOption} = useSearchOption();

    const isCreator = communityData.ownerId === user?.profile.sub;

    const postFilter = {
        searchType,
        communityId: communityData.id,
        sortOption,
        direction,
    };

    const handleSearchTypeChange = (event: SelectChangeEvent) => {
        setSearchType(event.target.value as SearchType);
    };

    // const handleSortOptionChange = (event: SelectChangeEvent) => {
    //     setSortOption(event.target.value as SortOption);
    // };

    const handleDirectionChange = () => {
        setDirection((prev) => (prev === "ASC" ? "DESC" : "ASC"));
    };

    const updateInfo = async (
        patch: CommunityEdit
    ) =>{
        try {
            const resp = await editCommunity(
                communityData.id,
                patch
            );
            setCommunity(prev => ({
                ...resp,
                communityModerators: prev.communityModerators
            }));
        } catch (error) {
            console.log(error);
        }
    }

    const uploadModeratorsInfo = async () => {
        try {
            const mods = communityData.communityModerators;
            if(mods && mods.length > 0){
                const moderators = await getUserInfo(mods);
                setCommunity(prev => ({
                    ...prev,
                    communityModerators: [...moderators.values()]
                }));
            }   
        } catch (error) {
            console.log(error);
        }
    }

    const editScopes = async (userScopes: ScopePair[], loadedInfo: ({id: string} | UserInfo)[]) =>{
        if(userScopes.find(scope => scope.scopes.length === 0)){
            enqueueSnackbar({
                variant: 'error',
                message: "Select at least one scope!"
            })
            return;
        }
        try {
            await editCommunityScopes(communityData.id, userScopes);
            setCommunity(prev => ({
                ...prev,
                communityModerators: [
                    prev.communityModerators.find(user => user.id === communityData.ownerId),
                    ...loadedInfo
                ]
            }))
        } catch (error) {
            console.log(error);
        }
    }
    useEffect(() => {
        setSearchOption({
            type: "COMMUNITY",
            ...communityData
        })
        uploadModeratorsInfo();
        return () => setSearchOption(null);
    },[])

    return (
        <Box display="flex" flexDirection="row">
            <Box flexGrow={3}>
                <CommunityHeader {...community}/>
                <Divider sx={{ my: 4 }} />

                <PostSelector
                    searchType={searchType}
                    direction={direction}
                    changeDirection={setDirection}
                    availableSearchTypes={[
                        "BEST",
                        "LATEST",
                        "POPULAR",
                    ]}
                    changeSearchType={setSearchType}
                />

                <Box mt={3}>
                    <PostPreviewContainer postFilter={postFilter} />
                </Box>
            </Box>
            <Box flexShrink={2} ml={5}>
                <CommunitySideBar 
                    ownerId={communityData.ownerId}
                    communityId={communityData.id}
                    canEdit={isCreator} 
                    save={updateInfo}
                    editScopes={editScopes}
                    {...community}
                />  
            </Box>
        </Box>
    );
};