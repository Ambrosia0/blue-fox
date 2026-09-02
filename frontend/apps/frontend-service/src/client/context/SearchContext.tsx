import { createContext, useContext, useState } from "react";

type SearchOption =
    | {
        type: 'COMMUNITY',
        id: number,
        slug: string,
        avatarId?: string
    }
    | {
        type: 'USER',
        id: string,
        username: string,
        avatarId?: string
    }

type SearchContextValue = {
    searchOption: SearchOption | null;
    setSearchOption?: (option: SearchOption | null) => void;
}

const SearchContext = createContext<SearchContextValue | null>(null);

export const useSearchOption = () =>{
    const context = useContext(SearchContext);
    if(!context)
        throw new Error('useSearchOption must be used within SearchOptionProvider');
    return context;
}

export const SearchOptionProvider: React.FC<{children: React.ReactNode}> = ({children}) =>{
    const [searchOption, setSearchOption] = useState<SearchOption>(null);
    return(
        <SearchContext.Provider value={{searchOption, setSearchOption}}>
            {children}
        </SearchContext.Provider>
    )
}