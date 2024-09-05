package transportation.provider.controller;

import transportation.provider.model.ContainerGood;
import transportation.provider.service.ContainerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ContainerControllerTest {

    @Autowired
    ContainerService containerService;

    @BeforeEach
    void reset() {
        ContainerGood containerGood = containerService.unloadGood();
        while (containerGood !=null){
            containerGood = containerService.unloadGood();
        }
    }
    @Test
    void should_load_goods() {
        assertTrue(containerService.loadGood(new ContainerGood(1,2, 100.0)));
        assertTrue(containerService.loadGood(new ContainerGood(2,5, 200.0)));
        assertTrue(containerService.loadGood(new ContainerGood(3,3, 150.0)));
    }

    @Test
    void should_not_load_goods() {
        assertTrue(containerService.loadGood(new ContainerGood(1,2, 100.0)));
        assertTrue(containerService.loadGood(new ContainerGood(2,5, 200.0)));
        assertTrue(containerService.loadGood(new ContainerGood(3,3, 150.0)));
        assertFalse(containerService.loadGood(new ContainerGood(4,3, 130.0)));
    }

    @Test
    void should_unload_goods() {
        assertTrue(containerService.loadGood(new ContainerGood(1,2, 100.0)));
        assertTrue(containerService.loadGood(new ContainerGood(2,5, 200.0)));
        assertTrue(containerService.loadGood(new ContainerGood(3,1, 150.0)));
        assertEquals(3,containerService.unloadGood().id());
    }

    @Test
    void should_get_TotalWeight() {
        assertTrue(containerService.loadGood(new ContainerGood(1,2, 100.0)));
        assertTrue(containerService.loadGood(new ContainerGood(2,5, 200.0)));
        assertTrue(containerService.loadGood(new ContainerGood(3,1, 150.0)));
        assertEquals(8, containerService.getTotalWeight());

    }

    @Test
    void should_get_HighestPricedGood() {
        assertTrue(containerService.loadGood(new ContainerGood(1,2, 100.0)));
        assertTrue(containerService.loadGood(new ContainerGood(2,5, 200.0)));
        assertTrue(containerService.loadGood(new ContainerGood(3,1, 150.0)));
        assertEquals(200, containerService.getHighestPricedGood().price());
        assertEquals(2, containerService.getHighestPricedGood().id());
    }

    @Test
    void should_get_updated_HighestPricedGood_after_unload() {
        assertTrue(containerService.loadGood(new ContainerGood(1,2, 100.0)));
        assertTrue(containerService.loadGood(new ContainerGood(2,5, 200.0)));
        assertTrue(containerService.loadGood(new ContainerGood(3,1, 150.0)));
        containerService.unloadGood();
        containerService.unloadGood();
        assertEquals(100, containerService.getHighestPricedGood().price());
        assertEquals(1, containerService.getHighestPricedGood().id());
    }

    @Test
    void should_get_updated_HighestPricedGood_after_unload_and_loading() {
        assertTrue(containerService.loadGood(new ContainerGood(1,2, 100.0)));
        assertTrue(containerService.loadGood(new ContainerGood(2,5, 200.0)));
        assertTrue(containerService.loadGood(new ContainerGood(3,1, 150.0)));
        containerService.unloadGood();
        containerService.unloadGood();
        assertTrue(containerService.loadGood(new ContainerGood(4,1, 210.0)));
        assertEquals(210, containerService.getHighestPricedGood().price());
        assertEquals(4, containerService.getHighestPricedGood().id());
        assertTrue(containerService.loadGood(new ContainerGood(5,2, 200.0)));
        assertEquals(210, containerService.getHighestPricedGood().price());
        assertEquals(4, containerService.getHighestPricedGood().id());
    }

    @Test
    void should_get_updated_HighestPricedGood_to_null_after_unloading_all() {
        assertTrue(containerService.loadGood(new ContainerGood(1,2, 100.0)));
        assertTrue(containerService.loadGood(new ContainerGood(2,5, 200.0)));
        assertTrue(containerService.loadGood(new ContainerGood(3,1, 150.0)));
        containerService.unloadGood();
        containerService.unloadGood();
        containerService.unloadGood();
        assertNull(containerService.getHighestPricedGood());
    }
    @Test
    void should_get_updated_TotalWeight_after_unload() {
        assertTrue(containerService.loadGood(new ContainerGood(1,2, 100.0)));
        assertTrue(containerService.loadGood(new ContainerGood(2,5, 200.0)));
        assertTrue(containerService.loadGood(new ContainerGood(3,1, 150.0)));
        containerService.unloadGood();
        assertEquals(7, containerService.getTotalWeight());
    }

    @Test
    void should_get_updated_TotalWeight_to_zero_after_unloading_all() {
        assertTrue(containerService.loadGood(new ContainerGood(1,2, 100.0)));
        assertTrue(containerService.loadGood(new ContainerGood(2,5, 200.0)));
        assertTrue(containerService.loadGood(new ContainerGood(3,1, 150.0)));
        containerService.unloadGood();
        containerService.unloadGood();
        containerService.unloadGood();
        assertEquals(0, containerService.getTotalWeight());
    }

    @Test
    void should_get_updated_TotalWeight_after_unloading_and_loading() {
        assertTrue(containerService.loadGood(new ContainerGood(1,2, 100.0)));
        assertTrue(containerService.loadGood(new ContainerGood(2,5, 200.0)));
        assertTrue(containerService.loadGood(new ContainerGood(3,1, 150.0)));
        containerService.unloadGood();
        containerService.unloadGood();
        assertTrue(containerService.loadGood(new ContainerGood(4,1, 200.0)));
        assertTrue(containerService.loadGood(new ContainerGood(5,2, 150.0)));

        assertEquals(5, containerService.getTotalWeight());
    }
}