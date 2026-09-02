import { Box, Button, FormControl, InputLabel, MenuItem, Select, Stack, ToggleButton, ToggleButtonGroup } from "@mui/material"
import { Direction, SearchType, SortOption } from "@services/user/userPostApi"
import { useTranslation } from "react-i18next"
import ArrowUpwardIcon from "@mui/icons-material/ArrowUpward";
import ArrowDownwardIcon from "@mui/icons-material/ArrowDownward";


type PostSelectorProps = {
    direction: Direction, // sort direction
    availableSearchTypes?: SearchType[], // switch'able search types
    searchType: SearchType,
    sortOption?: SortOption, // option for personalized feed
    changeDirection: (direction: Direction) => void,
    changeSearchType?: (searchType: SearchType) => void,
    changeSortOption?: (SortOption: SortOption) => void,
}

export const PostSelector: React.FC<PostSelectorProps> = ({
    direction,
    availableSearchTypes,
    searchType,
    sortOption,
    changeDirection,
    changeSearchType,
    changeSortOption
}) => {
    const { t } = useTranslation();

    const handleSearchTypeChange = (event: React.MouseEvent<HTMLElement>, value: SearchType) =>{
        changeSearchType(value);
    }

    const toggleDirection = () => {
        changeDirection(direction === "ASC" ? "DESC" : "ASC");
    };

    return(
        <Box sx={{
                display: "flex",
                alignItems: "center",
                mb: 2,
                flexWrap: "wrap",
                gap: 2,}}
        >
            <Stack
                direction="row"
                spacing={2}
                alignItems="center"
                flexWrap="wrap"
                useFlexGap
            >
                {availableSearchTypes && (
                    <ToggleButtonGroup
                        value={searchType}
                        aria-label={t("posts.search.controls.type")}
                        onChange={handleSearchTypeChange}
                        exclusive
                        size="small"
                    >
                        {availableSearchTypes.map((val) => (
                            <ToggleButton
                                key={val}
                                value={val}
                                sx={{
                                    textTransform: "none",
                                    px: 2,
                                }}
                            >
                                {t("posts.search.type." + val.toLowerCase())}
                            </ToggleButton>
                        ))}
                    </ToggleButtonGroup>
                )}

                {searchType === "PERSONALIZED" && (
                    <ToggleButtonGroup
                        value={sortOption}
                        exclusive
                        size="small"
                        onChange={(_, value) => {
                            if (value) {
                                changeSortOption(value);
                            }
                        }}
                    >
                        <ToggleButton
                            value="score"
                            sx={{
                                textTransform: "none",
                                px: 2,
                            }}
                        >
                            {t("posts.search.field.score")}
                        </ToggleButton>

                        <ToggleButton
                            value="date"
                            sx={{
                                textTransform: "none",
                                px: 2,
                            }}
                        >
                            {t("posts.search.field.date")}
                        </ToggleButton>
                    </ToggleButtonGroup>
                )}

                <ToggleButton
                    value={direction}
                    onClick={toggleDirection}
                    size="small"
                    sx={{
                        textTransform: "none",
                        px: 2,
                        display: "flex",
                        alignItems: "center",
                        gap: 0.5,
                    }}
                >
                    {direction === "ASC" ? (
                        <ArrowUpwardIcon fontSize="small" />
                    ) : (
                        <ArrowDownwardIcon fontSize="small" />
                    )}

                    {direction === "ASC"
                        ? t("posts.search.direction.asc")
                        : t("posts.search.direction.desc")}
                </ToggleButton>
            </Stack>
        </Box>
    )
}