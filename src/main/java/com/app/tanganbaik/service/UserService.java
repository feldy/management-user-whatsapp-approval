package com.app.tanganbaik.service;

import com.app.tanganbaik.entity.User;
import com.app.tanganbaik.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final WhatsAppService whatsAppService;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder,
            WhatsAppService whatsAppService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.whatsAppService = whatsAppService;
    }

    public List<User> findAll() {
        return userRepository.findAll();
    }

    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    public User save(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        // New users are not approved by default
        if (user.getApproved() == null) {
            user.setApproved(false);
        }

        User savedUser = userRepository.save(user);

        // Send WhatsApp notification if user is pending approval
        if (!savedUser.getApproved()) {
            try {
                whatsAppService.sendDualRegistrationNotification(savedUser);
            } catch (Exception e) {
                System.err.println("⚠️ Failed to send WhatsApp notification: " + e.getMessage());
                // Don't fail user creation if WhatsApp fails
            }
        }

        return savedUser;
    }

    public User update(Long id, User userDetails) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setName(userDetails.getName());
        user.setEmail(userDetails.getEmail());
        if (userDetails.getPassword() != null && !userDetails.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(userDetails.getPassword()));
        }
        user.setRole(userDetails.getRole());
        // Keep existing approved status when updating
        if (userDetails.getApproved() != null) {
            user.setApproved(userDetails.getApproved());
        }

        return userRepository.save(user);
    }

    public void deleteById(Long id) {
        userRepository.deleteById(id);
    }

    // Approval methods
    public User approveUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setApproved(true);
        return userRepository.save(user);
    }

    public User rejectUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setApproved(false);
        return userRepository.save(user);
    }

    public List<User> findPendingUsers() {
        return userRepository.findAll().stream()
                .filter(user -> !user.getApproved())
                .collect(Collectors.toList());
    }
}
