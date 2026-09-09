package com.foodlink.foodlink;

import com.foodlink.foodlink.service.NgoSlot;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class NgoSlotTest {

    @Test
    void shouldCreateNgoSlot() {

        NgoSlot slot =
                new NgoSlot(
                        10L,
                        12.9716,
                        77.5946
                );

        assertEquals(10L, slot.getNgoId());
        assertEquals(12.9716, slot.getLatitude());
        assertEquals(77.5946, slot.getLongitude());
    }
}
