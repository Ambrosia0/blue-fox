import { Box, Chip, IconButton, Stack, Typography } from "@mui/material"
import { UserInfo } from "../../../../types/user"
import { Avatar } from "../../../user/Avatar";
import { useNavigate } from "react-router";
import { AboutTitle, ModeratorsTitle, RulesTitle, TagsTitle } from "./SectionTitle";
import { useTranslation } from "react-i18next";
import EditIcon from '@mui/icons-material/Edit';
import { isInfoLoaded } from "../../utils/utils";
import { UserView } from "./UserView";

type CommunitySideBarViewProps = {
    ownerId?: string;
    description?: string,
    rules?: string[],
    tags?: string[],
    communityModerators?: ({id: string} | UserInfo)[];
}

export const CommunitySideBarView: React.FC<CommunitySideBarViewProps> = ({
    description,
    rules,
    tags,
    communityModerators
}) => {
    const navigate = useNavigate();
    const { t } = useTranslation();

    const TagSection = () =>{
        return(
            <Box>
                <TagsTitle />
                {tags?.length > 0 && (
                    <Stack
                        direction="row"
                        spacing={1}
                        useFlexGap
                        flexWrap="wrap"
                    >
                        {tags.map(tag => (
                            <Chip
                                key={tag}
                                label={tag}
                                size="small"
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
            </Box>
        )
    }

    const AboutSection = () =>{
        return(
            <Box>
                <AboutTitle />
                {description ? (
                    <Typography
                        variant="body2"
                        color="text.secondary"
                        sx={{
                            whiteSpace: "pre-wrap",
                            lineHeight: 1.8,
                        }}
                    >
                        {description}
                    </Typography>
                ) : (
                    <Typography
                        variant="body2"
                        color="text.disabled"
                        sx={{
                            fontStyle: "italic",
                            lineHeight: 1.8,
                        }}
                    >
                        {t('community.descriptionPlaceholder')}
                    </Typography>
                )}
            </Box>
        )
    }

    const RulesSection = () => {
        return(
            <Box>
                <RulesTitle />
                {rules?.length ? (
                    <Stack spacing={1.5}>
                        {rules.map((rule, index) => (
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

                                <Typography
                                    variant="body2"
                                    sx={{
                                        pt: 0.5,
                                        lineHeight: 1.6,
                                    }}
                                >
                                    {rule}
                                </Typography>
                            </Box>
                        ))}
                    </Stack>
                ) : (
                    <Typography
                        variant="body2"
                        color="text.disabled"
                        sx={{
                            fontStyle: "italic",
                        }}
                    >
                        { t('community.rulesPlaceholder') }
                    </Typography>
                )}
            </Box>
        )
    }

    const ModeratorSection = () =>{
        return(
            <Box>
                <ModeratorsTitle />
                {communityModerators?.length > 0 && (
                    <Stack spacing={1}>
                        {communityModerators.map((moderator, index) => {
                            const isLoaded = isInfoLoaded(moderator);
                            const username = isLoaded?
                                moderator.username:
                                moderator.id;
                            return (
                                <Box
                                    key={index}
                                    onClick={() => 
                                        isLoaded?
                                            navigate(
                                                `/profile/${moderator.username}`
                                            ):
                                            null
                                    }
                                    sx={{
                                        display: "flex",
                                        alignItems: "center",
                                        gap: 1.5,
                                        p: 1,
                                        borderRadius: 2,
                                        transition: "0.2s",
                                        cursor:
                                            isLoaded? 
                                                "pointer": 
                                                "default",
                                        "&:hover":
                                            isLoaded? 
                                                {
                                                    bgcolor:
                                                        "action.hover",
                                                }: 
                                                {},
                                    }}
                                >
                                    <Avatar
                                        name={isLoaded? moderator.firstName: username}
                                        avatarId={
                                            isLoaded? 
                                                moderator.avatarId: 
                                                undefined
                                        }
                                        baseProps={{
                                            alt: username,
                                            sx: {
                                                width: 36,
                                                height: 36,
                                            },
                                        }}
                                    />
                                    <UserView
                                        user={moderator}
                                    />
                                </Box>
                            );
                        })}
                    </Stack>
                )}
            </Box>
        )
    }

    return (
        <Stack mb={1.5} gap={1.5}>
            <TagSection />
            <AboutSection />
            <RulesSection />
            <ModeratorSection />
        </Stack>
    );
};