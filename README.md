# Planning Poker

A lightweight planning poker tool for agile sprint estimation.

## Architecture

- **Frontend**: Angular 18 → deployed on Netlify
- **Backend**: Spring Boot 3.3 + H2 → deployed on Render / Google Cloud Run

## Local Development

### Backend
```bash
cd backend
mvn spring-boot:run
# API at http://localhost:8080/api
```

### Frontend
```bash
cd frontend
npm install
ng serve
# App at http://localhost:4200
```

## Deployment

### Backend (Render)
1. Push `backend/` to a GitHub repo
2. Create a new Web Service on Render, point to the repo
3. Set environment: Docker, env var `ALLOWED_ORIGINS=https://your-netlify-app.netlify.app`

### Frontend (Netlify)
1. Push `frontend/` to GitHub
2. Connect to Netlify, it reads `netlify.toml` automatically
3. Update `src/app/environments/environment.prod.ts` with your Render URL

## API Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/sessions` | Create a new session |
| GET | `/api/sessions/{id}` | Get session state |
| POST | `/api/sessions/{id}/join` | Join (body: `{name}`) |
| POST | `/api/sessions/{id}/vote` | Vote (body: `{name, vote}`) |
| POST | `/api/sessions/{id}/reveal` | Reveal all votes |
| POST | `/api/sessions/{id}/reset` | Reset for new ticket (body: `{ticketId}`) |

Sessions auto-delete after 24 hours. Session IDs are UUIDs and never reused.
