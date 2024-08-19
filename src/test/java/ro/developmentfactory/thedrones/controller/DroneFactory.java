package ro.developmentfactory.thedrones.controller;

import org.springframework.stereotype.Component;
import ro.developmentfactory.thedrones.repository.DroneRepository;
import ro.developmentfactory.thedrones.repository.DroneStatusRepository;
import ro.developmentfactory.thedrones.repository.entity.Drone;
import ro.developmentfactory.thedrones.repository.entity.DroneStatus;
import ro.developmentfactory.thedrones.repository.entity.Direction;

import java.time.OffsetDateTime;
import java.util.UUID;


@Component
public class DroneFactory {

    private final DroneRepository droneRepository;
    private final DroneStatusRepository droneStatusRepository;

    public DroneFactory(DroneRepository droneRepository, DroneStatusRepository droneStatusRepository) {
        this.droneRepository = droneRepository;
        this.droneStatusRepository = droneStatusRepository;
    }

    public UUID createDroneAtEdge() {
        Drone drone = new Drone();
        drone.setIdDrone(UUID.randomUUID());
        drone.setName("TestDrone");
        drone.setCountMove(0);
        drone.setCreatedAt(OffsetDateTime.now());
        drone.setUpdatedAt(OffsetDateTime.now());
        drone = droneRepository.save(drone);

        DroneStatus droneStatus = new DroneStatus();
        droneStatus.setDrone(drone);
        droneStatus.setCurrentPositionX(9);
        droneStatus.setCurrentPositionY(9);
        droneStatus.setFacingDirection(Direction.E);

        droneStatusRepository.save(droneStatus);

        return drone.getIdDrone();
    }
}
