import { Box, CircularProgress, Paper, Stack, Typography } from "@mui/material";
import { ThemeProvider } from "../context/ThemeContext";

export const Loading = () => {
    return (
        <ThemeProvider>
            <Paper
                elevation={0}
                sx={{
                    width: "100%",
                    minHeight: "100%",
                    display: "flex",
                    alignItems: "center",
                    justifyContent: "center",
                    bgcolor: "background.default",
                }}
            >
                <Stack
                    spacing={3}
                    alignItems="center"
                >
                    <CircularProgress size={36} />

                    <Typography
                        variant="body2"
                        color="text.secondary"
                    >
                        Loading...
                    </Typography>
                </Stack>
            </Paper>
        </ThemeProvider>
    );
};

export const CircularLoading = () => {
    return (
        <Box sx={{
            display: 'flex',
            justifyContent: 'center',
            alignItems: 'center',
            minHeight: '200px',
            width: '100%'
        }}>
            <CircularProgress />
        </Box>
    );
};