import type { User } from "oidc-client-ts";
import { createContext, useContext, useEffect, useState } from "react";
import { userManager } from "../App";

type AuthContextType = {
    user: User | null;
    isLoading: boolean;
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

    }, []);
    
    return(
        <AuthContext.Provider value={{
            user,
            isLoading,
        }}>
            {children}
        </AuthContext.Provider>
    )
}