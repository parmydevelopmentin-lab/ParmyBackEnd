package com.parmy.parmy_backend.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.parmy.parmy_backend.dto.ApiResponse;
import com.parmy.parmy_backend.dto.PurchaseDtos;
import com.parmy.parmy_backend.model.User;
import com.parmy.parmy_backend.service.AuthService;
import com.parmy.parmy_backend.service.PurchaseService;
import com.parmy.parmy_backend.util.JwtUtil;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/purchases")
@CrossOrigin(origins = { "https://parmytechnologies.netlify.app", "http://localhost:5173",
        "http://localhost:3000" }, allowCredentials = "true")
public class PurchaseController {

    @Autowired
    private PurchaseService purchaseService;

    @Autowired
    private AuthService authService;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping(consumes = { "multipart/form-data" })
    public ResponseEntity<ApiResponse<PurchaseDtos.PurchaseResponse>> create(
            @RequestPart("data") PurchaseDtos.CreatePurchaseRequest data,
            @RequestPart("file") MultipartFile file,
            HttpServletRequest request) {
        User user = getCurrentUser(request);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ApiResponse.error("Unauthorized"));
        }
        var res = purchaseService.createPurchase(user.getId(), user.getEmail(), data, file);
        return ResponseEntity.ok(ApiResponse.success("Purchase submitted", res));
    }

    @GetMapping("/my")
    public ResponseEntity<ApiResponse<List<PurchaseDtos.PurchaseResponse>>> myPurchases(HttpServletRequest request) {
        User user = getCurrentUser(request);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ApiResponse.error("Unauthorized"));
        }
        var list = purchaseService.listMy(user.getId());
        return ResponseEntity.ok(ApiResponse.success("OK", list));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<PurchaseDtos.PurchaseResponse>> getById(@PathVariable String id,
            HttpServletRequest request) {
        User user = getCurrentUser(request);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ApiResponse.error("Unauthorized"));
        }
        Optional<PurchaseDtos.PurchaseResponse> res = purchaseService.getByIdForUser(id, user.getId());
        return res.map(r -> ResponseEntity.ok(ApiResponse.success("OK", r)))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.error("Not found")));
    }

    private User getCurrentUser(HttpServletRequest request) {
        try {
            String authHeader = request.getHeader("Authorization");
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                String token = authHeader.substring(7);
                String email = jwtUtil.extractEmail(token);
                if (email != null && jwtUtil.validateToken(token)) {
                    return authService.getUserByEmail(email);
                }
            }
        } catch (Exception ignored) {
        }
        return null;
    }
}
