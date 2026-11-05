# FinTrack — deployment handoff

## Important status

This package removes the fake JWT login and guest-session bypass, adds explicit API methods, and prepares a production Spring profile and Docker build. It is **not certified as a fully functional public multi-user finance app yet**. The backend entities/services currently do not consistently associate every financial record with its owning user; several frontend page actions still use the local FinanceContext instead of awaiting API persistence; goal contributions and the “cloud backup” are not real server-side features. Do not expose real financial data or describe those parts as production-secure until those gaps are addressed and integration tests pass.

## Projects
- `fintrack/`: Spring Boot backend, default local `dev` profile uses in-memory H2.
- `FinTrack-Frontend/fintrack-frontend/`: React + Vite frontend.

## Local frontend
```bash
cd FinTrack-Frontend/fintrack-frontend
npm install
cp .env.example .env
npm run build
npm run dev
```

## Local backend
Use JDK 21 and Maven 3.9+. Import `fintrack/` as a Maven project in Eclipse or run:
```bash
cd fintrack
mvn test
mvn spring-boot:run
```
The default `dev` profile uses H2 and port 9090. Swagger UI is available at `/swagger-ui/index.html`.

## Production backend environment
Set `SPRING_PROFILES_ACTIVE=prod`, `SPRING_DATASOURCE_URL`, `SPRING_DATASOURCE_USERNAME`, `SPRING_DATASOURCE_PASSWORD`, `JWT_SECRET`, `FRONTEND_ORIGIN`, and optionally `PORT`/`JWT_EXPIRATION`. If using `MYSQLHOST`, `MYSQLPORT`, `MYSQLDATABASE`, `MYSQLUSER`, and `MYSQLPASSWORD`, set all five. Never commit credentials. `ddl-auto=validate` requires the database schema to exist before startup; no automatic migration has been added. Back up existing database data before changing schemas.

Docker build:
```bash
docker build -t fintrack-api ./fintrack
docker run --env-file fintrack/.env -p 9090:9090 fintrack-api
```

## Frontend deployment
Build as a static site with root `FinTrack-Frontend/fintrack-frontend`, build command `npm ci && npm run build` (if a lockfile is present; otherwise `npm install && npm run build`), and publish directory `dist`. Set `VITE_API_BASE_URL` to the backend URL ending in `/api`. The public host must serve `public/_redirects` or configure its equivalent SPA fallback.

## Verification requirements before public demo
1. Register and log in using a real backend; failed API calls must remain failures (no fake sessions).
2. Confirm all CRUD operations write to and reload from MySQL after backend restart.
3. Add user ownership and authorization checks to transactions, accounts, categories, budgets, goals, notifications and audit data.
4. Add integration tests for user isolation, transaction CRUD, budget/goal logic, notifications and reports.
5. Implement or clearly disable unsupported features (goal contributions, admin dashboard API, cloud backup, profile avatar persistence).
6. Test deployed CORS and JWT expiration with a fresh browser session.
