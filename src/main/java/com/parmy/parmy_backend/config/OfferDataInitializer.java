//package com.trivio.trivio_backend.config;
//
//import com.trivio.trivio_backend.model.Offer;
//import com.trivio.trivio_backend.model.OfferStatus;
//import com.trivio.trivio_backend.repository.OfferRepository;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.CommandLineRunner;
//import org.springframework.core.annotation.Order;
//import org.springframework.stereotype.Component;
//
//import java.time.LocalDate;
//import java.util.Arrays;
//import java.util.List;
//
//@Component
//@Order(3) // Run after DataInitializer and ExpenseDataInitializer
//public class OfferDataInitializer implements CommandLineRunner {
//
//    @Autowired
//    private OfferRepository offerRepository;
//
//    @Override
//    public void run(String... args) throws Exception {
//        // Only create dummy data if no offers exist
//        if (offerRepository.count() == 0) {
//            createDummyOffers();
//            System.out.println("Dummy offer data has been created!");
//        } else {
//            System.out.println("Offer data already exists, skipping dummy data creation.");
//        }
//    }
//
//    private void createDummyOffers() {
//        List<Offer> dummyOffers = Arrays.asList(
//            // Recent offers - various statuses
//            createOffer(
//                "Aarav Sharma",
//                "aarav.sharma@example.com",
//                "Software Development Engineer I",
//                LocalDate.now().plusDays(15),
//                "Hyderabad, India",
//                "6 months",
//                "123 Tech Park, Hyderabad, Telangana, India",
//                OfferStatus.SENT,
//                "admin@trivioglobal.com"
//            ),
//
//            createOffer(
//                "Priya Patel",
//                "priya.patel@example.com",
//                "Frontend Developer",
//                LocalDate.now().plusDays(20),
//                "Bangalore, India",
//                "6 months",
//                "456 Innovation Hub, Bangalore, Karnataka, India",
//                OfferStatus.ACCEPTED,
//                "admin@trivioglobal.com"
//            ),
//
//            createOffer(
//                "Rohit Kumar",
//                "rohit.kumar@example.com",
//                "Backend Developer",
//                LocalDate.now().plusDays(10),
//                "Remote - India",
//                "3 months",
//                "789 Cyber City, Pune, Maharashtra, India",
//                OfferStatus.DRAFT,
//                "admin@trivioglobal.com"
//            ),
//
//            createOffer(
//                "Anita Singh",
//                "anita.singh@example.com",
//                "UI/UX Designer",
//                LocalDate.now().plusDays(25),
//                "Mumbai, India",
//                "6 months",
//                "321 Design Plaza, Mumbai, Maharashtra, India",
//                OfferStatus.SENT,
//                "admin@trivioglobal.com"
//            ),
//
//            createOffer(
//                "Vikram Reddy",
//                "vikram.reddy@example.com",
//                "DevOps Engineer",
//                LocalDate.now().plusDays(30),
//                "Chennai, India",
//                "6 months",
//                "654 Cloud Avenue, Chennai, Tamil Nadu, India",
//                OfferStatus.REJECTED,
//                "admin@trivioglobal.com"
//            ),
//
//            // Older offers
//            createOffer(
//                "Sakshi Gupta",
//                "sakshi.gupta@example.com",
//                "Product Manager",
//                LocalDate.now().minusDays(5), // Past joining date
//                "Delhi, India",
//                "6 months",
//                "987 Business Center, New Delhi, India",
//                OfferStatus.EXPIRED,
//                "admin@trivioglobal.com"
//            ),
//
//            createOffer(
//                "Arjun Menon",
//                "arjun.menon@example.com",
//                "Full Stack Developer",
//                LocalDate.now().plusDays(45),
//                "Kochi, India",
//                "6 months",
//                "111 Tech Hub, Kochi, Kerala, India",
//                OfferStatus.SENT,
//                "admin@trivioglobal.com"
//            ),
//
//            createOffer(
//                "Neha Joshi",
//                "neha.joshi@example.com",
//                "Business Analyst",
//                LocalDate.now().plusDays(35),
//                "Pune, India",
//                "3 months",
//                "222 Analytics Tower, Pune, Maharashtra, India",
//                OfferStatus.ACCEPTED,
//                "admin@trivioglobal.com"
//            ),
//
//            createOffer(
//                "Karan Malhotra",
//                "karan.malhotra@example.com",
//                "Senior Software Engineer",
//                LocalDate.now().plusDays(50),
//                "Gurgaon, India",
//                "6 months",
//                "333 Corporate Hub, Gurgaon, Haryana, India",
//                OfferStatus.DRAFT,
//                "admin@trivioglobal.com"
//            ),
//
//            createOffer(
//                "Riya Agarwal",
//                "riya.agarwal@example.com",
//                "Data Scientist",
//                LocalDate.now().plusDays(40),
//                "Hyderabad, India",
//                "6 months",
//                "444 Data Center, Hyderabad, Telangana, India",
//                OfferStatus.SENT,
//                "admin@trivioglobal.com"
//            ),
//
//            // International candidates
//            createOffer(
//                "John Anderson",
//                "john.anderson@example.com",
//                "Software Development Engineer II",
//                LocalDate.now().plusDays(60),
//                "Remote - Global",
//                "6 months",
//                "123 Main Street, San Francisco, CA, USA",
//                OfferStatus.SENT,
//                "admin@trivioglobal.com"
//            ),
//
//            createOffer(
//                "Emily Chen",
//                "emily.chen@example.com",
//                "Frontend Developer",
//                LocalDate.now().plusDays(55),
//                "Remote - Global",
//                "3 months",
//                "456 Queen Street, Toronto, ON, Canada",
//                OfferStatus.ACCEPTED,
//                "admin@trivioglobal.com"
//            ),
//
//            // More recent offers
//            createOffer(
//                "Siddharth Iyer",
//                "siddharth.iyer@example.com",
//                "Machine Learning Engineer",
//                LocalDate.now().plusDays(21),
//                "Bangalore, India",
//                "6 months",
//                "555 AI Campus, Bangalore, Karnataka, India",
//                OfferStatus.DRAFT,
//                "admin@trivioglobal.com"
//            ),
//
//            createOffer(
//                "Kavya Nair",
//                "kavya.nair@example.com",
//                "Quality Assurance Engineer",
//                LocalDate.now().plusDays(28),
//                "Thiruvananthapuram, India",
//                "6 months",
//                "666 Quality Plaza, Thiruvananthapuram, Kerala, India",
//                OfferStatus.SENT,
//                "admin@trivioglobal.com"
//            ),
//
//            createOffer(
//                "Manish Tiwari",
//                "manish.tiwari@example.com",
//                "Database Administrator",
//                LocalDate.now().plusDays(18),
//                "Lucknow, India",
//                "3 months",
//                "777 Database Center, Lucknow, Uttar Pradesh, India",
//                OfferStatus.REJECTED,
//                "admin@trivioglobal.com"
//            )
//        );
//
//        offerRepository.saveAll(dummyOffers);
//    }
//
//    private Offer createOffer(String candidateName, String candidateEmail, String role,
//                             LocalDate joiningDate, String location, String trialPeriod,
//                             String address, OfferStatus status, String createdBy) {
//        Offer offer = new Offer(candidateName, candidateEmail, role, joiningDate,
//                               location, trialPeriod, address, createdBy);
//
//        // Set status and related fields based on status
//        offer.setStatus(status);
//
//        if (status == OfferStatus.SENT || status == OfferStatus.ACCEPTED ||
//            status == OfferStatus.REJECTED || status == OfferStatus.EXPIRED) {
//            // These offers have been sent
//            offer.setEmailSent(true);
//            offer.setEmailSubject("Job Offer - " + role + " Position at Trivio Global");
//            offer.setPdfFileName("offer_letter_" + candidateName.replaceAll("\\s+", "_") + "_" + System.currentTimeMillis() + ".pdf");
//            offer.setPdfFilePath("offer_letters/offer_letter_" + candidateName.replaceAll("\\s+", "_") + "_" + System.currentTimeMillis() + ".pdf");
//        }
//
//        return offer;
//    }
//}