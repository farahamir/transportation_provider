package transportation.provider.controller;

import transportation.provider.model.ContainerGood;
import transportation.provider.service.ContainerService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
public class ControllerMultiThreadTests {

    @Autowired
    ContainerService containerService ;


    @Test
    public void runsMultipleTimes() throws InterruptedException {
        int numberOfThreads = 10;
        ExecutorService service = Executors.newFixedThreadPool(10);
        CountDownLatch latch = new CountDownLatch(numberOfThreads);
        for (int i = 0; i < numberOfThreads; i++) {
            service.submit(() -> {
                System.out.println("running multiple times with thread "+Thread.currentThread().getName());
                containerService.loadGood(new ContainerGood(1,2, 100.0));
                containerService.loadGood(new ContainerGood(2,5, 200.0));
                containerService.loadGood(new ContainerGood(3,3, 150.0));
                containerService.unloadGood();
                containerService.unloadGood();
                containerService.unloadGood();
                containerService.loadGood(new ContainerGood(1,2, 100.0));
                containerService.loadGood(new ContainerGood(2,5, 200.0));
                containerService.loadGood(new ContainerGood(3,3, 150.0));
                containerService.unloadGood();
                containerService.unloadGood();
                containerService.unloadGood();


                latch.countDown();
            });
        }
        latch.await();
        assertEquals(0, containerService.getTotalWeight());
    }
}