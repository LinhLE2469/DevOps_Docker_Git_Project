# Simple API - Docker Compose

This repo contains a 3-tier demo application (backend, database, proxy) orchestrated with `docker-compose`.

**Prerequisites**
- Docker and Docker Compose installed.

**Start (build + detached)**
```bash
docker compose up --build -d
```

**Useful commands**
- Status: `docker compose ps`
- Logs (follow): `docker compose logs -f proxy`
- Execute inside a service: `docker compose exec backend sh`
- Stop and remove: `docker compose down`
- Clean volumes/images: `docker compose down --volumes --rmi local`

**Test endpoints (via reverse proxy)**
- Landing page (static): `curl -I http://localhost:8080/index.html`
- Greeting (proxied to backend): `curl -sS 'http://localhost:8080/?name=Alice' | jq .`
- Department students: `curl -sS http://localhost:8080/departments/ETI/students | jq .`

**Where ports are exposed**
- Only the proxy service is published to the host: `ports: - "8080:80"` in `docker-compose.yml`.
- The `backend` and `database` are internal-only (no `ports:`) for better security. To expose for local dev, add `ports:` to the service.

**Compose file summary**
- `database`: Postgres container, data persisted in `pgdata` volume. Environment variables set in compose.
- `backend`: Spring Boot app, connects to `database` using service name `database:5432`.
- `proxy`: Apache httpd reverse-proxy. Serves `index.html` and proxies `/` to `http://backend:8080/`.

**Why a reverse proxy?**
- Single public entrypoint, SSL termination, load balancing, caching, request routing, and improved security by keeping DB/backend internal.

**Publish images (short)**
```bash
# login
docker login
# tag
docker tag my-backend:1.0 YOUR_USER/my-backend:1.0
# push
docker push YOUR_USER/my-backend:1.0
```

If you want, I can add healthchecks, expose backend/database for dev, or add HTTPS to the proxy.
