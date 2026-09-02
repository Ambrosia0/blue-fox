import { Box, Typography, Card, CardContent, Avatar } from "@mui/material";
import PersonIcon from "@mui/icons-material/Person";
import { UserInfo } from "../../types/user";
import { useNavigate } from "react-router";

const avatarPlaceholder = (
    <Avatar sx={{ bgcolor: "primary.main", width: 56, height: 56 }}>
        <PersonIcon fontSize="large" />
    </Avatar>
);

interface UserPreviewProps {
    user: UserInfo;
}

export const UserPreview: React.FC<UserPreviewProps> = ({ user }) => {
    const navigate = useNavigate();

    const handleClick = () => {
        navigate(`/profile/${user.username}`);
    };

    return (
        <Card
            sx={{
                mb: 1,
                borderRadius: 2,
                overflow: "hidden",
                cursor: "pointer",
                transition: "all 0.2s ease-in-out",
                "&:hover": {
                    bgcolor: "action.hover",
                    transform: "translateX(4px)",
                    boxShadow: "0 4px 12px rgba(0,0,0,0.1)",
                },
            }}
            onClick={handleClick}
        >
            <CardContent sx={{ display: "flex", alignItems: "center", p: 1.5, py: 1 }}>
                <Box sx={{ minWidth: 72, mr: 2 }}>
                    {user.avatarId ? (
                        <Avatar
                            src={`/api/public/media/${user.avatarId}`}
                            sx={{ width: 56, height: 56, borderRadius: 2 }}
                        />
                    ) : (
                        avatarPlaceholder
                    )}
                </Box>
                <Box sx={{ flex: 1 }}>
                    <Typography variant="h6" fontWeight={600} gutterBottom>
                        {user.username}
                    </Typography>
                </Box>
            </CardContent>
        </Card>
    );
};