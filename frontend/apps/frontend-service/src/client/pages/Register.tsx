import { Box, Button, Container, FormControl, FormHelperText, IconButton, Input, InputAdornment, InputLabel, Paper } from "@mui/material"
import { useState } from "react";
import { Form, useActionData, useNavigate } from "react-router"
import AccountCircle from '@mui/icons-material/AccountCircle';
import Visibility from '@mui/icons-material/Visibility';
import VisibilityOff from '@mui/icons-material/VisibilityOff';
import LoginIcon from '@mui/icons-material/Login';
import AppRegistrationIcon from '@mui/icons-material/AppRegistration';


export const Register = () =>{
    const [showPassword, setShowPassword] = useState<boolean>(false);
    const data = useActionData();
    const navigate = useNavigate();

    const handleShowPassword = () => {
        setShowPassword(!showPassword);
    }

    const handleMouseDownPassword = (event: React.MouseEvent<HTMLButtonElement>) => {
        event.preventDefault();
    };

    const handleMouseUpPassword = (event: React.MouseEvent<HTMLButtonElement>) => {
        event.preventDefault();
    };

    return(
        <Paper sx={{ width: '100%', height: '100%', alignContent: 'center' }}>
            <Container>
                <Form action="/login" method="POST">
                    <Box display='flex' flexDirection='column' justifySelf={'center'} alignSelf={'center'}>
                        <Box display='flex' flexDirection='column'>
                            <FormControl variant="standard" sx={{ mb: 1 }}>
                                <InputLabel htmlFor="email-adornment">Email</InputLabel>
                                <Input
                                    id="email-adornment"
                                    name="email"
                                    fullWidth
                                    required
                                    startAdornment={
                                        <InputAdornment position="start">
                                            <AccountCircle />
                                        </InputAdornment>
                                    }
                                    error={data}>
                                </Input>
                                {data &&
                                    <FormHelperText>Error</FormHelperText>
                                }
                            </FormControl>
                            <FormControl variant="standard" sx={{ mb: 1 }}>
                                <InputLabel htmlFor="password-adornment">Password</InputLabel>
                                <Input
                                    id="password-adornment"
                                    type={showPassword ? "text" : "password"}
                                    name="password"
                                    fullWidth
                                    required
                                    endAdornment={
                                        <InputAdornment position="end">
                                            <IconButton
                                                aria-label={showPassword ? "hide the password" : "display the password"}
                                                onClick={handleShowPassword}
                                                onMouseDown={handleMouseDownPassword}
                                                onMouseUp={handleMouseUpPassword}
                                                edge="end"
                                            >
                                                {showPassword ? <VisibilityOff /> : <Visibility />}
                                            </IconButton>
                                        </InputAdornment>
                                    }
                                    error={data}>
                                </Input>
                                {data &&
                                    <FormHelperText>Error</FormHelperText>
                                }
                            </FormControl>
                        </Box>
                        <Box display='flex' flexDirection='row' justifyContent='space-between'>
                            <FormControl>
                                <Button type="submit" startIcon={<LoginIcon />}>Login</Button>
                            </FormControl>
                            <Button type="button" startIcon={<AppRegistrationIcon />} onClick={() => { navigate("/register") }}>Register</Button>
                        </Box>
                    </Box>
                </Form>
                {/* </Box> */}
            </Container>
        </Paper>
    )
}