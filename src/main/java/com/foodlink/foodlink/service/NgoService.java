package com.foodlink.foodlink.service;

import com.foodlink.foodlink.entity.Ngo;
import com.foodlink.foodlink.repository.NgoRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NgoService {

    private final NgoRepository ngoRepository;
    private final PasswordEncoder passwordEncoder;

    public NgoService(
            NgoRepository ngoRepository,
            PasswordEncoder passwordEncoder) {

        this.ngoRepository = ngoRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public List<Ngo> getAllNgos() {
        return ngoRepository.findAll();
    }

    public Ngo getNgoById(Long id) {
        return ngoRepository.findById(id).orElse(null);
    }

    public Ngo createNgo(Ngo ngo) {

        /*
         * Every newly registered NGO must start
         * as unverified.
         */
        ngo.setVerified(false);

        String hashedPassword =
                passwordEncoder.encode(ngo.getPassword());

        ngo.setPassword(hashedPassword);

        return ngoRepository.save(ngo);
    }

    public Ngo updateNgo(Ngo ngo) {
        return ngoRepository.save(ngo);
    }

    public void deleteNgo(Long id) {
        ngoRepository.deleteById(id);
    }

    /*
     * Verify or unverify an NGO.
     *
     * This method will be called only by the
     * admin verification endpoint.
     */
    public Ngo setNgoVerificationStatus(
            Long id,
            boolean verified) {

        Ngo ngo = ngoRepository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "NGO not found with id: " + id));

        ngo.setVerified(verified);

        return ngoRepository.save(ngo);
    }
}