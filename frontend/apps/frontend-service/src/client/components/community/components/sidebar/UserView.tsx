import { Box, Typography } from "@mui/material";
import { UserInfo } from "../../../../types/user";
import { isInfoLoaded } from "../../utils/utils";

type UserViewProps = {
    user: {id: string} | UserInfo;
}

export const UserView = ({
    user
}) =>{
    const isLoaded = isInfoLoaded(user);
    return(
        <Box>
            <Typography
                variant="body1"
                color="text.primary"
                fontWeight={600}
                noWrap
            >
                {isLoaded? 
                    `${user.firstName} ${user.lastName}`:
                    user.id
                }
            </Typography>
            <Typography
                variant="body2"
                color="text.secondary"
                fontWeight={600}
                noWrap
            >
                @{isLoaded?
                    user.username:
                    user.id
                }
            </Typography>
        </Box>
    )
}