package com.app.tanganbaik.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class WhatsAppConfig {

    @Value("${whatsapp.api.url:http://localhost:3001/api}")
    private String apiUrl;

    @Value("${whatsapp.session:default}")
    private String session;

    @Value("${whatsapp.admin.number}")
    private String adminNumber;

    @Value("${whatsapp.verifier.number}")
    private String verifierNumber;

    @Value("${whatsapp.rate.limit.hourly:20}")
    private int hourlyLimit;

    @Value("${whatsapp.rate.limit.daily:100}")
    private int dailyLimit;

    @Value("${whatsapp.delay.min:2000}")
    private int minDelay; // 2 seconds

    @Value("${whatsapp.delay.max:5000}")
    private int maxDelay; // 5 seconds

    @Value("${whatsapp.enabled:true}")
    private boolean enabled;

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    // Getters
    public String getApiUrl() {
        return apiUrl;
    }

    public String getSession() {
        return session;
    }

    public String getAdminNumber() {
        return adminNumber;
    }

    public String getVerifierNumber() {
        return verifierNumber;
    }

    public int getHourlyLimit() {
        return hourlyLimit;
    }

    public int getDailyLimit() {
        return dailyLimit;
    }

    public int getMinDelay() {
        return minDelay;
    }

    public int getMaxDelay() {
        return maxDelay;
    }

    public boolean isEnabled() {
        return enabled;
    }
}
