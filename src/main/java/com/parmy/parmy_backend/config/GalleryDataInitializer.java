package com.parmy.parmy_backend.config;

import com.parmy.parmy_backend.model.GalleryItem;
import com.parmy.parmy_backend.repository.GalleryItemRepository;
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
@Order(4) // Run after ProjectDataInitializer
public class GalleryDataInitializer implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(GalleryDataInitializer.class);

    @Autowired
    private GalleryItemRepository galleryRepository;

    @Override
    public void run(String... args) throws Exception {
        if (galleryRepository.count() == 0) {
            seedGallery();
        } else {
            logger.info("Gallery database already populated, skipping gallery seeding.");
        }
    }

    private void seedGallery() {
        logger.info("Seeding default gallery items into MongoDB...");

        List<GalleryItem> defaultItems = Arrays.asList(
            createItem(
                "https://images.unsplash.com/photo-1497366216548-37526070297c?auto=format&fit=crop&w=1400&q=90",
                "Our Modern Headquarters",
                "Office Life",
                "A space designed for collaboration, creativity, and deep focus."
            ),
            createItem(
                "https://images.unsplash.com/photo-1504384308090-c894fdcc538d?auto=format&fit=crop&w=1400&q=90",
                "Open Collaboration Zone",
                "Office Life",
                "Teams brainstorming in our open-plan creative hub."
            ),
            createItem(
                "https://images.unsplash.com/photo-1497366754035-f200968a6e72?auto=format&fit=crop&w=1400&q=90",
                "Executive Meeting Suite",
                "Office Life",
                "Where strategy is forged and decisions are made."
            ),
            createItem(
                "https://images.unsplash.com/photo-1572021335469-31706a17aaef?auto=format&fit=crop&w=1400&q=90",
                "Tech-Enabled Workstations",
                "Office Life",
                "Every desk equipped with the best tools money can buy."
            ),
            createItem(
                "https://images.unsplash.com/photo-1529543544282-ea669407fca3?auto=format&fit=crop&w=1400&q=90",
                "Annual Company Retreat",
                "Team Events",
                "Bonding, learning, and celebrating milestones together."
            ),
            createItem(
                "https://images.unsplash.com/photo-1511632765486-a01980e01a18?auto=format&fit=crop&w=1400&q=90",
                "Team Outing 2024",
                "Team Events",
                "Outside the office, the team bonds even stronger."
            ),
            createItem(
                "https://images.unsplash.com/photo-1542744173-8e7e53415bb0?auto=format&fit=crop&w=1400&q=90",
                "Product Launch Celebration",
                "Team Events",
                "Celebrating every milestone with the people who made it happen."
            ),
            createItem(
                "https://images.unsplash.com/photo-1600880292203-757bb62b4baf?auto=format&fit=crop&w=1400&q=90",
                "Client Strategy Workshop",
                "Client Work",
                "Deep-diving into client goals and mapping the road to success."
            ),
            createItem(
                "https://images.unsplash.com/photo-1557804506-669a67965ba0?auto=format&fit=crop&w=1400&q=90",
                "Project Delivery Day",
                "Client Work",
                "Handing over a successful project with pride and precision."
            ),
            createItem(
                "https://images.unsplash.com/photo-1553877522-43269d4ea984?auto=format&fit=crop&w=1400&q=90",
                "Requirement Gathering Session",
                "Client Work",
                "Every great product starts with truly understanding the client."
            ),
            createItem(
                "https://images.unsplash.com/photo-1540575467063-178a50c2df87?auto=format&fit=crop&w=1400&q=90",
                "Corporate Training Summit",
                "Training",
                "Bringing together 200+ professionals for a full-day skill intensive."
            ),
            createItem(
                "https://images.unsplash.com/photo-1552664730-d307ca884978?auto=format&fit=crop&w=1400&q=90",
                "Leadership Workshop",
                "Training",
                "Building the next generation of confident, empathetic leaders."
            ),
            createItem(
                "https://images.unsplash.com/photo-1531482615713-2afd69097998?auto=format&fit=crop&w=1400&q=90",
                "Intern Orientation Day",
                "Training",
                "Welcoming the brightest minds and setting them up for success."
            ),
            createItem(
                "https://images.unsplash.com/photo-1560179707-f14e90ef3623?auto=format&fit=crop&w=1400&q=90",
                "Our Campus",
                "Corporate",
                "A state-of-the-art facility that inspires innovation every day."
            ),
            createItem(
                "https://images.unsplash.com/photo-1486406146926-c627a92ad1ab?auto=format&fit=crop&w=1400&q=90",
                "Corporate Tower",
                "Corporate",
                "Standing tall as a symbol of growth, stability, and ambition."
            ),
            createItem(
                "https://images.unsplash.com/photo-1521791136064-7986c2920216?auto=format&fit=crop&w=1400&q=90",
                "Award Ceremony 2024",
                "Corporate",
                "Recognising excellence and honoring dedication across the organisation."
            )
        );

        galleryRepository.saveAll(defaultItems);
        logger.info("Successfully seeded 16 gallery items into MongoDB.");
    }

    private GalleryItem createItem(String url, String title, String category, String description) {
        GalleryItem item = new GalleryItem();
        item.setSrc(url);
        item.setThumb(url);
        item.setTitle(title);
        item.setCategory(category);
        item.setDescription(description);
        item.setCreatedAt(LocalDateTime.now());
        return item;
    }
}
