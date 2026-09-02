import { Box, Chip, IconButton, Stack, TextField, Typography } from "@mui/material"
import { useState } from "react";
import { UserInfo } from "../../../../types/user";
import { CommunityEdit } from "@services/user/userCommunityApi";
import { AboutTitle, RulesTitle, TagsTitle } from "./SectionTitle";
import { t } from "i18next";
import { useTranslation } from "react-i18next";
import AddIcon from '@mui/icons-material/Add';
import DeleteIcon from '@mui/icons-material/Delete';
import { TAG_REGEXP } from "../../../../utils/utils";

type CommunitySideBarProps = {
    description?: string,
    rules?: string[],
    tags?: string[],
    communityModerators?: ({id: string} | UserInfo)[];
    setPatch: React.Dispatch<React.SetStateAction<CommunityEdit>>;
} 

const TagSection = ({initTags, setPatch}: {initTags: string[], setPatch: (tags: string[]) => void}) =>{
    const [tags, setTags] = useState<string[]>(initTags);
    const [inputTag, setInputTag] = useState<string>("");

    const addTag = (tag: string) =>{
        const normalized = tag.startsWith('#')? tag: `#${tag}`;
        if(!normalized.match(TAG_REGEXP))
            return;
        setTags(prev => {
            const next = ((prev ?? []).includes(normalized) || prev.length >= 3)? prev: [...prev, normalized];
            setPatch(next);
            return next;
        });
    }

    const deleteTag = (index: number) =>{
        setTags(prev => {
            const next = prev.filter((_, idx) => idx !== index);
            setPatch(next);
            return next;
        })
    }
    
    return(
        <Box>
            <TagsTitle />
            {tags?.length > 0 && (
                <Stack
                    direction="row"
                    spacing={1}
                    useFlexGap
                    flexWrap="wrap"
                    sx={{
                        mb: 2
                    }}
                >
                    {tags.map((tag, index) => (
                        <Chip
                            key={tag}
                            label={tag}
                            size="small"
                            onDelete={() => deleteTag(index)}
                            sx={{
                                borderRadius: 2,
                                bgcolor: "action.hover",
                                border: "none",
                                fontWeight: 500,
                            }}
                        />
                    ))}
                </Stack>
            )}
            <TextField 
                label={t('community.tagsPlaceholder')}
                value={inputTag}
                size="small"
                onKeyDown={(e) =>{
                    if(e.key === 'Enter'){
                        addTag(inputTag);
                        setInputTag("");
                    }
                }}
                onChange={(e) => setInputTag(e.target.value)}
                fullWidth
            />
        </Box>
    )
}
const AboutSection = ({initAbout, setPatch}: {initAbout: string, setPatch: (descripion: string) => void}) =>{
    const [description, setDescription] = useState<string>(initAbout);
    const { t } = useTranslation();

    return(
        <Box>
            <AboutTitle />
            <TextField
                label={t('community.descriptionPlaceholder')}
                size="small"
                value={description}
                fullWidth
                onChange={(e) => {
                    setDescription(e.target.value);
                    setPatch(e.target.value);
                }}
                multiline
            />
        </Box>
    )
}
const RulesSection = ({initRules, setPatch}: {initRules: string[], setPatch: (rules: string[]) => void}) => {
    const [rules, setRules] = useState<string[]>(initRules);

    const addRule = () => {
        if(rules && rules.length >= 5)
            return;
        setRules(prev => {
            const next = [...(prev ?? []), ""];
            setPatch(next);
            return next;
        })
    }

    const deleteRule = (index: number) => {
        setRules(prev => {
            const next = prev.filter((val, idx) => idx !== index);
            setPatch(next);
            return next;
        })
    }

    const handleInput = (index: number, input: string) => {
        setRules(prev => {
            const next = [...prev];
            next[index] = input;
            setPatch(next);
            return next;
        })
    }

    return(
        <Box>
            <Box sx={{
                position: 'relative'
            }}>
                <IconButton 
                    size="small"
                    onClick={addRule}
                    sx={{
                        position: 'absolute',
                        right: 0
                    }}
                >
                    <AddIcon />
                </IconButton>
            </Box>
            <RulesTitle />

            <Stack spacing={1.5}>
                {(rules && rules.length > 0 && rules.map((rule, index) => (
                    <Box
                        key={index}
                        sx={{
                            display: "flex",
                            gap: 1.5,
                            alignItems: "flex-start",
                        }}
                    >
                        <Box
                            sx={{
                                flexShrink: 0,
                                width: 28,
                                height: 28,
                                borderRadius: "50%",
                                bgcolor: "action.hover",
                                display: "flex",
                                alignItems: "center",
                                justifyContent: "center",
                                fontSize: 13,
                                fontWeight: 700,
                            }}
                        >
                            {index + 1}
                        </Box>
                        <TextField 
                            size="small"
                            value={rules[index]}
                            onChange={(e) => handleInput(index, e.target.value)}
                            multiline
                        />
                        <IconButton
                            size="small"
                            onClick={() => deleteRule(index)}
                            sx={{
                                position: 'relative',
                                right: 0
                            }}
                        >
                            <DeleteIcon />
                        </IconButton>
                    </Box>
                ))) || 
                    <Typography
                        variant="body2"
                        color="text.disabled"
                        sx={{
                            fontStyle: "italic",
                            lineHeight: 1.8,
                        }}
                    >
                        {t('community.rulesPlaceholder')}
                    </Typography>
                }
            </Stack>
        </Box>
    )
}

export const CommunitySideBarEdit: React.FC<CommunitySideBarProps> = ({
    description,
    rules,
    tags,
    communityModerators,
    setPatch
}) =>{

    return(
        <Stack mb={1.5} gap={1.5}>
            <TagSection initTags={tags} setPatch={(tags) => setPatch(prev => ({...prev, tags: tags}))} />
            <AboutSection initAbout={description} setPatch={(description) => setPatch(prev => ({...prev, description: description}))}/>
            <RulesSection initRules={rules} setPatch={(rules) => setPatch(prev =>({...prev, rules: rules}))}/>
        </Stack>
    )
}