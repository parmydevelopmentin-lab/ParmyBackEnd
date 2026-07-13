package com.parmy.parmy_backend.config;

import com.parmy.parmy_backend.model.Project;
import com.parmy.parmy_backend.repository.ProjectRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@Component
@Order(3) // Run after DataInitializer
public class ProjectDataInitializer implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(ProjectDataInitializer.class);

    @Autowired
    private ProjectRepository projectRepository;

    @Override
    public void run(String... args) throws Exception {
        if (projectRepository.count() == 0) {
            seedProjects();
        } else {
            logger.info("Projects database already populated, skipping project seeding.");
        }
    }

    private void seedProjects() {
        logger.info("Seeding default projects into MongoDB...");
        
        List<Project> defaultProjects = Arrays.asList(
            createProject(
                "e-commerce-mobile-app",
                "E-Commerce Mobile App Platform",
                "A cross-platform mobile app for retail businesses.",
                "A robust, cross-platform mobile application built using React Native and powered by a Spring Boot microservices backend. Features include secure Stripe/PayPal integration, real-time push notifications, offline search capabilities, user shopping carts, wishlist management, and a comprehensive admin panel for inventory and sales tracking.",
                45000.0,
                "INR",
                "Software Development",
                Arrays.asList("React Native", "Spring Boot", "MongoDB", "Stripe")
            ),
            createProject(
                "ai-customer-chatbot",
                "AI Customer Assistance Chatbot",
                "Intelligent conversational agent powered by AI/ML.",
                "An advanced conversational chatbot designed to streamline customer support operations. Using natural language processing (NLP) and trained on custom corporate knowledge base datasets, it can handle multi-turn conversations, resolve common customer queries, route complex issues to human agents, and integrate seamlessly with Slack, WhatsApp, and websites.",
                35000.0,
                "INR",
                "AI & Machine Learning",
                Arrays.asList("Python", "TensorFlow", "FastAPI", "NLP", "React")
            ),
            createProject(
                "cloud-infra-migration",
                "Enterprise Cloud Infrastructure Setup",
                "Secure, automated AWS cloud environment.",
                "A professional AWS cloud infrastructure setup automated using Terraform (Infrastructure as Code). Includes multi-AZ VPC, private and public subnets, secure RDS database clusters, application load balancers, auto-scaling ECS container instances, cloud monitoring dashboards, and robust CI/CD pipelines using GitHub Actions.",
                60000.0,
                "INR",
                "Cloud Services",
                Arrays.asList("AWS", "Terraform", "Docker", "CI/CD", "GitHub Actions")
            ),
            createProject(
                "seo-marketing-booster",
                "SEO & Digital Marketing Campaign Pack",
                "Comprehensive search engine visibility campaign.",
                "A target-driven digital marketing and SEO package designed to boost search engine visibility and increase organic lead acquisition. Includes full audit, on-page keyword optimizations, competitive research, high-quality backlink building, Google Analytics dashboard configurations, and a 3-month tailored social media content strategy.",
                18000.0,
                "INR",
                "SEO Services",
                Arrays.asList("SEO", "Google Analytics", "SEM", "Content Strategy")
            ),
            createProject(
                "healthcare-patient-portal",
                "Secure Patient Healthcare Portal",
                "HIPAA-compliant portal for patient record management.",
                "A highly secure, HIPAA-compliant patient management system. It allows patients to schedule virtual doctor appointments, securely access their electronic health records (EHR), upload medical prescriptions, message doctors privately, and complete online payments safely.",
                95000.0,
                "INR",
                "Software Development",
                Arrays.asList("Next.js", "Java", "Spring Security", "PostgreSQL", "HIPAA")
            )
        );

        projectRepository.saveAll(defaultProjects);
        logger.info("✅ Seeded {} projects successfully!", defaultProjects.size());
    }

    private Project createProject(
            String slug, String title, String shortDesc, String desc,
            double price, String currency, String category, List<String> tags) {
        Project project = new Project();
        project.setSlug(slug);
        project.setTitle(title);
        project.setShortDescription(shortDesc);
        project.setDescription(desc);
        project.setPrice(price);
        project.setCurrency(currency);
        project.setCategory(category);
        project.setTags(tags);
        project.setActive(true);
        project.setCreatedBy("system");
        project.setUpdatedBy("system");
        project.setCreatedAt(LocalDateTime.now());
        project.setUpdatedAt(LocalDateTime.now());
        return project;
    }
}
