import { createContext, useContext, useEffect, useMemo, useState } from "react"
import { createTheme, type PaletteMode, ThemeProvider as MuiThemeProvider, responsiveFontSizes } from "@mui/material";


type ThemeContextType = {
    paletteMode: PaletteMode;
    setPaletteMode: (theme: PaletteMode) => void;
}

const ThemeContext= createContext<ThemeContextType | undefined>(undefined);

export const useTheme = () =>{
    const context = useContext(ThemeContext);
    if(!context)
        throw new Error('useTheme must be used within ThemeProvider');
    return context;
}

export const ThemeProvider: React.FC<{children: React.ReactNode}> = ({children}) =>{
    const [paletteMode, setPaletteMode] = useState<PaletteMode>(() =>{
        const type = typeof window !== 'undefined'? 
            localStorage.getItem("paletteMode"):
            "";
        return type === "light"? "light": "dark";
    });

    const theme = useMemo(
        () =>{
            let theme = createTheme({
                palette:{
                    mode: paletteMode,
                    background: {
                        paper: paletteMode === "light"? "#f7f8fa": "#1e1e1e",
                        default: paletteMode === "light"? "#f6f7f9": "#121212"
                    }
                },
                typography: {
                    fontFamily: ["Inter", 'sans-serif'].join(','),
                }
            });
            return responsiveFontSizes(theme);
        }, [paletteMode]
    )

    useEffect(() =>{
        localStorage.setItem("paletteMode", paletteMode);
    }, [paletteMode]);

    return(
        <ThemeContext.Provider value={{paletteMode, setPaletteMode}}>
            <MuiThemeProvider theme={theme}>
                {children}
            </MuiThemeProvider>
        </ThemeContext.Provider>
    )
}

