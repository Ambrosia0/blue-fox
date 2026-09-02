import { UserManager } from "oidc-client-ts";

export const userManager = typeof window !== 'undefined'?
    new UserManager({
        authority: import.meta.env.VITE_OIDC_AUTH_URL,
        client_id: "frontend-auth",
        redirect_uri: location.origin+'/auth/callback',
        automaticSilentRenew: true,
    }) : null;


if(userManager !== null){
    userManager.events.addSilentRenewError(async () => {
        await userManager.removeUser();
    });
    userManager.events.addAccessTokenExpired(async () => {
        try {
            await userManager.signinSilent();
        } catch (error) {
            await userManager.removeUser();
        }
    });
}