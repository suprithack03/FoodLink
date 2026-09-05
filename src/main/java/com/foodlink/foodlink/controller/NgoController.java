package com.foodlink.foodlink.controller;

import com.foodlink.foodlink.entity.Ngo;
import com.foodlink.foodlink.service.NgoService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ngos")
public class NgoController {

    private final NgoService ngoService;

    public NgoController(NgoService ngoService) {
        this.ngoService = ngoService;
    }

    @GetMapping
    public List<Ngo> getAllNgos() {
        return ngoService.getAllNgos();
    }

    @GetMapping("/{id}")
    public Ngo getNgoById(@PathVariable Long id) {
        return ngoService.getNgoById(id);
    }

    @PostMapping
    public Ngo createNgo(@RequestBody Ngo ngo) {
        return ngoService.createNgo(ngo);
    }

    @PutMapping("/{id}")
    public Ngo updateNgo(
            @PathVariable Long id,
            @RequestBody Ngo updatedNgo) {

        Ngo existingNgo =
                ngoService.getNgoById(id);

        if (existingNgo == null) {
            return null;
        }

        existingNgo.setName(updatedNgo.getName());
        existingNgo.setEmail(updatedNgo.getEmail());
        existingNgo.setPassword(updatedNgo.getPassword());
        existingNgo.setPhone(updatedNgo.getPhone());
        existingNgo.setLocation(updatedNgo.getLocation());
        existingNgo.setVerified(updatedNgo.isVerified());

        return ngoService.updateNgo(existingNgo);
    }

    @DeleteMapping("/{id}")
    public void deleteNgo(@PathVariable Long id) {
        ngoService.deleteNgo(id);
    }
}