import { getUserFollows, UserFollowResponse } from "@services/user/userFollowApi";
import { enqueueSnackbar } from "notistack";
import { useEffect, useState } from "react"

export const UserFollowMenu = () =>{
    const [userFollows, setUserFollows] = useState<UserFollowResponse[]>([]);
    const [hasNext, setHasNext] = useState<boolean>(false);
    const [page, setPage] = useState(0);

    const fetchUserFollows = async () =>{
        try {
            const data = await getUserFollows(page);
            setUserFollows(data.content);
            setHasNext(data.hasNext);
        } catch (error) {
            console.log(error);
        }
    }

    useEffect(() => {
        fetchUserFollows();
    }, []);
    return(
        <div>

        </div>
    )
}