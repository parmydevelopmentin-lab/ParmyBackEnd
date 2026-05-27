package com.parmy.parmy_backend.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.parmy.parmy_backend.dto.PurchaseDtos;
import com.parmy.parmy_backend.model.Project;
import com.parmy.parmy_backend.model.Purchase;
import com.parmy.parmy_backend.model.PurchaseStatus;
import com.parmy.parmy_backend.model.User;
import com.parmy.parmy_backend.repository.ProjectRepository;
import com.parmy.parmy_backend.repository.PurchaseRepository;
import com.parmy.parmy_backend.repository.UserRepository;

@Service
public class PurchaseService {
    private static final Logger logger = LoggerFactory.getLogger(PurchaseService.class);

    @Autowired
    private PurchaseRepository purchaseRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EmailService emailService;

    @Value("${purchase.proof.storage-dir:purchases}")
    private String storageDir;

    @Value("${admin.notification.email:parmydevelopment.in@gmail.com}")
    private String adminNotificationEmail;

    public PurchaseDtos.PurchaseResponse createPurchase(String userId, String buyerEmail,
            PurchaseDtos.CreatePurchaseRequest req, MultipartFile proofFile) {
        try {
            // Get project
            Project project = projectRepository.findById(req.projectId)
                    .orElseThrow(() -> new RuntimeException("Project not found"));

            // Save file
            String storedPath = storeFile(proofFile);

            // Create purchase
            Purchase p = new Purchase();
            p.setUserId(userId);
            p.setBuyerEmail(buyerEmail);
            p.setProjectId(project.getId());
            p.setProjectTitle(project.getTitle());
            p.setAmount(project.getPrice());
            p.setCurrency(project.getCurrency());
            p.setProofFilePath(storedPath);
            p.setNotes(req.notes);

            Purchase saved = purchaseRepository.save(p);

            // Notify admin and buyer (best-effort)
            try {
                emailService.sendPurchaseSubmittedNotification(adminNotificationEmail, buyerEmail, project.getTitle(),
                        project.getPrice(), project.getCurrency());
                emailService.sendPurchaseConfirmationToBuyer(buyerEmail, project.getTitle());
            } catch (Exception ex) {
                logger.warn("Failed to send purchase emails: {}", ex.getMessage());
            }

            return toResponse(saved);

        } catch (IOException e) {
            throw new RuntimeException("Failed to store proof file", e);
        }
    }

    public List<PurchaseDtos.PurchaseResponse> listMy(String userId) {
        return purchaseRepository.findByUserIdOrderByCreatedAtDesc(userId)
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    public Optional<PurchaseDtos.PurchaseResponse> getByIdForUser(String id, String userId) {
        return purchaseRepository.findById(id)
                .filter(p -> p.getUserId().equals(userId))
                .map(this::toResponse);
    }

    public List<PurchaseDtos.PurchaseResponse> listAllAdmin() {
        return purchaseRepository.findAll().stream()
                .sorted((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt()))
                .map(this::toResponse).collect(Collectors.toList());
    }

    public Optional<PurchaseDtos.PurchaseResponse> updateStatus(String id, PurchaseStatus status) {
        return purchaseRepository.findById(id).map(p -> {
            p.setStatus(status);
            p.setUpdatedAt(LocalDateTime.now());
            return toResponse(purchaseRepository.save(p));
        });
    }

    private String storeFile(MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new RuntimeException("Payment proof file is required");
        }
        Path dir = Paths.get(storageDir);
        if (!Files.exists(dir)) {
            Files.createDirectories(dir);
        }
        String ext = Optional.ofNullable(file.getOriginalFilename())
                .filter(name -> name.contains("."))
                .map(name -> name.substring(name.lastIndexOf('.')))
                .orElse("");
        String filename = "proof_" + UUID.randomUUID() + ext;
        Path dest = dir.resolve(filename);
        Files.copy(file.getInputStream(), dest);
        return dest.toString().replace('\\', '/');
    }

    private PurchaseDtos.PurchaseResponse toResponse(Purchase p) {
        PurchaseDtos.PurchaseResponse res = new PurchaseDtos.PurchaseResponse();
        res.id = p.getId();
        res.userId = p.getUserId();
        res.userEmail = p.getBuyerEmail();
        
        // Get username from user repository
        try {
            User user = userRepository.findById(p.getUserId()).orElse(null);
            res.username = user != null ? user.getUsername() : "Unknown User";
        } catch (Exception e) {
            res.username = "Unknown User";
        }
        
        res.projectId = p.getProjectId();
        res.projectTitle = p.getProjectTitle();
        res.projectPrice = p.getAmount();
        res.projectCurrency = p.getCurrency();
        res.status = p.getStatus();
        res.proofFilePath = p.getProofFilePath();
        
        // Extract filename from path and construct URL
        if (p.getProofFilePath() != null) {
            String path = p.getProofFilePath().replace('\\', '/');
            String filename = path.substring(path.lastIndexOf('/') + 1);
            res.proofFileName = filename;
            res.proofUrl = "/purchases/" + filename;
        }
        
        res.notes = p.getNotes();
        res.createdAt = p.getCreatedAt();
        res.updatedAt = p.getUpdatedAt();
        return res;
    }
}
