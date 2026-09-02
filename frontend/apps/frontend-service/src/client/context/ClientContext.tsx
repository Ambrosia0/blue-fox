import { createContext, useContext, useEffect, useState } from "react";

const ClientContext = createContext<boolean>(false);

export const useClient = () => useContext(ClientContext);

export const ClientContextProvider: React.FC<{children}> = ({children}) =>{
    const [isClient, setIsClient] = useState(false);

    useEffect(() =>{
        setIsClient(true);
    },[])

    return(
        <ClientContext.Provider value={isClient}>
            {children}
        </ClientContext.Provider>
    )
}