import { Box, Container, Toolbar, Typography } from '@mui/material';
import AppBar from '@mui/material/AppBar';
import { UserInfo } from './components/UserInfo';
import { ThemeSwitch } from '../ThemeButton';
import { useNavigate } from 'react-router';
import { SearchInput } from './components/SearchInput';

export const UpperBar = () => {
    const navigate = useNavigate();

    return (
        <AppBar position='fixed' color='default' id='upperBar' sx={{}}>
            <Container>
                <Toolbar>
                    <Box display="flex" flexDirection="row" justifyContent="space-between" width="100%" alignItems="center">
                        <Box>
                            <Typography variant='h5' onClick={() => { navigate("/") }} sx={{ cursor: 'pointer' }}>Blog app</Typography>
                        </Box>
                        {/* TABS */}
                        <Box display="flex" flexDirection="row" alignItems="center" gap="12px">
                            <Box>
                                <SearchInput />
                            </Box>
                            <Box display="flex"flexDirection="row" alignItems="center">
                                <ThemeSwitch />
                                <UserInfo />
                            </Box>
                        </Box>
                    </Box>
                </Toolbar>
            </Container>
        </AppBar>
    )
}
