package com.app.tanganbaik.service;

import com.app.tanganbaik.config.WhatsAppConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

@Component
public class WhatsAppRateLimiter {

    private final ConcurrentHashMap<String, Queue<Long>> messageHistory = new ConcurrentHashMap<>();
    private final int hourlyLimit;
    private final int dailyLimit;

    @Autowired
    public WhatsAppRateLimiter(WhatsAppConfig config) {
        this.hourlyLimit = config.getHourlyLimit();
        this.dailyLimit = config.getDailyLimit();
    }

    public boolean canSendMessage(String recipient) {
        String key = recipient;
        Queue<Long> history = messageHistory.computeIfAbsent(key, k -> new LinkedList<>());

        long now = System.currentTimeMillis();
        long oneHourAgo = now - TimeUnit.HOURS.toMillis(1);
        long oneDayAgo = now - TimeUnit.DAYS.toMillis(1);

        // Remove old entries
        history.removeIf(timestamp -> timestamp < oneDayAgo);

        // Check hourly limit
        long messagesLastHour = history.stream()
                .filter(timestamp -> timestamp > oneHourAgo)
                .count();

        if (messagesLastHour >= hourlyLimit) {
            System.err.println("⚠️ HOURLY LIMIT REACHED: " + messagesLastHour + "/" + hourlyLimit);
            return false;
        }

        // Check daily limit
        if (history.size() >= dailyLimit) {
            System.err.println("⚠️ DAILY LIMIT REACHED: " + history.size() + "/" + dailyLimit);
            return false;
        }

        return true;
    }

    public void recordMessage(String recipient) {
        String key = recipient;
        Queue<Long> history = messageHistory.computeIfAbsent(key, k -> new LinkedList<>());
        history.add(System.currentTimeMillis());
    }

    public Map<String, Object> getStats(String recipient) {
        Queue<Long> history = messageHistory.get(recipient);
        if (history == null) {
            return Map.of("hourly", 0, "daily", 0, "hourlyLimit", hourlyLimit, "dailyLimit", dailyLimit);
        }

        long now = System.currentTimeMillis();
        long oneHourAgo = now - TimeUnit.HOURS.toMillis(1);

        long hourly = history.stream()
                .filter(timestamp -> timestamp > oneHourAgo)
                .count();

        return Map.of(
                "hourly", hourly,
                "daily", history.size(),
                "hourlyLimit", hourlyLimit,
                "dailyLimit", dailyLimit);
    }
}
