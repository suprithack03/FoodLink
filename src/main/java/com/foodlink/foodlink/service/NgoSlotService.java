package com.foodlink.foodlink.service;

import com.foodlink.foodlink.entity.Ngo;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class NgoSlotService {

    public List<NgoSlot> expandCapacity(List<Ngo> ngos) {

        List<NgoSlot> slots =
                new ArrayList<>();

        for (Ngo ngo : ngos) {

            if (ngo.getCapacity() == null
                    || ngo.getCapacity() <= 0) {
                continue;
            }

            if (ngo.getLatitude() == null
                    || ngo.getLongitude() == null) {
                continue;
            }

            int capacity =
                    ngo.getCapacity();

            for (int i = 0; i < capacity; i++) {

                slots.add(
                        new NgoSlot(
                                ngo.getId(),
                                ngo.getLatitude(),
                                ngo.getLongitude()
                        )
                );
            }
        }

        return slots;
    }
}

