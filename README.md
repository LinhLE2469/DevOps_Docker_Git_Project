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

# CI part

Job: test-backend


1. Trigger Event

When you push code to main or develop branch
Or when a pull request is created
GitHub automatically starts this job
2. Step 1: Checkout Code

Downloads your entire GitHub repository code
Places it in the Ubuntu virtual machine's workspace
So the workflow can access all your files
3. Step 2: Set up JDK 21

Installs Java 21 (Temurin) on the Ubuntu machine
Required because your Java application needs the JDK to compile and run
4. Step 3: Build and Test with Maven

clean - Deletes old build files
verify - Compiles code + runs ALL tests (unit + integration)
Tests your backend code quality
Generates test reports in target
Possible Outcomes:

✅ If all tests PASS:

Job succeeds green ✓
Next job build-and-push-docker-image can run (because needs: test-backend)
Docker images get built and pushed
❌ If any test FAILS:

Job fails red ✗
The workflow STOPS
Docker build is SKIPPED
Your branch shows failed status on GitHub
This ensures only tested code gets deployed!

he build-and-push-docker-image job is the CD (Continuous Deployment) part. Here's what happens step-by-step:

Job Configuration:

needs: test-backend - Only runs if test-backend succeeds ✓
Runs on Ubuntu 24.04 virtual machine
Step 1: Checkout Code

Downloads your GitHub code again (needed for Docker build)
Step 2: Login to DockerHub

Authenticates with your Docker Hub account using GitHub Secrets
Required to push images to your Docker Hub registry
Step 3: Build and Push Backend Image

Builds Docker image from Dockerfile
Tags it as: yourusername/tp-devops-simple-api:latest
Only pushes to Docker Hub if branch is main, otherwise just builds
Step 4: Build and Push Database Image

Same process for database folder
Creates: yourusername/tp-devops-database:latest
Step 5: Build and Push HTTPD Image

Same process for ./Docker_Compose/proxy folder
Creates: yourusername/tp-devops-httpd:latest



