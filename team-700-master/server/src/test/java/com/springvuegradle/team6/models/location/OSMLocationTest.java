package com.springvuegradle.team6.models.location;

import org.junit.Ignore;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class OSMLocationTest {

    /*
    This test currently fails tests as it relies on an external api to pass. This class is not currently being used so
    I see no reason to update however if in future this class becomes important again a mocking api should be developed.

    @Test
    void updateLocationData() {
        OSMElementID id = new OSMElementID();
        assertTrue(id.findBySearch("Christchurch"));

        OSMLocation location = new OSMLocation(id);
        location.updateLocationData();

        assertEquals("Christchurch, Christchurch City, Canterbury, New Zealand", location.getName());
    }
     */
}