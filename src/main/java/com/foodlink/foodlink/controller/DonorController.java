package com.foodlink.foodlink.controller;

import com.foodlink.foodlink.entity.Donor;
import com.foodlink.foodlink.service.DonorService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/donors")
public class DonorController {

    private final DonorService donorService;

    public DonorController(DonorService donorService) {
        this.donorService = donorService;
    }

    @GetMapping
    public List<Donor> getAllDonors() {
        return donorService.getAllDonors();
    }

    @GetMapping("/{id}")
    public Donor getDonorById(@PathVariable Long id) {
        return donorService.getDonorById(id);
    }

    @PostMapping
    public Donor createDonor(@RequestBody Donor donor) {
        return donorService.createDonor(donor);
    }

    @PutMapping("/{id}")
    public Donor updateDonor(
            @PathVariable Long id,
            @RequestBody Donor updatedDonor) {

        Donor existingDonor =
                donorService.getDonorById(id);

        if (existingDonor == null) {
            return null;
        }

        existingDonor.setName(updatedDonor.getName());
        existingDonor.setEmail(updatedDonor.getEmail());
        existingDonor.setPhone(updatedDonor.getPhone());
        existingDonor.setLocation(updatedDonor.getLocation());
        existingDonor.setPassword(updatedDonor.getPassword());

        return donorService.updateDonor(existingDonor);
    }

    @DeleteMapping("/{id}")
    public void deleteDonor(@PathVariable Long id) {
        donorService.deleteDonor(id);
    }
}