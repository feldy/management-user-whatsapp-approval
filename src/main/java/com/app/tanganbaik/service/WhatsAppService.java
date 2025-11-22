package com.app.tanganbaik.service;

import com.app.tanganbaik.config.WhatsAppConfig;
import com.app.tanganbaik.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Service
public class WhatsAppService {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private WhatsAppConfig config;

    @Autowired
    private WhatsAppRateLimiter rateLimiter;

    private final Random random = new Random();
    private long lastMessageTime = 0;

    /**
     * Send registration confirmation to user and verification request to verifier
     */
    public void sendDualRegistrationNotification(User user) {
        if (!config.isEnabled()) {
            System.out.println("⚠️ WhatsApp disabled in configuration");
            return;
        }

        // Send confirmation to user
        if (user.getPhoneNumber() != null && !user.getPhoneNumber().isEmpty()) {
            String userMessage = generateUserRegistrationConfirmation(user);
            sendMessageSafely(user.getPhoneNumber(), userMessage);
        }

        // Send verification request to verifier
        String verifierMessage = generateVerificationRequest(user);
        sendMessageSafely(config.getVerifierNumber(), verifierMessage);
    }

    /**
     * Send approval confirmation to verifier and user
     */
    public void sendApprovalConfirmation(User user) {
        if (!config.isEnabled())
            return;

        // Confirm to verifier
        String verifierMessage = generateApprovalConfirmation(user);
        sendMessageSafely(config.getVerifierNumber(), verifierMessage);

        // Notify user
        if (user.getPhoneNumber() != null && !user.getPhoneNumber().isEmpty()) {
            String userMessage = generateUserApprovalNotification(user);
            sendMessageSafely(user.getPhoneNumber(), userMessage);
        }
    }

    /**
     * Send rejection confirmation to verifier and user
     */
    public void sendRejectionConfirmation(User user) {
        if (!config.isEnabled())
            return;

        // Confirm to verifier
        String verifierMessage = generateRejectionConfirmation(user);
        sendMessageSafely(config.getVerifierNumber(), verifierMessage);

        // Notify user
        if (user.getPhoneNumber() != null && !user.getPhoneNumber().isEmpty()) {
            String userMessage = generateUserRejectionNotification(user);
            sendMessageSafely(user.getPhoneNumber(), userMessage);
        }
    }

    /**
     * Generate user registration confirmation message
     */
    private String generateUserRegistrationConfirmation(User user) {
        return "✅ *Pendaftaran Berhasil!*\n\n" +
                "Halo " + user.getName() + ",\n\n" +
                "Pendaftaran Anda telah diterima dan sedang menunggu persetujuan admin.\n\n" +
                "Anda akan menerima notifikasi setelah akun Anda disetujui.\n\n" +
                "Email: " + user.getEmail();
    }

    /**
     * Generate verification request for verifier
     */
    private String generateVerificationRequest(User user) {
        return String.format(
                "🔔 *Pendaftaran User Baru*\n\n" +
                        "📝 Nama: %s\n" +
                        "📧 Email: %s\n" +
                        "📱 WhatsApp: %s\n" +
                        "👤 Role: %s\n\n" +
                        "Untuk approve: /approve %d\n" +
                        "Untuk reject: /reject %d",
                user.getName(),
                user.getEmail(),
                user.getPhoneNumber() != null ? user.getPhoneNumber() : "Tidak ada",
                user.getRole().replace("ROLE_", ""),
                user.getId(),
                user.getId());
    }

    /**
     * Generate user approval notification
     */
    private String generateUserApprovalNotification(User user) {
        return "🎉 *Akun Anda Telah Disetujui!*\n\n" +
                "Halo " + user.getName() + ",\n\n" +
                "Akun Anda telah disetujui oleh admin.\n" +
                "Anda sekarang dapat login menggunakan email dan password Anda.\n\n" +
                "Silakan login untuk mengakses sistem.";
    }

    /**
     * Generate user rejection notification
     */
    private String generateUserRejectionNotification(User user) {
        return "❌ *Pendaftaran Ditolak*\n\n" +
                "Halo " + user.getName() + ",\n\n" +
                "Mohon maaf, pendaftaran Anda telah ditolak oleh admin.\n\n" +
                "Jika Anda merasa ini adalah kesalahan, silakan hubungi admin.";
    }

    /**
     * Generate varied approval messages to look more human-like
     */
    private String generateApprovalMessage(User user) {
        String[] templates = {
                "🔔 *New User Registration*\n\n" +
                        "📝 Name: %s\n" +
                        "📧 Email: %s\n" +
                        "👤 Role: %s\n\n" +
                        "To approve: /approve %d\n" +
                        "To reject: /reject %d",

                "👤 *Pendaftaran Baru*\n\n" +
                        "Nama: %s\n" +
                        "Email: %s\n" +
                        "Role: %s\n\n" +
                        "Approve: /approve %d\n" +
                        "Reject: /reject %d",

                "✨ *User baru mendaftar*\n\n" +
                        "• %s\n" +
                        "• %s\n" +
                        "• %s\n\n" +
                        "Ketik /approve %d untuk menerima\n" +
                        "Ketik /reject %d untuk menolak"
        };

        String template = templates[random.nextInt(templates.length)];
        return String.format(
                template,
                user.getName(),
                user.getEmail(),
                user.getRole().replace("ROLE_", ""),
                user.getId(),
                user.getId());
    }

    private String generateApprovalConfirmation(User user) {
        String[] templates = {
                "✅ User %s telah disetujui!",
                "✅ Approved! User %s sekarang bisa login.",
                "👍 Berhasil approve user %s (%s)"
        };

        String template = templates[random.nextInt(templates.length)];
        if (template.contains("%s") && template.lastIndexOf("%s") != template.indexOf("%s")) {
            return String.format(template, user.getName(), user.getEmail());
        }
        return String.format(template, user.getName());
    }

    private String generateRejectionConfirmation(User user) {
        String[] templates = {
                "❌ User %s telah ditolak.",
                "❌ Rejected! User %s tidak dapat login.",
                "🚫 User %s (%s) ditolak."
        };

        String template = templates[random.nextInt(templates.length)];
        if (template.contains("%s") && template.lastIndexOf("%s") != template.indexOf("%s")) {
            return String.format(template, user.getName(), user.getEmail());
        }
        return String.format(template, user.getName());
    }

    /**
     * Send message with anti-spam protection
     */
    public void sendMessageSafely(String to, String text) {
        try {
            // Check rate limit
            if (!rateLimiter.canSendMessage(to)) {
                System.err.println("🚫 Message blocked by rate limiter!");
                System.err.println("📊 Message would have been: " + text);
                return;
            }

            // Human-like delay between messages
            applyHumanLikeDelay();

            // Send message
            String url = String.format("%s/sendText", config.getApiUrl());

            Map<String, Object> request = new HashMap<>();
            request.put("session", config.getSession());
            request.put("chatId", to + "@c.us");
            request.put("text", text);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("X-Api-Key", "tanganbaik-secret-key-2025");

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(request, headers);

            restTemplate.postForEntity(url, entity, String.class);

            // Record message
            rateLimiter.recordMessage(to);
            lastMessageTime = System.currentTimeMillis();

            System.out.println("✅ WhatsApp message sent to: " + to);

            // Log stats
            Map<String, Object> stats = rateLimiter.getStats(to);
            System.out.println("📊 Usage: " + stats.get("hourly") + "/" +
                    stats.get("hourlyLimit") + " (hourly), " +
                    stats.get("daily") + "/" +
                    stats.get("dailyLimit") + " (daily)");

        } catch (Exception e) {
            System.err.println("❌ Failed to send WhatsApp message: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Apply random delay to simulate human typing
     */
    private void applyHumanLikeDelay() {
        try {
            long now = System.currentTimeMillis();
            long timeSinceLastMessage = now - lastMessageTime;

            // If less than min delay, wait
            if (timeSinceLastMessage < config.getMinDelay()) {
                Thread.sleep(config.getMinDelay() - timeSinceLastMessage);
            }

            // Add random delay (2-5 seconds)
            int randomDelay = config.getMinDelay() +
                    random.nextInt(config.getMaxDelay() - config.getMinDelay());
            Thread.sleep(randomDelay);

            System.out.println("⏱️ Applied delay: " + randomDelay + "ms");

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    /**
     * Get current usage stats
     */
    public Map<String, Object> getUsageStats() {
        return rateLimiter.getStats(config.getAdminNumber());
    }

    /**
     * Send pending users list
     */
    public void sendPendingUsersList(String to, java.util.List<User> users) {
        if (!config.isEnabled())
            return;

        if (users.isEmpty()) {
            sendMessageSafely(to, "✅ No pending users.");
            return;
        }

        StringBuilder message = new StringBuilder("📋 *Pending Users*\n\n");
        for (User user : users) {
            message.append(String.format(
                    "ID: %d\n" +
                            "Name: %s\n" +
                            "Email: %s\n" +
                            "/approve %d | /reject %d\n\n",
                    user.getId(),
                    user.getName(),
                    user.getEmail(),
                    user.getId(),
                    user.getId()));
        }

        sendMessageSafely(to, message.toString());
    }
}
