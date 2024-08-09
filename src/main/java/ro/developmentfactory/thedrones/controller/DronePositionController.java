package ro.developmentfactory.thedrones.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ro.developmentfactory.thedrones.controller.dto.DroneStatusResponse;
import ro.developmentfactory.thedrones.controller.dto.TurnDirection;
import ro.developmentfactory.thedrones.service.DronePositionService;

import java.util.UUID;

@RestController
@RequestMapping("/drones/{id}")
public class DronePositionController {

    private final DronePositionService dronePositionService;
    private static final Logger log = LoggerFactory.getLogger(DronePositionController.class);

    public DronePositionController(DronePositionService dronePositionService) {
        this.dronePositionService = dronePositionService;
    }

    @PostMapping("/turn/{direction}")
    public ResponseEntity<DroneStatusResponse> turn(@PathVariable ("id") UUID idDrone , @PathVariable("direction") TurnDirection turnDirection) {
        log.debug("Received request to turn {} for drone with id: {}", turnDirection, idDrone);

        DroneStatusResponse response = dronePositionService.turn(idDrone, turnDirection);
        log.debug("Response from turn {}: {}",turnDirection, response);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/goForward")
    public ResponseEntity<DroneStatusResponse> moveForward(@PathVariable ("id") UUID idDrone) {
        log.debug("Received request to move forward: {}", idDrone);
        DroneStatusResponse response = dronePositionService.moveForward(idDrone);
        log.debug("Response from move forward: {}", response);
        return ResponseEntity.ok(response);
    }
}
