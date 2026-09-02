import { useNavigate } from "react-router"
import { Loading } from "./pages/Loading"
import { useEffect } from "react";
import { userManager } from "./auth";

export const AuthCallback = () =>{
    const navigate = useNavigate();
    useEffect(() => {
        async function callback() {
            if(!userManager){
                navigate('/login', {replace: true})
                return;
            }
            
            const params = new URLSearchParams(window.location.search);

            if(!params.has("code") || !params.has("state")){
                navigate('/login', {replace: true});
                return;
            }

            try {
                const user = await userManager.signinRedirectCallback();
                const returnUrl = (user.state as { returnUrl?: string })?.returnUrl;
                navigate(
                    returnUrl ?? "/",
                    {replace: true}
                )
            } catch (error) {
                console.error(error);
            }
        }

        callback();
    },[])
    return(
        <Loading />
    )
}