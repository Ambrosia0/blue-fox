import { createTheme, ThemeProvider} from '@mui/material'
import { AdminPanel } from './panel'
import { ControlOptionProvider } from './context/ControlOptionContext'
import { User, UserManager } from 'oidc-client-ts'
import { AuthProvider } from './context/AuthContext';

export const mockUser = new User({
    id_token: 'mock-id-token',
    session_state: 'mock-session',
    access_token: 'mock-access-token',
    refresh_token: 'mock-refresh-token',
    token_type: 'Bearer',
    scope: 'openid profile',

    profile: {
        sub: '1',
        preferred_username: 'admin',
        name: 'Administrator',
        email: 'admin@example.com',

        iss: 'http://localhost:8080/realms/dev',
        aud: 'frontend',
        exp: new Date().getTime() + 3600,
        iat: new Date().getTime()
    },

    expires_at: new Date().getTime() + 3600
});
export class MockUserManager {

    async getUser(): Promise<User> {
        return mockUser;
    }

    async signinRedirect(): Promise<void> {}

    async signinSilent(): Promise<User> {
        return mockUser;
    }

    async signoutRedirect(): Promise<void> {}

    async storeUser(_: User): Promise<void> {}
}
export const userManager =
    import.meta.env.DEV
        ? new MockUserManager()
        : new UserManager({
              authority: import.meta.env.VITE_OIDC_AUTH_URL,
    client_id: "frontend-auth",
    redirect_uri: location.origin+'/auth/callback',
    automaticSilentRenew: true,
        });
// export const userManager = new UserManager({
//     authority: import.meta.env.VITE_OIDC_AUTH_URL,
//     client_id: "frontend-auth",
//     redirect_uri: location.origin+'/auth/callback',
//     automaticSilentRenew: true,
// });

// userManager.events.addSilentRenewError(async () => {
//     await userManager.removeUser();
//     userManager.signinRedirect();
// });

// if((await userManager.getUser()) == null)
//   userManager.signinRedirect();

function App() {
  const theme = createTheme({
    palette: {
      mode: 'dark',
    },
  })

  return (
    <ControlOptionProvider>
      <AuthProvider>
        <ThemeProvider theme={theme}>
          <AdminPanel />
        </ThemeProvider>
      </AuthProvider>
    </ControlOptionProvider>
  )
}

export default App
