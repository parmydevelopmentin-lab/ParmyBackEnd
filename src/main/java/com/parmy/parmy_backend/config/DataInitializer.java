//package com.trivio.trivio_backend.config;
//
//import com.trivio.trivio_backend.model.AuthProvider;
//import com.trivio.trivio_backend.model.User;
//import com.trivio.trivio_backend.model.UserRole;
//import com.trivio.trivio_backend.repository.UserRepository;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.CommandLineRunner;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.stereotype.Component;
//
//import java.util.HashSet;
//import java.util.Set;
//
//@Component
//public class DataInitializer implements CommandLineRunner {
//
//    private static final Logger logger = LoggerFactory.getLogger(DataInitializer.class);
//
//    @Autowired
//    private UserRepository userRepository;
//
//    @Autowired
//    private PasswordEncoder passwordEncoder;
//
//    @Override
//    public void run(String... args) throws Exception {
//        createAdminUser();
//    }
//
//    /**
//     * Create default admin user if none exists
//     */
//    private void createAdminUser() {
//        try {
//            // Check if any admin user exists
//            boolean adminExists = userRepository.findAll().stream()
//                    .anyMatch(user -> user.getRoles().contains(UserRole.ADMIN));
//
//            if (!adminExists) {
//                // Create admin user
//                User adminUser = new User();
//                adminUser.setEmail("trivioglobal@gmail.com");
//                adminUser.setUsername("admin");
//                adminUser.setPassword(passwordEncoder.encode("admin123")); // Default password
//                adminUser.setVerified(true);
//                adminUser.setAuthProvider(AuthProvider.LOCAL);
//
//                // Set admin role
//                Set<UserRole> roles = new HashSet<>();
//                roles.add(UserRole.USER);
//                roles.add(UserRole.ADMIN);
//                adminUser.setRoles(roles);
//
//                userRepository.save(adminUser);
//
//                logger.info("✅ Default admin user created successfully!");
//                logger.info("📧 Email: trivioglobal@gmail.com");
//                logger.info("🔐 Password: admin123");
//                logger.info("⚠️  Please change the default password after first login!");
//
//            } else {
//                logger.info("✅ Admin user already exists, skipping creation.");
//            }
//
//        } catch (Exception e) {
//            logger.error("❌ Failed to create admin user", e);
//        }
//    }
//}