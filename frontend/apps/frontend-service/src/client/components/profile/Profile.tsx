import { Box, Container, IconButton, Tab, Tabs, Tooltip, Chip } from "@mui/material"
import { useLoaderData } from "react-router";
import { useEffect, useState } from "react";
import { ProfileCard } from "./components/ProfileCard";
import AddIcon from '@mui/icons-material/Add';
import { DraftContainer } from "./components/DraftContainer";
import { useClient } from "../../context/ClientContext";
import { PostPreviewContainer } from "../post/PostPreviewContainer";
import { Direction, PostFilter, SearchType} from "../../services/user/userPostApi";
import { useTranslation } from "react-i18next";
import { PostSelector } from "../post/PostSelector";
import { useAuth } from "../../context/AuthContext";
import CloseIcon from '@mui/icons-material/Close';
import { useSearchOption } from "../../context/SearchContext";
import { CurrentUserProfile, PublicUserProfile } from "@services/user/userProfileApi";

export const ProfileDataDisplay = () =>{
    const data = useLoaderData<CurrentUserProfile | PublicUserProfile>();
    const auth = useAuth();
    const isClient = useClient();
    const { t } = useTranslation();
    const [category, setCategory] = useState<'published' | 'drafts'>('published');
    const [isDraftCreationOpen, setIsDraftCreationOpen] = useState<boolean>(false);
    const [searchType, setSearchType] = useState<SearchType>('LATEST');
    const [direction, setDirection] = useState<Direction>('DESC');
    const [showAuthorFilter, setShowAuthorFilter] = useState<boolean>(true);
    const { setSearchOption } = useSearchOption();
    
    const isUserProfile = isClient && auth && auth.user?.profile.sub === data.id;

    const handleCategoryChange = (e: React.SyntheticEvent, val: 'published' | 'drafts') => {
        setCategory(val);
    };

    const postFilter: PostFilter = {
        searchType: searchType,
        direction: direction,
        authorId: showAuthorFilter ? data.id : undefined,
    };

    useEffect(() => {
        setSearchOption({
            type: "USER",
            ...data
        })
        return () =>
            setSearchOption(null)
    }, [])

    return (
        <Container maxWidth="lg">
            <Box sx={{
                position: 'fixed',
                mt: 5,
                width: 280
            }}>
                <ProfileCard {...data} isUserProfile = {isUserProfile}/>
            </Box>
            <Box sx={{
                display: 'flex',
                gap: 4,
                py: 4
            }}>
                <Box sx={{ width: 280, flexShrink: 0 }} />
                <Box sx={{ flex: 1 }}>
                    {isUserProfile &&
                        <Box sx={{
                            display: 'flex',
                            alignItems: 'center',
                            justifyContent: 'space-between',
                            mb: 2
                        }}>
                            <Tabs value={category} onChange={handleCategoryChange}>
                                <Tab value='published' label={t('profile.tabs.posts')} />
                                <Tab value='drafts' label={t('profile.tabs.drafts')}/>
                            </Tabs>
                            {category === 'drafts' && <Tooltip title={t('createNew')}>
                                <IconButton onClick={() => setIsDraftCreationOpen(true)}>
                                    <AddIcon />
                                </IconButton>
                            </Tooltip>}
                        </Box>
                    }
                    {(isUserProfile &&
                        <Box>
                            {category === 'published' && (
                                <PostSelector
                                    availableSearchTypes={["BEST", "LATEST"]}
                                    direction={direction}
                                    searchType={searchType}
                                    changeSearchType={setSearchType}
                                    changeDirection={setDirection}
                                />
                            )}
                            {category === 'published' && <PostPreviewContainer postFilter={postFilter} />}
                            {category === 'drafts' && <DraftContainer draftCreationOpen={isDraftCreationOpen} setDraftCreation={setIsDraftCreationOpen} />}
                        </Box>)
                        || 
                        <>
                            <Box sx={{ display: 'flex', alignItems: 'center', gap: 1, mb: 2 }}>
                                <Chip
                                    label={`${t('profile.tabs.posts')}: @${data.username}`}
                                    size="small"
                                    sx={{ bgcolor: 'primary.light', color: 'primary.contrastText' }}
                                    onDelete={() => setShowAuthorFilter(false)}
                                    deleteIcon={<CloseIcon />}
                                />
                            </Box>
                            <PostSelector
                                availableSearchTypes={["BEST", "LATEST"]}
                                direction={direction}
                                searchType={searchType}
                                changeSearchType={setSearchType}
                                changeDirection={setDirection}
                            />
                            <PostPreviewContainer postFilter={postFilter} />
                        </>
                    }
                </Box>
            </Box>
        </Container>
    );
}