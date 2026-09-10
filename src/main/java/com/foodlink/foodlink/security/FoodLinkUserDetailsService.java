package com.foodlink.foodlink.security;

import com.foodlink.foodlink.entity.Admin;
import com.foodlink.foodlink.entity.Donor;
import com.foodlink.foodlink.entity.Ngo;
import com.foodlink.foodlink.repository.AdminRepository;
import com.foodlink.foodlink.repository.DonorRepository;
import com.foodlink.foodlink.repository.NgoRepository;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class FoodLinkUserDetailsService implements UserDetailsService {

    private final DonorRepository donorRepository;
    private final NgoRepository ngoRepository;
    private final AdminRepository adminRepository;

    public FoodLinkUserDetailsService(
            DonorRepository donorRepository,
            NgoRepository ngoRepository,
            AdminRepository adminRepository) {

        this.donorRepository = donorRepository;
        this.ngoRepository = ngoRepository;
        this.adminRepository = adminRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email)
            throws UsernameNotFoundException {

        Donor donor = donorRepository.findByEmail(email).orElse(null);

        if (donor != null) {
            return User.withUsername(donor.getEmail())
                    .password(donor.getPassword())
                    .roles("DONOR")
                    .build();
        }

        Ngo ngo = ngoRepository.findByEmail(email).orElse(null);

        if (ngo != null) {
            return User.withUsername(ngo.getEmail())
                    .password(ngo.getPassword())
                    .roles("NGO")
                    .build();
        }

        Admin admin = adminRepository.findByEmail(email).orElse(null);

        if (admin != null) {
            return User.withUsername(admin.getEmail())
                    .password(admin.getPassword())
                    .roles("ADMIN")
                    .build();
        }

        throw new UsernameNotFoundException(
                "User not found with email: " + email);
    }
}

