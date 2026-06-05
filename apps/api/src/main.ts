import { Hono } from 'hono';
import healthRoute from './routes/health.js';
import analyseRoute from './routes/analyse.js';

const app = new Hono()

app.route("/health", healthRoute);
app.route("/analyse", analyseRoute);

export default app