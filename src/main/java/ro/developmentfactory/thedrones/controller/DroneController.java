package ro.developmentfactory.thedrones.controller;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import ro.developmentfactory.thedrones.controller.dto.DroneRequest;
import ro.developmentfactory.thedrones.controller.dto.DroneResponse;
import ro.developmentfactory.thedrones.service.DroneService;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/drones")
public class DroneController {

    private final DroneService droneService;

    public DroneController(DroneService droneService) {
        this.droneService = droneService;
    }

    //Save operation
    @PostMapping
    public DroneResponse saveDrone(@Valid @RequestBody DroneRequest droneRequest){
        return droneService.saveDrone(droneRequest);
    }

    @GetMapping("/drones")
    public List<DroneResponse> fetchDronesList() {
        return droneService.fetchDroneList().stream()
                .map(drone -> DroneResponse.builder()
                .idDrone(drone.getIdDrone())
                .name(drone.getName())
                .countMove(drone.getCountMove())
                .createdAt(drone.getCreatedAt())
                .updatedAt(drone.getUpdatedAt())
                .build())
        .collect(Collectors.toList());
    }

    //Update operation
    @PutMapping("/drone/{id}")
    public DroneResponse updateDrone(@RequestBody DroneRequest droneRequest,@PathVariable ("id") UUID idDrones){
        return droneService.updateNameDrone(droneRequest,idDrones);
    }

    @DeleteMapping("/drones/{id}")
    public String deleteDroneById(@PathVariable ("id") UUID idDrone){
        droneService.deleteDrone(idDrone);
        return "Drone deleted";
    }
}
