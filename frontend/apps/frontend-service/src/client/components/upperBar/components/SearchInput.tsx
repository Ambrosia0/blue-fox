import { Chip, InputBase, List, ListItemAvatar, ListItemButton, ListItemText, Paper, Popper, Tooltip, useAutocomplete } from "@mui/material"
import SearchIcon from '@mui/icons-material/Search';
import { useNavigate } from "react-router";
import { useState, useRef, useEffect } from "react";
import { useTranslation } from "react-i18next";
import { useSearchOption } from "../../../context/SearchContext";
import { AVATAR_ENDPOINT } from "@services/apiClient";
import { searchUsers } from "@services/user/userProfileApi";
import { UserInfo } from "../../../types/user";
import { Avatar } from "../../user/Avatar";

const HASHTAG_REGEX = /#[\p{L}\p{N}_]+/gu;
const USERNAME_REGEX = /@([\p{L}\p{N}_]+)$/u;

type MentionState = {
    start: number;
    query: string;
} | null;

export const SearchInput = () => {
    const navigate = useNavigate();
    const { t } = useTranslation();
    const [tags, setTags] = useState<string[]>([]);
    const [inputValue, setInputValue] = useState<string>('');
    const [error, setError] = useState<boolean>(false);
    const { searchOption, setSearchOption } = useSearchOption();

    const [searchUser, setSearchUser] = useState<UserInfo[]>([]);
    const [mention, setMention] = useState<MentionState>(null);
    
    const inputRef = useRef<HTMLInputElement>(null);

    const handleSearch = () => {
        const cleanTags = tags.map(t => t.substring(1));

        const params = new URLSearchParams();

        if(inputValue.length < 3){
            setError(true);
            return;
        }

        if(searchOption?.type === "COMMUNITY"){
            params.set('ci', searchOption.id.toString());
            params.set('cn', searchOption.slug);
        }

        if(searchOption?.type === "USER"){
            params.set('ui', searchOption.id);
            params.set('un', searchOption.username);
        }
        
        if (inputValue.trim()) {
            const cleanQuery = inputValue.replace(HASHTAG_REGEX, '').trim();
            if (cleanQuery) {
                params.set('q', cleanQuery);
            }
        }
        
        if (cleanTags.length > 0) {
            params.set('tags', cleanTags.join(','));
        }
        
        const queryString = params.toString();
        const searchUrl = queryString ? `/search?${queryString}` : '/search';
        
        navigate(searchUrl);
    };

    const onKeyDown = (e: React.KeyboardEvent<HTMLInputElement>) => {
        setError(false);
        if (e.key === 'Enter') {
            handleSearch();
        } else if (e.key === ' ') {
            const cursor = e.currentTarget.selectionStart;
            const beforeCursor = e.currentTarget.value.slice(0, cursor);
            const lastWord = beforeCursor.split(/\s+/).at(-1);
            if(lastWord?.startsWith('#')){ 
                e.preventDefault();

                const trimmed = lastWord.trim();
                const newTag = trimmed.toLowerCase();
                if (!tags.includes(newTag) && tags.length < 4) {
                    setTags(prev => [...prev, newTag]);
                }
                setInputValue(inputValue.replace(lastWord, ''));
            }
        }
    };

    const detectUserMention = (value: string, cursor: number) =>{
        const beforeCursor = value.slice(0, cursor);
        const match = beforeCursor.match(USERNAME_REGEX);
        if(!match)
            return null;

        return {
            start: cursor - match[0].length,
            query: match[1]
        };
    }

    const handleTagDelete = (tagToRemove: string) => {
        setTags(prev => prev.filter(t => t !== tagToRemove));
    };

    const handleSearchOptionDelete = () =>{
        setSearchOption(null);
    }

    const onInputChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        const value = e.target.value;
        const cursor = e.target.selectionStart ?? value.length;

        setInputValue(value);

        const mention = detectUserMention(value, cursor);

        if(mention){
            setSearchUser([]);
            setMention(mention);
        }else{
            setMention(null);
        }
    };

    useEffect(() =>{
        if(!mention || mention.query.length <= 2)
            return;
        const timeout = setTimeout(() => {
            search();
        }, 300);
        return () => clearTimeout(timeout);
    }, [mention]);

    async function search(){
        try {
            setSearchUser(await searchUsers(mention.query));
        } catch (error) {
            console.log(error);
        }
    }

    function selectUser(user: UserInfo){
        if(!mention)
            return;
        const before = inputValue.slice(0, mention.start);
        const after = inputValue.slice(
            mention.start + mention.query.length + 1
        );
        setSearchOption({
            type: "USER",
            id: user.id,
            username: user.username,
            avatarId: user.avatarId
        })
        setInputValue(`${before}${after}`);
        setSearchUser([]);
        setMention(null);
    }

    return (
        <Paper
            sx={{
                borderRadius: "12px", 
                p: "5px 8px", 
                bgcolor: "background.default",
                border: '1px solid',
                borderColor: 'text.secondary',
                transition: 'border-color 0.3s ease, background-color 0.3s ease',
                display: 'flex',
                alignItems: 'center',
                flexWrap: 'wrap',
                gap: 0.5,
                minHeight: '36px',
                '&:hover':{
                    borderColor: 'primary.main'
                },
                '&:focus-within': {
                    borderColor: 'primary.main',
                    backgroundColor: 'action.hover',
                },
            }}
        >
            {searchOption !== null && ((searchOption.type === "COMMUNITY" &&
                <Chip 
                    avatar={
                        <Avatar 
                            avatarId={searchOption.avatarId}
                        />
                    }
                    label={searchOption.slug}
                    onDelete={handleSearchOptionDelete}
                />) || (searchOption.type === "USER" &&
                    <Chip
                        avatar={
                            <Avatar 
                                avatarId={searchOption.avatarId}
                                name={searchOption.username}
                            />
                        }
                        label={searchOption.username}
                        onDelete={handleSearchOptionDelete}
                    />
                ))
            }
            {tags.map((tag) => (
                <Chip
                    key={tag}
                    label={tag}
                    size="small"
                    onDelete={() => handleTagDelete(tag)}
                    sx={{
                        bgcolor: 'primary.light',
                        color: 'primary.contrastText',
                        '& .MuiChip-deleteIcon': {
                            color: 'primary.contrastText',
                            '&:hover': {
                                color: 'rgba(255,255,255,0.7)',
                            },
                        },
                    }}
                />
            ))}
            <Tooltip 
                open={error} 
                title={t('search.size-constraint')} 
                placement="bottom"
                sx={{
                    zIndex: 4444
                }}
            >
                 <InputBase
                    inputRef={inputRef}
                    sx={{ flex: 1 }}
                    placeholder={t('search.placeholder')} 
                    value={inputValue}
                    onChange={onInputChange}
                    onKeyDown={onKeyDown}
                    startAdornment={
                        <SearchIcon 
                            sx={{
                                cursor: "pointer",
                                mr: 1,
                                transition: 'color 0.3s ease',
                                '&:hover':{
                                    color: 'primary.main'
                                }
                            }} 
                            onClick={handleSearch} 
                        />
                    }
                 />
            </Tooltip>
            {searchUser.length !== 0
                &&<Popper
                    open={Boolean(mention)}
                    anchorEl={inputRef.current}
                    sx={{
                        zIndex: 3333,
                    }}
                    modifiers={[
                        {
                            name: 'offset',
                            options: {
                                offset: [0, 8]
                            }
                        }
                    ]}
                    placement="bottom-start"
                >
                    <Paper sx={{
                        width: inputRef.current?.clientWidth,
                        maxHeight: 300,
                        overflowY: "auto"
                    }}>
                        <List>
                            {searchUser.map(user => (
                                <ListItemButton
                                    key={user.id}
                                    onClick={() => selectUser(user)}
                                >
                                    <ListItemAvatar>
                                        <Avatar 
                                            avatarId={user.avatarId}
                                            name={user.username}
                                            baseProps={{
                                                alt: user.username
                                            }}
                                        />
                                    </ListItemAvatar>
                                    <ListItemText primary={user.username} />
                                </ListItemButton>
                            ))}
                        </List>
                    </Paper>
                </Popper>
            }
        </Paper>
    );
}