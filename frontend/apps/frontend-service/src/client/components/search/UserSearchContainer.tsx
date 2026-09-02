import { Box, Typography } from "@mui/material";
import { searchUsers } from "@services/user/userProfileApi";
import React, { useEffect, useState } from "react";
import { UserPreview } from "./UserPreview";
import { UserInfo } from "../../types/user";
import { useTranslation } from "react-i18next";

const MAX_USERS = 10;

interface UserSearchContainerProps {
    searchString: string;
}

export const UserSearchContainer: React.FC<UserSearchContainerProps> = ({
    searchString
}) => {
    const [users, setUsers] = useState<UserInfo[]>([]);
    const [loading, setLoading] = useState<boolean>(false);
    const { t } = useTranslation();

    useEffect(() => {
        const fetchUsers = async () => {
            if (!searchString || searchString.trim().length === 0) {
                setUsers([]);
                return;
            }

            try {
                setLoading(true);
                const data = await searchUsers(searchString);
                setUsers(data.slice(0, MAX_USERS));
            } catch (error) {
                console.error(error);
            } finally {
                setLoading(false);
            }
        };

        const timeoutId = setTimeout(() => {
            fetchUsers();
        }, 300);

        return () => clearTimeout(timeoutId);
    }, [searchString]);

    return (
        <Box sx={{ p: 2 }}>
            {loading && (
                <Typography variant="body2" color="text.secondary" align="center">
                    {t("search.loading")}
                </Typography>
            )}
            {!loading && users.length > 0 && (
                users.map((user) => (
                    <UserPreview key={user.id} user={user} />
                ))
            )}
            {!loading && users.length === 0 && searchString.trim().length > 0 && (
                <Typography variant="body1" color="text.secondary" align="center" sx={{ mt: 4 }}>
                    {t("search.noResults")}
                </Typography>
            )}
            {!loading && users.length === 0 && searchString.trim().length === 0 && (
                <Typography variant="body1" color="text.secondary" align="center" sx={{ mt: 4 }}>
                    {t("search.placeholder")}
                </Typography>
            )}
        </Box>
    );
};