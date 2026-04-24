# DevOps Project - 3-Tier Application with Docker & Ansible

A complete DevOps infrastructure project featuring a 3-tier application (Frontend, Backend API, Database) with automated deployment pipelines using Docker Compose, Ansible, and GitHub Actions CI/CD.

## 📋 Table of Contents

- [Project Overview](#project-overview)
- [Architecture](#architecture)
- [Prerequisites](#prerequisites)
- [Quick Start (Local Development)](#quick-start-local-development)
- [Production Deployment (Ansible)](#production-deployment-ansible)
- [CI/CD Pipeline](#cicd-pipeline)
- [Project Structure](#project-structure)
- [Documentation](#documentation)

---

## 🎯 Project Overview

This project demonstrates enterprise-grade DevOps practices with:

- **Local Development**: Docker Compose for quick iteration
- **Infrastructure as Code**: Ansible for production deployment
- **Automated Pipelines**: GitHub Actions for CI/CD
- **Container Registry**: DockerHub for image distribution
- **Security**: Reverse proxy, internal-only services, SSH key management

### Components

| Component | Technology | Purpose |
|-----------|-----------|---------|
| **Frontend** | Vue.js + Apache httpd | Web UI & reverse proxy |
| **Backend** | Spring Boot (Java) | REST API |
| **Database** | PostgreSQL | Data persistency |
| **Orchestration (Dev)** | Docker Compose | Local environment |
| **Orchestration (Prod)** | Ansible + Docker | Production server |
| **CI/CD** | GitHub Actions | Automated testing & deployment |

---

## 🏗️ Architecture

### Local Development (Docker Compose)
```
┌─────────────────────────────────────────┐
│           Host Machine                  │
│  ┌─────────────────────────────────┐   │
│  │    Docker Network               │   │
│  │  ┌──────────────────────────┐  │   │
│  │  │   Proxy (Apache httpd)   │  │   │
│  │  │ :80 (exposed to :8080)   │  │   │
│  │  └────────────┬─────────────┘  │   │
│  │               │                │   │
│  │  ┌────────────▼─────────────┐  │   │
│  │  │  Backend (Spring Boot)   │  │   │
│  │  │  :8080 (internal only)   │  │   │
│  │  └────────────┬─────────────┘  │   │
│  │               │                │   │
│  │  ┌────────────▼─────────────┐  │   │
│  │  │  Database (PostgreSQL)   │  │   │
│  │  │  :5432 (internal only)   │  │   │
│  │  └──────────────────────────┘  │   │
│  └─────────────────────────────────┘   │
└─────────────────────────────────────────┘
```

### Production Deployment (Ansible)
```
┌─────────────────────────────────────┐
│   GitHub Actions (CI/CD)            │
│   • Build Docker images             │
│   • Push to DockerHub              │
└────────────────┬────────────────────┘
                 │
                 ▼
┌─────────────────────────────────────┐
│   SSH + Ansible Automation          │
│   • Install Docker                  │
│   • Pull images from registry       │
│   • Create networks                 │
│   • Start containers                │
└────────────────┬────────────────────┘
                 │
                 ▼
┌─────────────────────────────────────┐
│   Production Server                 │
│   • Proxy → Backend → Database      │
│   • Data persisted in volumes       │
└─────────────────────────────────────┘
```

---

## ✅ Prerequisites

### For Local Development
- **Docker** (v20.10+)
- **Docker Compose** (v2.0+)
- **Git**
- **curl** or **jq** (optional, for testing endpoints)

### For Production Deployment
- **Ansible** (v2.10+) on control machine
- **SSH access** to remote server
- **Python 3.7+** on remote server
- **Docker** + **Docker Compose** on remote server (Ansible will install)

### Recommended
- **VS Code** with Docker extension
- **Postman** or **Thunder Client** for API testing

---

## 🚀 Quick Start (Local Development)

### 1. Clone the Repository
```bash
cd /Users/linhle/LinhLe/Apprentisage/EFREI/S2_2026/DevOps/Docker/TP
```

### 2. Start Services with Docker Compose
```bash
# Build and start all services in detached mode
docker compose up --build -d
```

### 3. Verify Services
```bash
# Check service status
docker compose ps

# View logs (follow backend service)
docker compose logs -f backend
```

### 4. Test Endpoints

**Landing page (static):**
```bash
curl -I http://localhost:8080/index.html
```

**Greeting endpoint:**
```bash
curl -sS 'http://localhost:8080/?name=Alice' | jq .
```

**Department students:**
```bash
curl -sS http://localhost:8080/departments/ETI/students | jq .
```

### 5. Useful Commands

| Command | Purpose |
|---------|---------|
| `docker compose ps` | Show service status |
| `docker compose logs -f [service]` | View live logs |
| `docker compose exec backend sh` | Execute shell in service |
| `docker compose down` | Stop & remove containers |
| `docker compose down --volumes --rmi local` | Clean everything |
| `docker compose restart [service]` | Restart a service |

### 6. Development Workflow

**Making code changes:**
1. Edit source files (backend Java, frontend Vue, SQL schemas)
2. Rebuild and restart affected services:
   ```bash
   docker compose up --build -d [backend|database|front]
   ```
3. Test endpoints using curl or Postman

**Accessing service shells:**
```bash
# Backend service shell
docker compose exec backend sh

# Database shell
docker compose exec database psql -U usr -d db

# Frontend service shell
docker compose exec front sh
```

---

## 🔧 Production Deployment (Ansible)

### Prerequisites
1. **Remote server** with SSH access
2. **SSH key** for authentication
3. **Ansible** installed on your machine
4. **DockerHub credentials** (images already pushed by CI/CD)

### File Structure
```
ansible/
├── playbook.yml                 # Main playbook
├── inventories/setup.yml         # Inventory configuration
├── roles/
│   ├── docker/                  # Install Docker
│   ├── docker_network/          # Create networks
│   ├── database/                # Start database container
│   ├── backend/                 # Start backend container
│   └── proxy/                   # Start proxy container
└── README_PLAYBOOK.md           # Detailed documentation
```

### Quick Deploy

**1. Update inventory file** (`ansible/inventories/setup.yml`):
```yaml
all:
  hosts:
    prod_server:
      ansible_host: <your-server-ip>
      ansible_user: admin
      ansible_ssh_private_key_file: ~/.ssh/deploy_key
```

**2. Encrypt sensitive variables** (optional but recommended):
```bash
ansible-vault encrypt ansible/inventories/setup.yml
```

**3. Run the playbook**:
```bash
cd ansible
ansible-playbook -i inventories/setup.yml playbook.yml
```

**4. Verify deployment**:
```bash
# SSH to remote server
ssh admin@<server-ip>

# Check running containers
docker ps

# View logs
docker logs -f <container-name>
```

### What Ansible Does

1. ✅ Installs Docker and Docker Compose
2. ✅ Creates Docker networks
3. ✅ Pulls Docker images from DockerHub
4. ✅ Starts database container (PostgreSQL)
5. ✅ Starts backend container (Spring Boot)
6. ✅ Starts proxy container (Apache httpd)
7. ✅ Configures persistent volumes for data
8. ✅ Sets up health checks

### Ansible Roles Reference

- **[docker](ansible/roles/docker/tasks/main.yml)**: Installs Docker CE and Python SDK
- **[docker_network](ansible/roles/docker_network/tasks/main.yml)**: Creates isolated networks
- **[database](ansible/roles/database/tasks/main.yml)**: PostgreSQL container setup
- **[backend](ansible/roles/backend/tasks/main.yml)**: Spring Boot API deployment
- **[proxy](ansible/roles/proxy/tasks/main.yml)**: Apache reverse proxy configuration

For detailed information, see [ansible/README_PLAYBOOK.md](ansible/README_PLAYBOOK.md).

---

## 🔄 CI/CD Pipeline

### GitHub Actions Workflow: `.github/workflows/deploy.yml`

**Trigger**: Automatically runs when `Test & Quality Gate` workflow succeeds on `main` branch.

### Pipeline Stages

#### Stage 1: Build & Push Docker Images
```
✓ Checkout code
✓ Login to DockerHub
✓ Build 3 images:
  - Backend  → tp-devops-simple-api:latest
  - Database → tp-devops-database:latest
  - Proxy    → tp-devops-httpd:latest
✓ Push all images to DockerHub
```

#### Stage 2: Deploy to Production
```
✓ Checkout code
✓ Setup SSH key
✓ Install Ansible
✓ Run Ansible playbook on remote server
✓ Containers automatically updated with latest images
```

### Required Secrets

Configure these in GitHub repository settings (`Settings > Secrets and variables > Actions`):

| Secret | Purpose | Example |
|--------|---------|---------|
| `DOCKERHUB_USERNAME` | DockerHub login | `your_dockerhub_user` |
| `DOCKERHUB_TOKEN` | DockerHub auth | `dckr_pat_xxxx` |
| `ANSIBLE_PRIVATE_KEY` | SSH private key | (contents of pem file) |
| `ANSIBLE_INVENTORY_HOST` | Server IP/hostname | `192.168.1.100` |

### Full CI/CD Flow

```
1. Developer pushes code to main branch
        ↓
2. GitHub Actions runs tests (test.yml)
        ↓
3. If tests pass → Deploy workflow triggers
        ↓
4. Build & Push Docker images to DockerHub
        ↓
5. SSH to production server & run Ansible
        ↓
6. Ansible deploys latest containers
        ↓
7. Application updated in production! ✅
```

---

## 📁 Project Structure

```
.
├── README_FULL.md                    # This file
├── docker-compose.yml                # Local development orchestration
├── .github/
│   └── workflows/
│       ├── test.yml                  # CI testing pipeline
│       └── deploy.yml                # CD deployment pipeline
│
├── backend/
│   ├── Dockerfile                    # Spring Boot image
│   ├── pom.xml                       # Maven dependencies
│   ├── src/
│   │   ├── main/java/fr/takima/...   # Application code
│   │   └── test/java/fr/takima/...   # Unit & integration tests
│   └── target/                       # Build artifacts
│
├── database/
│   ├── Dockerfile                    # PostgreSQL image
│   └── sql/
│       ├── CreateScheme.sql          # Database schema
│       └── InsertData.sql            # Sample data
│
├── front/
│   ├── Dockerfile                    # Vue.js + httpd image
│   ├── package.json                  # Node dependencies
│   ├── babel.config.js, default.conf # Webpack & httpd config
│   ├── public/index.html             # Entry HTML
│   └── src/
│       ├── main.js                   # Vue entry point
│       ├── router.js                 # Routing config
│       ├── App.vue                   # Root component
│       └── components/               # Vue components
│
├── proxy/
│   ├── Dockerfile                    # Apache httpd image
│   ├── httpd.conf                    # Apache configuration
│   └── index.html                    # Static landing page
│
└── ansible/
    ├── playbook.yml                  # Main deployment playbook
    ├── README_PLAYBOOK.md            # Detailed Ansible docs
    ├── inventories/
    │   └── setup.yml                 # Host inventory
    └── roles/
        ├── docker/
        ├── docker_network/
        ├── database/
        ├── backend/
        └── proxy/
```

---

## 🔐 Security Best Practices

✅ **Implemented:**
- Backend & database are internal-only (no port exposure)
- Only proxy is exposed to host
- Reverse proxy handles SSL termination (ready for HTTPS)
- Ansible uses SSH key authentication
- Sensitive variables handled via GitHub Secrets
- Health checks monitor container status

⚠️ **Recommended for Production:**
- Enable HTTPS/TLS on proxy
- Use environment variables for DB credentials
- Implement Docker image signing
- Set resource limits on containers
- Regular security scanning of images
- Network policies (UFW/iptables on server)

---

## 🐛 Troubleshooting

### Local Development (Docker Compose)

**Services won't start:**
```bash
# Check for port conflicts
lsof -i :8080

# View detailed logs
docker compose logs --tail=50

# Rebuild without cache
docker compose down
docker compose up --build -d
```

**Database connection fails:**
```bash
# Verify database is healthy
docker compose ps database

# Check database logs
docker compose logs database

# Test connection manually
docker compose exec backend curl -v http://database:5432
```

### Production Deployment (Ansible)

**SSH connection fails:**
```bash
# Test SSH connectivity
ssh -i ~/.ssh/deploy_key admin@<server-ip>

# Verify key permissions
chmod 600 ~/.ssh/deploy_key
```

**Ansible fails to connect:**
```bash
# Run with verbose output
ansible-playbook -i <inventory> playbook.yml -vv

# Test Python availability on remote
ansible all -i <inventory> -m command -a "python3 --version"
```

**Containers don't start on server:**
```bash
# Check Docker is running
ssh admin@<server-ip> sudo systemctl status docker

# Manual container check
ssh admin@<server-ip> docker ps -a
ssh admin@<server-ip> docker logs <container-name>
```

---

## 📝 Contributing

1. Create a feature branch: `git checkout -b feature/something`
2. Make changes and test locally: `docker compose up --build -d`
3. Push to branch: `git push origin feature/something`
4. Tests run automatically on PR (GitHub Actions)
5. Once tests pass, merge to `main` triggers CD deployment

---

## 📖 Development Workflow Summary

| Task | Command |
|------|---------|
| **Local setup** | `docker compose up --build -d` |
| **View logs** | `docker compose logs -f [service]` |
| **Rebuild service** | `docker compose up --build -d [service]` |
| **Stop everything** | `docker compose down` |
| **Test API** | `curl -sS http://localhost:8080/[endpoint]` |
| **Deploy to prod** | Push to main → GitHub Actions → Ansible |
| **View CI/CD status** | GitHub Actions tab in repository |

---

## 🎓 Learning Outcomes

This project demonstrates:
- ✅ Docker containerization & orchestration
- ✅ Docker Compose for local development
- ✅ Ansible Infrastructure as Code
- ✅ GitHub Actions CI/CD workflows
- ✅ Reverse proxy & network architecture
- ✅ Production deployment automation
- ✅ Health checks & monitoring
- ✅ Volume management & data persistence
- ✅ Secret management in pipelines
- ✅ SSH key-based authentication

---

## 📞 Support

For issues or questions:
1. Check [Troubleshooting](#troubleshooting) section
2. Review component-specific README files
3. Check GitHub Actions logs for pipeline failures
4. Review Ansible playbook output for deployment issues

---

**Last Updated**: April 2026
**Version**: 1.0
