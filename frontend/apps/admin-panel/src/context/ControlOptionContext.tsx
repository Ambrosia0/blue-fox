import { createContext, useContext, useState, type ReactNode } from "react";

export type ControlOption = "posts" | "users" | "images" | "pages" | "paths" | "comments" | "reports" | null;

type ControlOptionContextType = {
    option: ControlOption;
    changeOption: (option: ControlOption) => void
}

const ControlOptionContext = createContext<ControlOptionContextType | undefined>(undefined);

export const useControlOption =  () =>{
    const context = useContext(ControlOptionContext);
    if(!context){
        throw new Error("useControlOption must be used within ComponentProvider");
    }
    return context;
}

export const ControlOptionProvider: React.FC<{children: ReactNode}> = ({children}) =>{
    const [option, setOption] = useState<ControlOption>(null);

    const changeOption = (option: ControlOption) => {
        setOption(option);
    };
    
    return(
        <ControlOptionContext.Provider value={{option, changeOption}}>
            {children}
        </ControlOptionContext.Provider>
    )
}