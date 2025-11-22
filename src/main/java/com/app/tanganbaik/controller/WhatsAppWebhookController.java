package com.app.tanganbaik.controller;

import com.app.tanganbaik.config.WhatsAppConfig;
import com.app.tanganbaik.entity.User;
import com.app.tanganbaik.service.UserService;
import com.app.tanganbaik.service.WhatsAppService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/webhook")
public class WhatsAppWebhookController {

    @Autowired
    private UserService userService;

    @Autowired
    private WhatsAppService whatsAppService;

    @Autowired
    private WhatsAppConfig config;

    // Simple LRU Cache for message deduplication (max 1000 items)
    private final java.util.Map<String, Long> processedMessageIds = java.util.Collections.synchronizedMap(
            new java.util.LinkedHashMap<String, Long>(1000, 0.75f, true) {
                @Override
                protected boolean removeEldestEntry(java.util.Map.Entry<String, Long> eldest) {
                    return size() > 1000;
                }
            });

    @PostMapping("/whatsapp")
    public ResponseEntity<String> handleWhatsAppWebhook(@RequestBody Map<String, Object> payload) {
        try {
            System.out.println("========================================");
            System.out.println("📥 WEBHOOK RECEIVED!");
            System.out.println("📦 Full Payload: " + payload);
            System.out.println("========================================");

            // Extract message from WAHA webhook format
            if (!payload.containsKey("event")) {
                System.out.println("⚠️ No 'event' key in payload");
                return ResponseEntity.ok("OK");
            }

            String event = (String) payload.get("event");

            // Handle different event types
            if ("message".equals(event)) {
                Map<String, Object> message = (Map<String, Object>) payload.get("payload");
                if (message == null)
                    return ResponseEntity.ok("OK");

                String text = (String) message.get("body");
                String from = (String) message.get("from");
                String messageId = (String) message.get("id");

                if (text == null || from == null || messageId == null)
                    return ResponseEntity.ok("OK");

                // Deduplication check
                if (processedMessageIds.containsKey(messageId)) {
                    System.out.println("♻️ Duplicate webhook received for message ID: " + messageId);
                    return ResponseEntity.ok("OK");
                }
                processedMessageIds.put(messageId, System.currentTimeMillis());

                // Extract phone number without @c.us suffix
                String phoneNumber = from.replace("@c.us", "");

                // Only process from verifier
                if (!phoneNumber.equals(config.getVerifierNumber())) {
                    System.out.println("⚠️ Ignoring message from non-verifier: " + phoneNumber);
                    return ResponseEntity.ok("OK");
                }

                System.out.println("💬 Processing command from admin: " + text);

                // Process commands
                processCommand(text.trim(), phoneNumber);
            }

            return ResponseEntity.ok("OK");

        } catch (Exception e) {
            System.err.println("❌ Webhook error: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(500).body("Error");
        }
    }

    private void processCommand(String text, String from) {
        try {
            if (text.startsWith("/approve ")) {
                Long userId = Long.parseLong(text.substring(9).trim());
                User user = userService.approveUser(userId);
                whatsAppService.sendApprovalConfirmation(user);

            } else if (text.startsWith("/reject ")) {
                Long userId = Long.parseLong(text.substring(8).trim());
                User user = userService.rejectUser(userId);
                whatsAppService.sendRejectionConfirmation(user);

            } else if (text.equals("/pending")) {
                List<User> pendingUsers = userService.findPendingUsers();
                whatsAppService.sendPendingUsersList(from, pendingUsers);

            } else if (text.equals("/help")) {
                sendHelpMessage(from);

            } else if (text.equals("/stats")) {
                sendStatsMessage(from);
            }

        } catch (NumberFormatException e) {
            System.err.println("❌ Invalid user ID format");
        } catch (Exception e) {
            System.err.println("❌ Command processing error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void sendHelpMessage(String to) {
        String help = "📚 *Available Commands*\n\n" +
                "/pending - List all pending users\n" +
                "/approve <id> - Approve user by ID\n" +
                "/reject <id> - Reject user by ID\n" +
                "/stats - Show usage statistics\n" +
                "/help - Show this help message";

        System.out.println("📖 Sending help message to: " + to);
        // Send actual message via WhatsApp
        try {
            whatsAppService.sendMessageSafely(to, help);
        } catch (Exception e) {
            System.err.println("❌ Failed to send help message: " + e.getMessage());
        }
    }

    private void sendStatsMessage(String to) {
        Map<String, Object> stats = whatsAppService.getUsageStats();
        String statsMsg = String.format(
                "📊 *WhatsApp Usage Statistics*\n\n" +
                        "Hourly: %s / %s\n" +
                        "Daily: %s / %s\n\n" +
                        "Messages are rate-limited to prevent spam.",
                stats.get("hourly"),
                stats.get("hourlyLimit"),
                stats.get("daily"),
                stats.get("dailyLimit"));

        System.out.println("📊 Sending stats to: " + to);
        try {
            whatsAppService.sendMessageSafely(to, statsMsg);
        } catch (Exception e) {
            System.err.println("❌ Failed to send stats message: " + e.getMessage());
        }
    }

    @GetMapping("/whatsapp/health")
    public ResponseEntity<Map<String, Object>> healthCheck() {
        Map<String, Object> health = Map.of(
                "status", "OK",
                "whatsappEnabled", config.isEnabled(),
                "adminNumber", config.getAdminNumber(),
                "usage", whatsAppService.getUsageStats());
        return ResponseEntity.ok(health);
    }
}
