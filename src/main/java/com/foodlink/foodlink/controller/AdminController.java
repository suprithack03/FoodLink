package com.foodlink.foodlink.controller;

import com.foodlink.foodlink.entity.Admin;
import com.foodlink.foodlink.entity.Ngo;
import com.foodlink.foodlink.service.AdminService;
import com.foodlink.foodlink.service.NgoService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admins")
public class AdminController {

    private final AdminService adminService;
    private final NgoService ngoService;

    public AdminController(
            AdminService adminService,
            NgoService ngoService) {

        this.adminService = adminService;
        this.ngoService = ngoService;
    }

    @PostMapping
    public Admin createAdmin(@RequestBody Admin admin) {
        return adminService.createAdmin(admin);
    }

    /*
     * Returns the currently authenticated administrator.
     *
     * This endpoint is used by the frontend to verify
     * admin login credentials.
     */
    @GetMapping("/me")
    public Admin getCurrentAdmin(
            Authentication authentication) {

        return adminService.getAdminByEmail(
                authentication.getName()
        );
    }

    /*
     * Admin verifies or unverifies an NGO.
     *
     * Example:
     * PUT /api/admins/ngos/1/verification?verified=true
     *
     * This endpoint is protected by SecurityConfig
     * and requires the ADMIN role.
     */
    @PutMapping("/ngos/{id}/verification")
    public Ngo setNgoVerificationStatus(
            @PathVariable Long id,
            @RequestParam boolean verified) {

        return ngoService.setNgoVerificationStatus(
                id,
                verified);
    }
}

