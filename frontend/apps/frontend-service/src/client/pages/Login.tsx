import { userManager } from "../auth";

export const Login = () => {
    if(window === undefined)
        return <p>Redirecting...</p>;
    return <LoginClient />
};

const LoginClient = () =>{
    userManager.signinRedirect();
    return <p>Redirecting to IDP...</p>
}