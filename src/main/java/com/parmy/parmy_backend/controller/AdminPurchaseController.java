package com.parmy.parmy_backend.controller;

import com.parmy.parmy_backend.dto.ApiResponse;
import com.parmy.parmy_backend.dto.PurchaseDtos;
import com.parmy.parmy_backend.model.PurchaseStatus;
import com.parmy.parmy_backend.model.User;
import com.parmy.parmy_backend.service.AuthService;
import com.parmy.parmy_backend.service.PurchaseService;
import com.parmy.parmy_backend.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/admin/purchases")
@CrossOrigin(origins = { "https://parmytechnologies.netlify.app", "http://localhost:5173",
        "http://localhost:3000" }, allowCredentials = "true")
public class AdminPurchaseController {

    @Autowired
    private PurchaseService purchaseService;

    @Autowired
    private AuthService authService;

    @Autowired
    private JwtUtil jwtUtil;

    @GetMapping
    public ResponseEntity<ApiResponse<List<PurchaseDtos.PurchaseResponse>>> listAll(HttpServletRequest request) {
        User user = getCurrentUser(request);
        if (user == null || !user.isAdmin()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ApiResponse.error("Admin only"));
        }
        var list = purchaseService.listAllAdmin();
        return ResponseEntity.ok(ApiResponse.success("OK", list));
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<ApiResponse<PurchaseDtos.PurchaseResponse>> updateStatus(
            @PathVariable String id,
            @RequestParam PurchaseStatus status,
            HttpServletRequest request) {
        User user = getCurrentUser(request);
        if (user == null || !user.isAdmin()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ApiResponse.error("Admin only"));
        }
        Optional<PurchaseDtos.PurchaseResponse> res = purchaseService.updateStatus(id, status);
        return res.map(r -> ResponseEntity.ok(ApiResponse.success("Updated", r)))
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
