import { Hono } from 'hono'
import { serve } from '@hono/node-server'
import { serveStatic } from '@hono/node-server/serve-static'
import fs from 'fs';
import { handler } from "./serverRoutes.js";
import { compress } from 'hono/compress'
import { cache } from 'hono/cache'
import { ContentfulStatusCode } from 'hono/utils/http-status';


const app = new Hono();
const template = fs.readFileSync('./dist/client/index.html', 'utf-8');

app.use('*', compress({encoding: 'gzip'}));

app.use('/*', (req, next) => {
    if(req.req.url.includes('.')){
        return serveStatic({
            root: './dist/client'
        })(req, next);
    }
    return next();
});

app.get('*', cache({
    cacheName: 'frontend-app',
    cacheControl: 'max-age=3600'
}));

app.get('*', async (req) =>{
    try {
        const data = await handler(req.req.raw);
        if(data instanceof Response){
            return req.html(data.text(), data.status as ContentfulStatusCode);
        }
        const html = template.replace(`<!--ssr-outlet-->`, () => data.html);
        return req.html(html, data.status as ContentfulStatusCode);   
    } catch (error) {
        console.error('SSR Error:', error);
        return req.text('Internal Server Error', { status: 500 });
    }
})

serve({
    fetch: app.fetch,
    port: 3000
}, (info) => console.log(`Server running on: http://localhost:${info.port}`))

process.on('uncaughtException', (err) => {
  console.error('Uncaught Exception:', err);
});

process.on('unhandledRejection', (reason) => {
  console.error('Unhandled Rejection:', reason);
});

export {app};