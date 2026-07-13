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
        seedProjects();
    }

    private void seedProjects() {
        logger.info("Checking and seeding default projects into MongoDB...");
        
        List<Project> defaultProjects = Arrays.asList(
            createProject(
                "e-commerce-mobile-app",
                "E-Commerce Mobile App Platform",
                "A cross-platform mobile app for retail businesses.",
                "A robust, cross-platform mobile application built using React Native and powered by a Spring Boot microservices backend. Features include secure Stripe/PayPal integration, real-time push notifications, offline search capabilities, user shopping carts, wishlist management, and a comprehensive admin panel for inventory and sales tracking.",
                45000.0,
                "INR",
                "Software Development",
                Arrays.asList("React Native", "Spring Boot", "MongoDB", "Stripe"),
                "/gradspot.png"
            ),
            createProject(
                "ai-customer-chatbot",
                "AI Customer Assistance Chatbot",
                "Intelligent conversational agent powered by AI/ML.",
                "An advanced conversational chatbot designed to streamline customer support operations. Using natural language processing (NLP) and trained on custom corporate knowledge base datasets, it can handle multi-turn conversations, resolve common customer queries, route complex issues to human agents, and integrate seamlessly with Slack, WhatsApp, and websites.",
                35000.0,
                "INR",
                "AI/ML",
                Arrays.asList("Python", "TensorFlow", "FastAPI", "NLP", "React"),
                "/logo.png"
            ),
            createProject(
                "cloud-infra-migration",
                "Enterprise Cloud Infrastructure Setup",
                "Secure, automated AWS cloud environment.",
                "A professional AWS cloud infrastructure setup automated using Terraform (Infrastructure as Code). Includes multi-AZ VPC, private and public subnets, secure RDS database clusters, application load balancers, auto-scaling ECS container instances, cloud monitoring dashboards, and robust CI/CD pipelines using GitHub Actions.",
                60000.0,
                "INR",
                "Cloud Services",
                Arrays.asList("AWS", "Terraform", "Docker", "CI/CD", "GitHub Actions"),
                "/DigiDefense.png"
            ),
            createProject(
                "seo-marketing-booster",
                "SEO & Digital Marketing Campaign Pack",
                "Comprehensive search engine visibility campaign.",
                "A target-driven digital marketing and SEO package designed to boost search engine visibility and increase organic lead acquisition. Includes full audit, on-page keyword optimizations, competitive research, high-quality backlink building, Google Analytics dashboard configurations, and a 3-month tailored social media content strategy.",
                18000.0,
                "INR",
                "SEO Services",
                Arrays.asList("SEO", "Google Analytics", "SEM", "Content Strategy"),
                "/logo.png"
            ),
            createProject(
                "healthcare-patient-portal",
                "Secure Patient Healthcare Portal",
                "HIPAA-compliant portal for patient record management.",
                "A highly secure, HIPAA-compliant patient management system. It allows patients to schedule virtual doctor appointments, securely access their electronic health records (EHR), upload medical prescriptions, message doctors privately, and complete online payments safely.",
                95000.0,
                "INR",
                "Software Development",
                Arrays.asList("Next.js", "Java", "Spring Security", "PostgreSQL", "HIPAA"),
                "/office.png"
            ),
            createProject(
                "ai-career-ready",
                "AI CareerReady",
                "AI-powered resume analyzer and mock interview system.",
                "An advanced AI-powered career preparation platform featuring a resume parser/analyzer, job description matcher, and interactive mock interview simulator using Django AI. It helps candidates identify career readiness gaps, optimize resumes for ATS alignment, and practice multi-turn interviews.",
                15000.0,
                "INR",
                "AI/ML",
                Arrays.asList("Django", "Python", "AI", "NLP", "React"),
                "/ai-career-ready.png"
            ),
            createProject(
                "ai-employee-stress-prediction",
                "AI Employee Stress Prediction",
                "Predicts and analyzes employee stress using LSTM, XGBoost, and XAI.",
                "A comprehensive machine learning system utilizing Flask, LSTM, XGBoost, and Explainable AI (XAI) to analyze telemetry data and predict employee stress levels. The platform offers cognitive workload insights, mental health telemetry dashboards, and actionable feedback loops for HR management.",
                18000.0,
                "INR",
                "AI/ML",
                Arrays.asList("Flask", "Python", "LSTM", "XGBoost", "XAI", "Machine Learning"),
                "/employee-stress.png"
            ),
            createProject(
                "email-spam-detection",
                "Email Spam Detection",
                "Detects and filters spam emails using machine learning.",
                "A high-accuracy machine learning system designed to detect and filter out spam emails. Built using Python, Scikit-Learn, and NLP pipelines, it classifies incoming mail packets based on content, domain trust, and header anomalies, providing detailed metrics on a clean React dashboard.",
                8000.0,
                "INR",
                "AI/ML",
                Arrays.asList("Python", "Scikit-Learn", "NLP", "React", "Machine Learning"),
                "/email-spam.png"
            ),
            createProject(
                "air-weather-quality-monitoring",
                "Air and Weather Quality Monitoring",
                "AI-based real-time weather and air quality index monitoring.",
                "An IoT and AI-driven monitoring system powered by Flask and machine learning. It collects, visualizes, and predicts local air quality index (AQI) values, temperature, humidity, and atmospheric pollutants in real-time, displaying details on interactive dashboards.",
                12000.0,
                "INR",
                "AI/ML",
                Arrays.asList("Flask", "Python", "IoT", "Machine Learning", "React"),
                "/weather-monitoring.png"
            )
        );

        int newlySeeded = 0;
        for (Project p : defaultProjects) {
            if (!projectRepository.existsBySlug(p.getSlug())) {
                projectRepository.save(p);
                newlySeeded++;
                logger.info("✅ Seeded new project: {}", p.getTitle());
            } else {
                // If it already exists, let's update it to ensure it has the correct thumbnail and details
                projectRepository.findBySlug(p.getSlug()).ifPresent(existing -> {
                    existing.setThumbnailUrl(p.getThumbnailUrl());
                    existing.setTags(p.getTags());
                    existing.setCategory(p.getCategory());
                    projectRepository.save(existing);
                });
            }
        }
        logger.info("Seeding check completed. Seeded {} new projects.", newlySeeded);
    }

    private Project createProject(
            String slug, String title, String shortDesc, String desc,
            double price, String currency, String category, List<String> tags, String thumbnailUrl) {
        Project project = new Project();
        project.setSlug(slug);
        project.setTitle(title);
        project.setShortDescription(shortDesc);
        project.setDescription(desc);
        project.setPrice(price);
        project.setCurrency(currency);
        project.setCategory(category);
        project.setTags(tags);
        project.setThumbnailUrl(thumbnailUrl);
        project.setActive(true);
        project.setCreatedBy("system");
        project.setUpdatedBy("system");
        project.setCreatedAt(LocalDateTime.now());
        project.setUpdatedAt(LocalDateTime.now());
        return project;
    }
}
