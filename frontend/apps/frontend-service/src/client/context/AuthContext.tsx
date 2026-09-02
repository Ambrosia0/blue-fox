import { User } from "oidc-client-ts"
import { createContext, useContext, useEffect, useState } from "react";
import { userManager } from "../auth";

type AuthContextType = {
    user: User | null;
    isLoading: boolean;
    login: () => Promise<void>;
    logout: () => Promise<void>;
}

const AuthContext = createContext<AuthContextType | null>(null);

export const useAuth = () =>{
    const context = useContext(AuthContext);
    if(!context)
        throw new Error('useAuth must be used within AuthProvider');
    return context;
}

export const AuthProvider: React.FC<{children: React.ReactNode}> = ({children}) =>{
    const [user, setUser] = useState<User | null>(null);
    const [isLoading, setIsLoading] = useState<boolean>(true);

    useEffect(() => {
        const handleUserLoaded = (user: User) => {
            setUser(user);
        };

        const handleUserUnloaded = () => {
            setUser(null);
        };

        const handleExpired = async () => {
            try {
                await userManager.signinSilent();
            } catch {
                setUser(null);
            }
        };

        userManager.events.addUserLoaded(handleUserLoaded);
        userManager.events.addUserUnloaded(handleUserUnloaded);
        userManager.events.addAccessTokenExpired(handleExpired);

        (async () => {
            let user = await userManager.getUser();

            if (user?.expired) {
                try {
                    user = await userManager.signinSilent();
                } catch {
                    user = null;
                }
            }

            setUser(user);
            setIsLoading(false);
        })();

        return () => {
            userManager.events.removeUserLoaded(handleUserLoaded);
            userManager.events.removeUserUnloaded(handleUserUnloaded);
            userManager.events.removeAccessTokenExpired(handleExpired);
        };
    }, []);
    
    return(
        <AuthContext.Provider value={{
            user,
            isLoading,
            login: () => userManager.signinRedirect(),
            logout: () => userManager.signoutRedirect(),
        }}>
            {children}
        </AuthContext.Provider>
    )
}