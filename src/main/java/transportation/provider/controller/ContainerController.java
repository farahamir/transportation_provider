package transportation.provider.controller;

import transportation.provider.config.Constants;
import transportation.provider.model.ContainerGood;
import transportation.provider.service.ContainerService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static transportation.provider.config.Constants.GOOD_LOADED_SUCCESSFULLY;

@RestController
@RequestMapping("/api/container")
public class ContainerController {

    private final ContainerService containerService;

    public ContainerController(ContainerService containerService) {
        this.containerService = containerService;
    }

    @PostMapping("/load")
    public ResponseEntity<String> loadGood(@RequestBody ContainerGood containerGood) {
        if (containerService.loadGood(containerGood)) {
            return ResponseEntity.ok(GOOD_LOADED_SUCCESSFULLY);
        } else {
            return ResponseEntity.badRequest().body(Constants.CANNOT_LOAD_GOOD_EXCEEDS_MAXIMUM_GROSS_LOAD);
        }
    }

    @PostMapping("/unload")
    public ResponseEntity<ContainerGood> unloadGood() {
        ContainerGood unloadedContainerGood = containerService.unloadGood();
        if (unloadedContainerGood != null) {
            return ResponseEntity.ok(unloadedContainerGood);
        } else {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @GetMapping("/total-weight")
    public ResponseEntity<Integer> getTotalWeight() {
        return ResponseEntity.ok(containerService.getTotalWeight());
    }

    @GetMapping("/highest-priced-good")
    public ResponseEntity<ContainerGood> getHighestPricedGood() {
        ContainerGood highestPricedContainerGood = containerService.getHighestPricedGood();
        if (highestPricedContainerGood != null) {
            return ResponseEntity.ok(highestPricedContainerGood);
        } else {
            return ResponseEntity.badRequest().body(null);
        }
    }
}
