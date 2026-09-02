import { useMatches } from "react-router";

type ActionMenuProps = {
    commentId: number;
}

export const ActionMenu: React.FC<ActionMenuProps> = ({...props}) =>{
    const pathMatch = useMatches(); // pathMatch["postId"]?
    return(
        <></>
    )    
}