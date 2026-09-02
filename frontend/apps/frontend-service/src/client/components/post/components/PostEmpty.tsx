import { Typography, Box } from "@mui/material";
import { SearchType } from "@services/user/userPostApi";
import DynamicFeedOutlinedIcon from "@mui/icons-material/DynamicFeedOutlined";
import { useTranslation } from 'react-i18next';

interface PostEmptyProps {
    type?: SearchType;
    authorId?: string;
}

type EmptyStateKey = {
    title: string;
    subtitle?: string;
};

const getEmptyStateKey = (
    type?: SearchType,
    authorId?: string
): EmptyStateKey => {
    if (authorId) {
        return {
            title: 'posts.empty.authorId.title',
            subtitle: 'posts.empty.authorId.message',
        };
    }

    switch (type) {
        case "POPULAR":
            return {
                title: 'posts.empty.popular.title',
                subtitle: 'posts.empty.popular.subtitle',
            };

        case "RELEVANCY":
            return {
                title: 'posts.empty.relevancy.title',
                subtitle: 'posts.empty.relevancy.subtitle',
            };

        case "LATEST":
            return {
                title: 'posts.empty.latest.title',
                subtitle: 'posts.empty.latest.subtitle',
            };

        case "BEST":
            return {
                title: 'posts.empty.best.title',
                subtitle: 'posts.empty.best.subtitle',
            };

        case "PERSONALIZED":
            return {
                title: 'posts.empty.personalized.title',
                subtitle: 'posts.empty.personalized.subtitle',
            };

        default:
            return {
                title: 'posts.empty.default.title',
                subtitle: 'posts.empty.default.subtitle',
            };
    }
};

export const PostEmpty = ({ type, authorId }: PostEmptyProps) => {
    const { t } = useTranslation();
    const stateKey = getEmptyStateKey(type, authorId);
    return (
        <Box
            sx={{
                py: 10,
                px: 3,
                display: "flex",
                flexDirection: "column",
                alignItems: "center",
                textAlign: "center",
            }}
        >
            <DynamicFeedOutlinedIcon
                sx={{
                    fontSize: 52,
                    color: "text.disabled",
                    mb: 2,
                }}
            />
            <Typography
                variant="h6"
                sx={{
                    fontWeight: 600,
                    mb: 1,
                }}
            >
                {t(stateKey.title)}
            </Typography>

            {stateKey.subtitle && (
                <Typography
                    variant="body2"
                    color="text.secondary"
                    sx={{ maxWidth: 400 }}
                >
                    {t(stateKey.subtitle)}
                </Typography>
            )}
        </Box>
    );
};