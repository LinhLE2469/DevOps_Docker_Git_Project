# Ansible Playbook Documentation

## Overview

**Playbook:** `playbook.yml`

This file automates the deployment of the Docker application to a server. It will:
1. Install Docker
2. Create Docker networks
3. Start containers (database, backend API, proxy)

---

## Playbook Structure

```yaml
- hosts: all              # Run on all hosts defined in inventory
  gather_facts: true      # Collect system information
  become: true            # Run all tasks with sudo/root privileges

  roles:
    - docker             # Role 1: Install Docker
    - docker_network     # Role 2: Create Docker networks
    - database           # Role 3: Start database container
    - backend            # Role 4: Start API container
    - proxy              # Role 5: Start proxy container
```

---

## Role Details

### 1. **docker** - Install Docker
**File:** `roles/docker/tasks/main.yml`

**Tasks:**
- Install prerequisites (curl, gnupg, lsb-release, etc.)
- Add Docker GPG key
- Add Docker APT repository
- Install Docker CE
- Install Python3 + docker SDK (`python3-docker`)
- Add `admin` user to docker group
- Restart Docker service
- Verify Docker is working

**Result:** Docker daemon is ready, `admin` user has Docker permissions

---

### 2. **docker_network** - Create Docker Networks
**File:** `roles/docker_network/tasks/main.yml`

**Creates 2 networks:**

```yaml
- name: Create frontend network
  docker_network:
    name: frontend
    driver: bridge
    state: present

- name: Create backend network
  docker_network:
    name: backend
    driver: bridge
    internal: yes        # Internal only, no external access
    state: present
```

**Purpose:**
- `frontend`: Connects proxy + backend (accessible from outside)
- `backend`: Connects backend + database (internal only)

---

### 3. **database** - Start Database Container
**File:** `roles/database/tasks/main.yml`

```yaml
- name: Run database container
  docker_container:
    name: database
    image: linh2409/tp-devops-database:latest
    networks:
      - name: backend
    env:
      POSTGRES_DB: db
      POSTGRES_USER: usr
      POSTGRES_PASSWORD: pwd
    volumes:
      - pgdata:/var/lib/postgresql/data
    state: started
    restart_policy: unless-stopped
```

**Parameters:**
- `name: database` - Container name (used as hostname in network)
- `image` - Docker image from Docker Hub
- `networks` - Connect to `backend` network
- `env` - Environment variables (database config)
- `volumes` - Persistent storage for data
- `state: started` - Ensure container is running
- `restart_policy: unless-stopped` - Auto-restart on failure

---

### 4. **backend** - Start API Container
**File:** `roles/backend/tasks/main.yml`

```yaml
- name: Run backend API container
  docker_container:
    name: backend
    image: linh2409/tp-devops-simple-api:latest
    networks:
      - name: frontend    # Connect to proxy
      - name: backend     # Connect to database
    state: started
    restart_policy: unless-stopped
```

**Parameters:**
- Connects to **2 networks**:
  - `frontend`: For proxy to forward requests
  - `backend`: To reach database

**No port publishing needed** - proxy will handle traffic

---

### 5. **proxy** - Start Proxy Container
**File:** `roles/proxy/tasks/main.yml`

```yaml
- name: Run proxy container
  docker_container:
    name: proxy
    image: linh2409/tp-devops-httpd:latest
    networks:
      - name: frontend
    ports:
      - "80:80"           # Expose port 80 to public
    state: started
    restart_policy: unless-stopped
```

**Parameters:**
- `ports: ["80:80"]` - Container port 80 → Host port 80
- `networks: frontend` - Connect to frontend network (where backend runs)

**HTTP Flow:**
```
User → port 80 (proxy) → proxy forwards to backend:8080 → database
```

---

## docker_container Module - Detailed Parameters

### Basic Syntax:
```yaml
- name: Run my container
  docker_container:
    name: container_name              # Unique container name
    image: image_name:tag             # Docker image
    state: started|stopped|absent     # Desired state
    restart_policy: unless-stopped    # Restart policy
    networks:                         # List of networks
      - name: network_name
    ports:                           # Port mapping
      - "8080:8080"                  # host:container
    env:                             # Environment variables
      VAR_NAME: value
    volumes:                         # Volume mounts
      - volume_name:/path/in/container
    command: command_or_args         # Command to run
    memory: 512m                     # Memory limit
    cpu_shares: 1024                 # CPU weight
```

### Important Parameters:

| Parameter | Description | Example |
|-----------|-------------|---------|
| `name` | Unique container name | `database`, `backend` |
| `image` | Docker image (repo/name:tag) | `linh2409/tp-devops-database:latest` |
| `state` | Desired state | `started`, `stopped`, `absent` |
| `networks` | List of networks to connect | `- name: backend` |
| `ports` | Port mapping (host:container) | `- "80:80"` |
| `env` | Environment variables | `DB_HOST: database` |
| `volumes` | Volume mounts | `- pgdata:/var/lib/postgresql/data` |
| `restart_policy` | Auto-restart policy | `unless-stopped`, `always` |
| `command` | Override CMD | `python app.py` |
| `memory` | Memory limit | `512m` |
| `cpu_shares` | CPU weight | `1024` |

### State Values:
- **started** - Container must be running
- **stopped** - Container must be stopped
- **absent** - Container should not exist
- **paused** - Container should be paused

### Restart Policies:
- **no** - Do not restart
- **always** - Always restart
- **unless-stopped** - Restart unless explicitly stopped
- **on-failure** - Restart only on failure

---

## Execution Flow

### When running the playbook:
```bash
ansible-playbook -i inventories/setup.yml playbook.yml
```

**Execution order:**
1. **docker role** - Install Docker, add user to group, verify
2. **docker_network role** - Create `frontend` + `backend` networks
3. **database role** - Pull image and start database container
4. **backend role** - Pull image and start API container
5. **proxy role** - Pull image and start proxy container

**All containers will:**
- Auto-restart on failure
- Use created networks for communication
- Use environment variables from tasks

---

## Network Communication Architecture

```
┌─────────────────────────────────────────────────┐
│            Frontend Network                      │
│  ┌──────────┐              ┌─────────────────┐  │
│  │  Proxy   │◄─────────────►│  Backend API   │  │
│  │(port 80) │              │ (port 8080)    │  │
│  └──────────┘              └────────┬────────┘  │
│                                     │            │
└─────────────────────────────────────┼────────────┘
                                      │
            ┌─────────────────────────┘
            │
  ┌─────────▼────────────────────────┐
  │   Backend Network (Internal)      │
  │  ┌──────────────────────────────┐ │
  │  │   Database (PostgreSQL)      │ │
  │  │   (port 5432)                │ │
  │  └──────────────────────────────┘ │
  └──────────────────────────────────┘
```

**Communication Flow:**
1. User requests `http://thi.le.takima.school/`
2. Request hits proxy (port 80)
3. Proxy forwards to backend (internal, port 8080)
4. Backend queries database (port 5432)
5. Response returns to user

---

## Testing & Verification

**Verify deployment succeeded:**
```bash
# SSH to server
ssh -i ~/.ssh/takima_thi_le_key admin@thi.le.takima.school

# Check all containers are running
docker ps

# Test API is accessible
curl http://thi.le.takima.school/

# Check API health
curl http://thi.le.takima.school/actuator/health

# View logs
docker logs backend
docker logs database
docker logs proxy
```

**Test specific endpoints:**
```bash
# Get student by ID
curl http://thi.le.takima.school/students/1

# Get department
curl http://thi.le.takima.school/departments/IT
```

---

## Troubleshooting

**Container failed to start:**
```bash
docker logs container_name
```

**Network connectivity issue:**
```bash
# Test if backend can reach database
docker exec backend ping database

# Check network connectivity
docker network inspect backend
```

**Port already in use:**
```bash
netstat -tlnp | grep 80
```

**Permission denied errors:**
```bash
# Verify admin user is in docker group
id admin

# Re-add if needed
usermod -aG docker admin
newgrp docker
```

**Database not initialized:**
```bash
# Check database container logs
docker logs database

# Execute SQL directly
docker exec database psql -U usr -d db -c "\dt"
```

---

## Advanced: Custom Environment Variables

To add environment variables to containers, modify the task:

```yaml
- name: Run backend API container
  docker_container:
    name: backend
    image: linh2409/tp-devops-simple-api:latest
    networks:
      - name: frontend
      - name: backend
    env:
      DATABASE_URL: "jdbc:postgresql://database:5432/db"
      DATABASE_USERNAME: "usr"
      DATABASE_PASSWORD: "pwd"
      APP_PROFILE: "production"
    state: started
    restart_policy: unless-stopped
```

---

## References

- **Ansible Official Documentation:** https://docs.ansible.com/ansible/latest/
- **Ansible Roles:** https://docs.ansible.com/ansible/latest/user_guide/playbooks_reuse_roles.html
- **docker_container module:** https://docs.ansible.com/ansible/latest/collections/community/docker/docker_container_module.html
- **docker_network module:** https://docs.ansible.com/ansible/latest/collections/community/docker/docker_network_module.html
- **Docker Official Docs:** https://docs.docker.com/

---

## Summary

This playbook provides:
- ✅ Automated Docker installation
- ✅ Network isolation for security
- ✅ Persistent data storage
- ✅ Auto-restart on failures
- ✅ Clean separation of concerns (roles)
- ✅ Easy to maintain and scale

All containers communicate through Docker networks, ensuring proper isolation and security while maintaining necessary connectivity.
