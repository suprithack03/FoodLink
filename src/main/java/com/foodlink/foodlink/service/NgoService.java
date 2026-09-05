package com.foodlink.foodlink.service;

import com.foodlink.foodlink.entity.Ngo;
import com.foodlink.foodlink.repository.NgoRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NgoService {

    private final NgoRepository ngoRepository;

    public NgoService(NgoRepository ngoRepository) {
        this.ngoRepository = ngoRepository;
    }

    public List<Ngo> getAllNgos() {
        return ngoRepository.findAll();
    }

    public Ngo getNgoById(Long id) {
        return ngoRepository.findById(id).orElse(null);
    }

    public Ngo createNgo(Ngo ngo) {
        return ngoRepository.save(ngo);
    }

    public Ngo updateNgo(Ngo ngo) {
        return ngoRepository.save(ngo);
    }

    public void deleteNgo(Long id) {
        ngoRepository.deleteById(id);
    }
}
