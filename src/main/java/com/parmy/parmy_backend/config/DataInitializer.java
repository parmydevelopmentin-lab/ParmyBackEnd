package com.parmy.parmy_backend.config;

import com.parmy.parmy_backend.model.AuthProvider;
import com.parmy.parmy_backend.model.User;
import com.parmy.parmy_backend.model.UserRole;
import com.parmy.parmy_backend.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Component
public class DataInitializer implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(DataInitializer.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        createAdminUser();
    }

    /**
     * Create or upgrade default admin user
     */
    private void createAdminUser() {
        try {
            String adminEmail = "parmydevelopment.in@gmail.com";
            Optional<User> userOpt = userRepository.findByEmail(adminEmail);

            if (userOpt.isPresent()) {
                User existingUser = userOpt.get();
                logger.info("Found existing user with email: {}. Updating to ADMIN with password 'admin123'...", adminEmail);
                
                // Force update password, verification status, and admin roles
                existingUser.setPassword(passwordEncoder.encode("admin123"));
                existingUser.setVerified(true);
                
                Set<UserRole> roles = existingUser.getRoles();
                if (roles == null) {
                    roles = new HashSet<>();
                }
                roles.add(UserRole.USER);
                roles.add(UserRole.ADMIN);
                existingUser.setRoles(roles);
                
                userRepository.save(existingUser);
                logger.info("✅ Existing user {} successfully updated/upgraded to ADMIN!", adminEmail);
            } else {
                logger.info("No user found with email: {}. Creating a new ADMIN user...", adminEmail);
                
                User adminUser = new User();
                adminUser.setEmail(adminEmail);
                adminUser.setUsername("admin");
                adminUser.setPassword(passwordEncoder.encode("admin123"));
                adminUser.setVerified(true);
                adminUser.setAuthProvider(AuthProvider.LOCAL);

                Set<UserRole> roles = new HashSet<>();
                roles.add(UserRole.USER);
                roles.add(UserRole.ADMIN);
                adminUser.setRoles(roles);

                userRepository.save(adminUser);
                logger.info("✅ New default admin user created successfully!");
            }
            
            logger.info("📧 Admin Email: {}", adminEmail);
            logger.info("🔐 Admin Password: admin123");

        } catch (Exception e) {
            logger.error("❌ Failed to create or upgrade admin user", e);
        }
    }
}