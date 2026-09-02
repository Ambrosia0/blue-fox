import { useLoaderData, useParams } from "react-router";
import { useTranslation } from "react-i18next";
import {
    Box,
    Container,
    Typography,
    Tabs,
    Tab,
    Chip,
} from "@mui/material";
import PersonIcon from '@mui/icons-material/Person';
import GroupIcon from '@mui/icons-material/Group';
import PostIcon from '@mui/icons-material/Article';
import { useState, useMemo } from "react";
import { CommunityPreview } from "@services/user/userCommunityApi";
import { Post } from "@services/user/userPostApi";
import { UserInfo } from "../../types/user";
import { PostSearchContainer } from "./PostSearchContainer";
import { CommunitySearchContainer } from "./CommunitySearchContainer";
import { UserSearchContainer } from "./UserSearchContainer";

interface SearchLoaderData {
    query: string;
    tags: string[];
    resource: string;
}

export type SearchResource = 'users' | 'communities' | 'posts';

interface SearchResults {
    users: UserInfo[];
    communities: CommunityPreview[];
    posts: Post[];
}

type UserContextSearch = {
    type: 'user';
    id: string;
    username: string;
}

type CommunityContextSearch = {
    type: 'community';
    id: number;
    name: string;
}


export const SearchResult = () => {
    const { t } = useTranslation();
    const loaderData = useLoaderData() as SearchLoaderData;
    const params = useParams();

    const searchQuery = loaderData.query || '';
    const searchTags = loaderData.tags || [];
    

    let searchedResource: UserContextSearch | CommunityContextSearch;
    if(params['ui'] && params['un'])
        searchedResource = {
            type: "user",
            id: params['ui'],
            username: params['un']
        }
    else if(params['ci'] && params['cn'])
        searchedResource = {
            type: "community",
            id: Number.parseInt(params['ci']) ?? 0,
            name: params['cn']
        }
    
    const [activeTab, setActiveTab] = useState<SearchResource>('users');

    const tabsToDisplay: SearchResource[] = useMemo(() => ['users', 'communities', 'posts'], []);

    const handleTabChange = (_event: React.SyntheticEvent, newValue: SearchResource) => {
        setActiveTab(newValue);
    };

    return (
        <Container maxWidth="md" sx={{ mt: 4, mb: 4 }}>
            <Typography variant="h4" gutterBottom>
                {t('search.results')}
            </Typography>
            
            {searchQuery && (
                <Typography variant="body1" color="text.secondary" gutterBottom>
                    {t('search.request')}: "{searchQuery}"
                </Typography>
            )}
            
            {searchTags.length > 0 && (
                <Box mb={2} display="flex" flexWrap="wrap" gap={0.5}>
                    {searchTags.map((tag) => (
                        <Chip key={tag} label={tag} size="small" sx={{ bgcolor: 'primary.light', color: 'primary.contrastText' }} />
                    ))}
                </Box>
            )}

            {!searchedResource?
                <>
                    <Tabs
                        value={activeTab}
                        onChange={handleTabChange}
                        variant="fullWidth"
                        sx={{ mb: 2 }}
                    >
                        {tabsToDisplay.map((tab) => (
                            <Tab
                                key={tab}
                                label={t(`search.tabs.${tab}`)}
                                value={tab}
                                icon={tab === 'users' ? <PersonIcon /> : tab === 'communities' ? <GroupIcon /> : <PostIcon />}
                                iconPosition="start"
                            />
                        ))}
                    </Tabs>
                    {activeTab === 'posts' &&
                        <PostSearchContainer 
                            searchString={searchQuery}
                            tags={searchTags}
                        />
                    }
                    {activeTab === "users" &&
                        <UserSearchContainer
                            searchString={searchQuery}
                        />
                    }
                    {activeTab === "communities" &&
                        <CommunitySearchContainer
                            searchString={searchQuery}
                            tags={searchTags}
                        />
                    }
                </>:
                <PostSearchContainer 
                    searchString={searchQuery}
                    tags={searchTags}
                    authorId={searchedResource.type === "user"? searchedResource.id: undefined}
                    communityId={searchedResource.type === "community"? searchedResource.id: undefined}
                />
            }
        </Container>
    );
};