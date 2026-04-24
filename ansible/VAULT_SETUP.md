# Ansible Vault Setup Guide

## 🔐 Ansible Vault là gì?

**Vault** = Dụng cụ để **mã hóa dữ liệu nhạy cảm** trong Ansible (mật khẩu, API keys, SSH keys, v.v.)

```
Plain text secrets          Encrypted (AES 256)
┌──────────────────┐       ┌──────────────────┐
│ password: xyz123 │  →    │ ♦♦♦♦♦♦♦♦♦♦♦♦♦♦ │
│ api_key: abc456  │       │ (encrypted file) │
└──────────────────┘       └──────────────────┘
   (unsafe)                     (safe ✅)
```

---

## 📋 Setup từng bước

### **Bước 1: Tạo Vault Password**

```bash
# Tạo một strong password (lưu ở chỗ an toàn!)
# Ví dụ: MyVaultPassword2026!

# Hoặc generate ngẫu nhiên:
openssl rand -base64 32
# Output: K3p9Zx2Jq8wL1mN5rVb7cFd3eG6hJ0sSt4uWx9yZ
```

### **Bước 2: Tạo Secrets File**

```bash
# Tạo file chứa dữ liệu nhạy cảm
cat > ansible/inventories/secrets.yml << 'EOF'
---
# Docker Registry Credentials
docker_registry_username: your_dockerhub_username
docker_registry_password: dckr_pat_xxxxxxxxxxxx

# Database Credentials
database_password: strong_database_password
database_user: db_admin

# SSH Keys (nếu cần)
deploy_ssh_key: |
  -----BEGIN RSA PRIVATE KEY-----
  MIIEpAIBAAKCAQEA...
  -----END RSA PRIVATE KEY-----
EOF
```

### **Bước 3: Encrypt File với Vault**

```bash
# Encrypt the secrets file
ansible-vault encrypt ansible/inventories/secrets.yml

# System sẽ hỏi password:
# New Vault password: [nhập password từ Bước 1]
# Confirm Vault password: [nhập lại]

# File bây giờ mã hóa - an toàn để commit lên GitHub
cat ansible/inventories/secrets.yml
# $ —— vault version 1.1 ——
# $default
# ♦♦♦♦♦♦♦♦♦♦♦♦♦♦♦♦♦♦♦♦♦♦♦♦
```

---

## 🔧 Sử dụng Vault trong Playbook

### **Playbook có dùng Vault**

```yaml
# ansible/playbook.yml
---
- hosts: all
  gather_facts: true
  become: true

  # Include encrypted secrets
  vars_files:
    - inventories/secrets.yml

  roles:
    - docker
    - docker_network
    - database      # Sẽ dùng {{ database_password }} từ secrets.yml
    - backend
    - frontend
    - proxy
```

### **Role sử dụng Vault variables**

```yaml
# ansible/roles/database/tasks/main.yml
---
- name: Run database container
  docker_container:
    name: database
    image: postgres:latest
    environment:
      POSTGRES_PASSWORD: "{{ database_password }}"  # ← Từ secrets.yml
      POSTGRES_USER: "{{ database_user }}"          # ← Từ secrets.yml
    networks:
      - name: backend
    state: started
```

---

## 🚀 Chạy Playbook với Vault

### **Cách 1: Nhập password trực tiếp (local)**

```bash
cd ansible

# Chạy playbook (sẽ hỏi password)
ansible-playbook -i inventories/setup.yml \
  --ask-vault-pass \
  playbook.yml

# System hỏi:
# Vault password: [nhập password]
```

### **Cách 2: Dùng password file (CI/CD)**

```bash
# Tạo file chứa password (KHÔNG commit lên GitHub)
echo "your_vault_password" > ~/.vault/password-file
chmod 600 ~/.vault/password-file

# Chạy playbook
ansible-playbook -i inventories/setup.yml \
  --vault-password-file ~/.vault/password-file \
  playbook.yml

# Hoặc set environment variable:
export ANSIBLE_VAULT_PASSWORD_FILE=~/.vault/password-file
ansible-playbook -i inventories/setup.yml playbook.yml
```

### **Cách 3: Chạy trong Docker (CI/CD)**

```bash
# Tạo password file (chỉ tồn tại lúc runtime)
mkdir ~/.vault
echo "your_vault_password" > ~/.vault/password-file

# Chạy Ansible trong Docker
docker run -it --rm \
  -v $(pwd):/ansible \
  -v ~/.vault:/root/.vault \
  -e ANSIBLE_VAULT_PASSWORD_FILE=/root/.vault/password-file \
  ansible/ansible:latest \
  ansible-playbook \
    -i /ansible/ansible/inventories/setup.yml \
    /ansible/ansible/playbook.yml

# Cleanup
rm -rf ~/.vault
```

---

## 🔄 GitHub Actions Integration

### **Setup GitHub Secrets**

1. Đi tới **Settings → Secrets and variables → Actions**
2. Thêm các secret:

| Secret | Value |
|--------|-------|
| `VAULT_PASSWORD` | your_vault_password |
| `DOCKERHUB_USERNAME` | your_dockerhub_user |
| `DOCKERHUB_TOKEN` | dckr_pat_... |
| `ANSIBLE_PRIVATE_KEY` | (contents of deploy_key) |
| `ANSIBLE_INVENTORY_HOST` | server_ip_or_hostname |

### **Workflow sử dụng Vault** (xem [deploy-advanced.yml](../.github/workflows/deploy-advanced.yml))

```yaml
- name: Decrypt Ansible Vault
  run: |
    mkdir -p ~/.vault
    echo "${{ secrets.VAULT_PASSWORD }}" > ~/.vault/password-file
    chmod 600 ~/.vault/password-file

- name: Deploy with Ansible in Docker
  run: |
    docker run -it --rm \
      -v $(pwd):/ansible \
      -v ~/.vault:/root/.vault \
      -e ANSIBLE_VAULT_PASSWORD_FILE=/root/.vault/password-file \
      ansible/ansible:latest \
      ansible-playbook \
        -i /ansible/ansible/inventories/setup.yml \
        /ansible/ansible/playbook.yml
```

---

## 📝 Edit Encrypted File

Nếu cần sửa file đã encrypt:

```bash
# Edit file (Ansible tự decrypt → edit → re-encrypt)
ansible-vault edit ansible/inventories/secrets.yml
# Nhập password, edit file, save

# Hoặc view without editing:
ansible-vault view ansible/inventories/secrets.yml
# Nhập password, view content
```

---

## ✅ Best Practices

✅ **DO:**
- Encrypt tất cả secrets (passwords, API keys, SSH keys)
- Lưu vault password ở nơi an toàn (password manager, 1Password, v.v.)
- Commit encrypted files lên GitHub (an toàn!)
- Rotate vault password định kỳ

❌ **DON'T:**
- Commit vault password lên GitHub!
- Lưu password trong plaintext
- Share vault password qua email/Slack
- Hardcode secrets trong playbooks

---

## 🐛 Troubleshooting

### **"Vault password is incorrect"**
```bash
# Kiểm tra password file
cat ~/.vault/password-file

# Hoặc nhập lại password manually
ansible-playbook --ask-vault-pass playbook.yml
```

### **"Permission denied for .vault/password-file"**
```bash
chmod 600 ~/.vault/password-file
```

### **Decrypt file để debug**
```bash
# Decrypt tạm thời
ansible-vault decrypt ansible/inventories/secrets.yml
cat ansible/inventories/secrets.yml

# Re-encrypt
ansible-vault encrypt ansible/inventories/secrets.yml
```

---

## 📚 References

- [Ansible Vault Documentation](https://docs.ansible.com/ansible/latest/user_guide/vault.html)
- [Best Practices](https://docs.ansible.com/ansible/latest/user_guide/vault.html#best-practices)

