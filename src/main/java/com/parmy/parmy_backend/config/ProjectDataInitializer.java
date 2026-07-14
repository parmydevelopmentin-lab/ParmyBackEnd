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
            // 5 default projects
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

            // 19 YouTube Projects
            createProject(
                "phish-catcher-ml-django",
                "PhishCatcher | Client-Side Defense Against Web Spoofing Attacks Using ML",
                "ML-based client-side defense system against web spoofing attacks.",
                "A comprehensive Django-based client-side defense framework that detects and prevents web spoofing (phishing) attacks using machine learning classification. It analyzes web page layouts, DOM elements, and visual cues to verify brand authenticity.",
                12000.0,
                "INR",
                "AI/ML",
                Arrays.asList("Django", "Python", "Machine Learning", "Security"),
                "https://img.youtube.com/vi/r41YuEF6FwQ/hqdefault.jpg"
            ),
            createProject(
                "ai-employee-stress-prediction",
                "AI Employee Stress Prediction Using Machine Learning",
                "Predict employee stress using LSTM, XGBoost, and XAI.",
                "A machine learning framework utilising Flask, LSTM networks, and XGBoost models combined with Explainable AI (XAI) techniques. It analyzes multiple employee telemetry metrics to evaluate workplace stress levels and outputs detailed visual dashboards for HR.",
                18000.0,
                "INR",
                "AI/ML",
                Arrays.asList("Flask", "LSTM", "XGBoost", "XAI", "Python", "Machine Learning"),
                "https://img.youtube.com/vi/86Vgq2g_S7E/hqdefault.jpg"
            ),
            createProject(
                "ai-career-ready",
                "AI CareerReady | AI Resume Analyzer & Mock Interview System",
                "AI-powered resume optimizer and mock interview platform.",
                "An intelligent career readiness dashboard built with Django. Features include an automated resume analyzer, a job description matching engine, and an interactive AI mock interviewer that provides contextual feedback for candidate preparation.",
                15000.0,
                "INR",
                "AI/ML",
                Arrays.asList("Django", "Python", "AI", "NLP", "React"),
                "https://img.youtube.com/vi/rj2puooGBoc/hqdefault.jpg"
            ),
            createProject(
                "air-weather-quality-monitoring",
                "Air and Weather Quality Monitoring using Artificial Intelligence",
                "AI-driven real-time atmospheric and weather quality monitoring.",
                "An IoT and ML-powered climate telemetry system built with Flask. It visualizes local air quality index (AQI) values, temperature, humidity, and predicts future pollution levels using historic datasets and classification models.",
                12000.0,
                "INR",
                "AI/ML",
                Arrays.asList("Flask", "Python", "IoT", "Machine Learning"),
                "https://img.youtube.com/vi/TvejVbUwRps/hqdefault.jpg"
            ),
            createProject(
                "email-spam-detection",
                "Email Spam Detection using Machine Learning Algorithms",
                "Content-based email spam detection system using ML.",
                "A high-performance spam filtering application. Built using Scikit-Learn NLP pipelines, it classifies incoming emails into spam or ham based on visual/text patterns, vocabulary weights, and structural metadata.",
                8000.0,
                "INR",
                "AI/ML",
                Arrays.asList("Python", "Scikit-Learn", "NLP", "Machine Learning"),
                "https://img.youtube.com/vi/ofxI7zSPvaM/hqdefault.jpg"
            ),
            createProject(
                "ai-financial-market-forecasting",
                "AI-Driven Predictive Analytics for Financial Market Forecasting",
                "AI and deep learning framework for market trend predictions.",
                "An advanced financial forecasting dashboard powered by Flask, recurrent neural networks (RNN/LSTM), and regression trees. It predicts stock and asset prices, calculates volatility indices, and provides visual trading trend analytics.",
                22000.0,
                "INR",
                "AI/ML",
                Arrays.asList("Flask", "Deep Learning", "Python", "FinTech", "LSTM"),
                "https://img.youtube.com/vi/QrADwUNshjc/hqdefault.jpg"
            ),
            createProject(
                "phishing-website-detection-url",
                "Machine Learning-Based Real-Time Phishing Website Detection System",
                "Real-time phishing website detector using URL feature analysis.",
                "A lightweight security module that analyzes domain and URL syntax patterns (length, dots, special tokens, protocol anomalies) in real-time to classify phishing websites using random forest models.",
                11000.0,
                "INR",
                "AI/ML",
                Arrays.asList("Python", "Machine Learning", "Security", "Flask"),
                "https://img.youtube.com/vi/-6PYNRUiCFE/hqdefault.jpg"
            ),
            createProject(
                "ai-medical-chatbot",
                "AI-Driven Medical Chatbot using NLP & ML",
                "Symptoms-based disease predictor and hospital recommender.",
                "An NLP-enabled healthcare assistant. Patients input symptoms in natural language, and the chatbot predicts potential diseases while recommending matching departments and nearby hospitals.",
                16000.0,
                "INR",
                "AI/ML",
                Arrays.asList("NLP", "FastAPI", "React", "Healthcare", "Machine Learning"),
                "https://img.youtube.com/vi/NR2tOQ3hn9A/hqdefault.jpg"
            ),
            createProject(
                "sql-attendance-marks-management",
                "SQL-Based Attendance, Marks & Assignment Management System",
                "Comprehensive student academic records manager with Django and MySQL.",
                "A secure portal for educational institutions. Teachers can log student attendance, grade assignments, publish term marks, and students can download performance analytics from a clean MySQL relational database.",
                14000.0,
                "INR",
                "Software Development",
                Arrays.asList("Django", "MySQL", "Python", "Database"),
                "https://img.youtube.com/vi/YD1uxL4Ggl0/hqdefault.jpg"
            ),
            createProject(
                "heart-disease-prediction-ecg-xray",
                "Heart Disease Prediction System using Machine Learning",
                "Predicts heart disease using ECG and Chest X-Ray image analysis.",
                "A hybrid diagnostic system. Combines numerical health history analytics with computer vision (CNN models) to classify chest X-ray images and ECG signals for accurate heart disease pre-screening.",
                24000.0,
                "INR",
                "AI/ML",
                Arrays.asList("Django", "CNN", "Deep Learning", "Healthcare", "Python"),
                "https://img.youtube.com/vi/gCdh0Xzi0ks/hqdefault.jpg"
            ),
            createProject(
                "ai-voice-to-sql-assistant",
                "AI Voice-to-SQL Assistant",
                "Convert spoken commands into executable SQL queries with visual analytics.",
                "An innovative database assistant that translates natural voice speech into SQL statements, executes them on the target DB, and automatically renders the result set in charts and tables.",
                19000.0,
                "INR",
                "AI/ML",
                Arrays.asList("SpeechRecognition", "NLP", "React", "Python", "Database"),
                "https://img.youtube.com/vi/JvuveyVa_gU/hqdefault.jpg"
            ),
            createProject(
                "indian-temple-3d-virtual-museum",
                "Indian Temple Museum | 3D Virtual Museum",
                "Interactive 3D virtual museum showcasing Indian heritage.",
                "An immersive virtual heritage tour platform. Built using Three.js and WebGL on the frontend and FastAPI/MySQL on the backend, allowing users to explore virtual rooms with historical artifacts in 3D.",
                28000.0,
                "INR",
                "Software Development",
                Arrays.asList("React", "Three.js", "FastAPI", "MySQL", "3D"),
                "https://img.youtube.com/vi/NGK44gwqiDI/hqdefault.jpg"
            ),
            createProject(
                "crowd-count-ai-yolov5",
                "CrowdCountAI | Real-Time Crowd Detection & Monitoring System",
                "Real-time crowd counting and object tracking with YOLOv5 and DeepSORT.",
                "A high-speed video analytics system. Detects and tracks people in crowd densities using YOLOv5 and handles re-identification/tracking across camera streams with DeepSORT.",
                23000.0,
                "INR",
                "AI/ML",
                Arrays.asList("YOLOv5", "DeepSORT", "Computer Vision", "Python"),
                "https://img.youtube.com/vi/M19qRuvLhQ8/hqdefault.jpg"
            ),
            createProject(
                "audio-to-sign-language-converter",
                "Audio to Sign Language Converter",
                "Real-time speech translation into animated Indian Sign Language.",
                "An accessibility application that records microphone input, translates English/Hindi text into structured sign tokens, and displays corresponding 3D avatar animations or video snippets.",
                17000.0,
                "INR",
                "Software Development",
                Arrays.asList("SpeechRecognition", "React", "Python", "Accessibility"),
                "https://img.youtube.com/vi/Hxcw4Ks4qp0/hqdefault.jpg"
            ),
            createProject(
                "co2-estimation-tracker-ar",
                "CO2 Estimation Tracker | AI + AR Powered Carbon Footprint Detection",
                "AI and AR-driven carbon footprint detection and estimation.",
                "A smart environmental tracking dashboard. It uses image recognition and augmented reality inputs to estimate carbon footprints of physical items, vehicles, and lifestyles, recommending offsets.",
                21000.0,
                "INR",
                "AI/ML",
                Arrays.asList("Python", "AR", "AI", "Environment"),
                "https://img.youtube.com/vi/WC_VAtx99FM/hqdefault.jpg"
            ),
            createProject(
                "automated-number-plate-detection-ocr",
                "Automated Number Plate Detection Using OCR",
                "Real-time vehicle license plate detection and OCR extraction.",
                "A vehicle logging and parking gate automation system. Utilizes OpenCV for frame analysis, locates license plates via boundary search, and extracts text using EasyOCR/Tesseract.",
                13000.0,
                "INR",
                "AI/ML",
                Arrays.asList("Django", "OpenCV", "EasyOCR", "Python"),
                "https://img.youtube.com/vi/EI5m9cyEjmg/hqdefault.jpg"
            ),
            createProject(
                "cyber-breach-monitoring-system",
                "Cyber Breach Monitoring System",
                "Detects and monitors network traffic breaches using ML classification.",
                "A security information dashboard. It consumes network log data feeds, detects anomalies and intrusion footprints using random forest classifiers, and highlights breach status alerts.",
                16000.0,
                "INR",
                "AI/ML",
                Arrays.asList("Flask", "Python", "Machine Learning", "Security"),
                "https://img.youtube.com/vi/ZXQ1sB5iuB8/hqdefault.jpg"
            ),
            createProject(
                "online-auction-system",
                "Online Auction System",
                "Real-time online bidding and auction management portal.",
                "A highly interactive web portal featuring real-time bidding updates via WebSockets, product galleries, countdown timers, secure payment processing, and seller metrics.",
                15000.0,
                "INR",
                "Software Development",
                Arrays.asList("React", "Python", "MySQL", "WebSockets"),
                "https://img.youtube.com/vi/2BtRyw2nGBc/hqdefault.jpg"
            ),
            createProject(
                "parmy-technologies-launch",
                "Welcome to Parmy Technologies Pvt Ltd | Academic Project Development",
                "Official portal for corporate services and project development.",
                "The official introductory landing page and corporate service portal showcasing software development, technical training courses, student project consultancy, and corporate staffing portfolios.",
                5000.0,
                "INR",
                "Software Development",
                Arrays.asList("React", "Spring Boot", "Corporate"),
                "https://img.youtube.com/vi/ieK_Q6g2Fis/hqdefault.jpg"
            )
        );

        int newlySeeded = 0;
        int updated = 0;
        for (Project p : defaultProjects) {
            if (!projectRepository.existsBySlug(p.getSlug())) {
                projectRepository.save(p);
                newlySeeded++;
                logger.info("✅ Seeded new project: {}", p.getTitle());
            } else {
                Project existing = projectRepository.findBySlug(p.getSlug()).orElse(null);
                if (existing != null) {
                    existing.setTitle(p.getTitle());
                    existing.setShortDescription(p.getShortDescription());
                    existing.setDescription(p.getDescription());
                    existing.setThumbnailUrl(p.getThumbnailUrl());
                    existing.setCategory(p.getCategory());
                    existing.setTags(p.getTags());
                    projectRepository.save(existing);
                    updated++;
                }
            }
        }
        logger.info("Seeding check completed. Seeded {} new projects, updated {} projects.", newlySeeded, updated);
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
