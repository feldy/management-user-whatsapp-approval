# Management User with WhatsApp Approval

A Spring Boot + React application for managing users with a WhatsApp-based approval workflow.

## 🌟 Features

- **User Registration:** Users register with email and WhatsApp number.
- **WhatsApp Verification:**
  - Dual notification system (User + Verifier).
  - Verifier receives approval requests via WhatsApp.
  - User receives status updates via WhatsApp.
- **Admin Dashboard:** Manage users via web interface.
- **Secure:** JWT Authentication & Role-based access.
- **Dockerized:** Easy deployment with Docker Compose.

---

## 🛠️ Prerequisites

- **Java 17+**
- **Node.js 18+**
- **Docker & Docker Compose**
- **WhatsApp Account** (for Admin/Verifier)

---

## 🚀 Installation & Setup

### 1. Clone Repository

```bash
git clone git@github.com:feldy/management-user-whatsapp-approval.git
cd management-user-whatsapp-approval
```

### 2. Configure WhatsApp

Edit `src/main/resources/application.properties`:

```properties
# Admin Number (Connected to WAHA - Scans QR)
whatsapp.admin.number=6285702588630

# Verifier Number (Receives requests & sends commands)
whatsapp.verifier.number=6287886955868
```

### 3. Run with Docker

Start Database, pgAdmin, and WAHA (WhatsApp API):

```bash
docker-compose up -d
```

### 4. Connect WhatsApp (WAHA)

1. **Get QR Code:**
   ```bash
   curl -H "X-Api-Key: tanganbaik-secret-key-2025" \
     "http://localhost:3001/api/default/auth/qr" | \
     jq -r '.image' | base64 -d > waha_qr.png
   ```
2. **Scan QR:** Open WhatsApp on the **Admin Number** -> Linked Devices -> Scan QR.

### 5. Start Backend

```bash
./mvnw spring-boot:run
```

### 6. Start Frontend

```bash
cd frontend
npm install
npm run build
npx serve dist -l 3000
```

Access the app at: `http://localhost:3000`

---

## ☁️ Deployment to Cloudflare Tunnel

Expose your local application to the internet securely.

### 1. Install Cloudflared

Download `cloudflared` for your OS: [Cloudflare Downloads](https://developers.cloudflare.com/cloudflare-one/connections/connect-apps/install-and-setup/installation/)

### 2. Start Tunnels

**Expose Frontend (UI):**
```bash
cloudflared tunnel --url http://localhost:3000
```
*Copy the generated URL (e.g., https://random-name.trycloudflare.com)*

**Expose Backend (API):**
```bash
cloudflared tunnel --url http://localhost:8185
```
*Copy the generated URL*

### 3. Update Frontend Config

If using Cloudflare, update `frontend/.env.production`:
```env
VITE_API_URL=https://your-backend-tunnel-url.trycloudflare.com
```
Then rebuild frontend: `npm run build`

---

## 📖 User Guide

### 1. Registration Flow
1. User opens the web app.
2. Clicks "Add User" (or registers via public form if enabled).
3. Fills details including **WhatsApp Number**.
4. **Result:**
   - User gets WhatsApp: "Registration received".
   - Verifier gets WhatsApp: "New user registered. Reply /approve {id}".

### 2. Approval Process
**Verifier** (from their WhatsApp) sends command to **Admin's WhatsApp**:

- **Approve:** `/approve {id}`
- **Reject:** `/reject {id}`

**Result:**
- Backend processes the command via webhook.
- User receives WhatsApp: "Account Approved/Rejected".
- Verifier receives confirmation.

### 3. Other Commands
Verifier can send these commands to Admin's WhatsApp:

- `/pending` - List all pending users.
- `/stats` - Show WhatsApp usage statistics.
- `/help` - Show available commands.

---

## 🔧 Troubleshooting

**Webhook not working?**
- Ensure WAHA is connected (Status: WORKING).
- Ensure Verifier is sending message TO Admin's WhatsApp.
- Check backend logs: `tail -f /tmp/spring-boot-console.log`

**Frontend not updating?**
- If running `serve dist`, you must run `npm run build` after every code change.

---

## 📄 License

MIT License
