import { Box, Chip, Stack, Typography } from "@mui/material";
import { useTranslation } from "react-i18next";
import TagIcon from "@mui/icons-material/Tag";
import InfoOutlinedIcon from "@mui/icons-material/InfoOutlined";
import GavelOutlinedIcon from "@mui/icons-material/GavelOutlined";
import ShieldOutlinedIcon from "@mui/icons-material/ShieldOutlined";

export const SectionTitle = ({ icon, children }) => (
    <Stack
        direction="row"
        spacing={1}
        alignItems="center"
        mb={1}
    >
        <Box
            sx={{
                display: "flex",
                color: "text.secondary",
            }}
        >
            {icon}
        </Box>
        <Typography
            variant="overline"
            fontWeight={700}
            color="text.secondary"
        >
            {children}
        </Typography>
    </Stack>
);


export const TagsTitle = () =>{
    const { t } = useTranslation();
    return(
        <SectionTitle icon={<TagIcon fontSize="small" />}>
            {t("community.tags")}
        </SectionTitle>
    )
}

export const AboutTitle = () =>{
    const { t } = useTranslation();
    return(
        <SectionTitle icon={<InfoOutlinedIcon fontSize="small" />}>
            { t("community.about") }
        </SectionTitle>
    )
}


export const RulesTitle = () => {
    const { t } = useTranslation();
    return(
        <SectionTitle icon={<GavelOutlinedIcon fontSize="small" />}>
            { t("community.rules") }
        </SectionTitle>
    )
}

export const ModeratorsTitle = () => {
    const { t } = useTranslation();
    return(
        <SectionTitle icon={<ShieldOutlinedIcon fontSize="small" />}>
            { t("community.moderators") }
        </SectionTitle>
    )
}