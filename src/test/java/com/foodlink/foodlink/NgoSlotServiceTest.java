package com.foodlink.foodlink;

import com.foodlink.foodlink.entity.Ngo;
import com.foodlink.foodlink.service.NgoSlot;
import com.foodlink.foodlink.service.NgoSlotService;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class NgoSlotServiceTest {

    @Test
    void shouldExpandNgoCapacityIntoSlots() {

        Ngo ngo = new Ngo();

        ngo.setId(10L);
        ngo.setLatitude(12.9716);
        ngo.setLongitude(77.5946);
        ngo.setCapacity(3);

        NgoSlotService service =
                new NgoSlotService();

        List<NgoSlot> slots =
                service.expandCapacity(
                        List.of(ngo)
                );

        assertEquals(3, slots.size());

        assertEquals(10L, slots.get(0).getNgoId());
        assertEquals(10L, slots.get(1).getNgoId());
        assertEquals(10L, slots.get(2).getNgoId());

        assertEquals(
                12.9716,
                slots.get(0).getLatitude()
        );

        assertEquals(
                77.5946,
                slots.get(0).getLongitude()
        );
    }
}
