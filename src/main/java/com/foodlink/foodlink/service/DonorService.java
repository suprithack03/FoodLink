package com.foodlink.foodlink.service;

import com.foodlink.foodlink.entity.Donor;
import com.foodlink.foodlink.repository.DonorRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DonorService {

    private final DonorRepository donorRepository;
    private final PasswordEncoder passwordEncoder;

    public DonorService(
            DonorRepository donorRepository,
            PasswordEncoder passwordEncoder) {

        this.donorRepository = donorRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public List<Donor> getAllDonors() {
        return donorRepository.findAll();
    }

    public Donor getDonorById(Long id) {
        return donorRepository.findById(id).orElse(null);
    }

    public Donor createDonor(Donor donor) {

        String hashedPassword =
                passwordEncoder.encode(donor.getPassword());

        donor.setPassword(hashedPassword);

        return donorRepository.save(donor);
    }

    public Donor updateDonor(Donor donor) {
        return donorRepository.save(donor);
    }

    public void deleteDonor(Long id) {
        donorRepository.deleteById(id);
    }
}