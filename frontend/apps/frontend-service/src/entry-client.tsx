import { hydrateRoot } from 'react-dom/client'
import './client/index.css'
import { router } from './client/route'
import { RouterProvider } from 'react-router'
import { ClientContextProvider } from './client/context/ClientContext';

import './i18n';

hydrateRoot(
  document.getElementById('root')!,
  <ClientContextProvider>
    <RouterProvider router={router} />
  </ClientContextProvider>,
  {
    onRecoverableError(error, info) {
      console.error(error);
      console.info(info);
    }
  }
);