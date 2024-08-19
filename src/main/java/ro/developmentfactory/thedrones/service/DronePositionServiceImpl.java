package ro.developmentfactory.thedrones.service;

import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ro.developmentfactory.thedrones.controller.dto.DroneStatusResponse;
import ro.developmentfactory.thedrones.controller.dto.TurnDirection;
import ro.developmentfactory.thedrones.repository.DroneRepository;
import ro.developmentfactory.thedrones.repository.DroneStatusRepository;
import ro.developmentfactory.thedrones.repository.entity.Direction;
import ro.developmentfactory.thedrones.repository.entity.Drone;
import ro.developmentfactory.thedrones.repository.entity.DroneStatus;

import java.util.Objects;
import java.util.UUID;

@Service
public class DronePositionServiceImpl implements DronePositionService {

    private final DroneStatusRepository droneStatusRepository;
    private final DroneRepository droneRepository;
    private static final Logger log = LoggerFactory.getLogger(DronePositionServiceImpl.class);

    public DronePositionServiceImpl(DroneStatusRepository droneStatusRepository, DroneRepository droneRepository) {
        this.droneStatusRepository = droneStatusRepository;
        this.droneRepository = droneRepository;
    }
    @Override
    @Transactional
    public DroneStatusResponse turn(UUID idDrone, TurnDirection turnDirection) {

        Drone drone = droneRepository.findById(idDrone)
                .orElseThrow(() -> new EntityNotFoundException("Drone with id " + idDrone + " not found"));

     DroneStatus currentStatus = drone.getDroneStatus();
     Objects.requireNonNull(currentStatus, "Drone status must not be null");

     Direction newDirection = (turnDirection == TurnDirection.RIGHT)
             ? currentStatus.getFacingDirection().turnRight()
             : currentStatus.getFacingDirection().turnLeft();

     currentStatus.setFacingDirection(newDirection);
     droneStatusRepository.save(currentStatus);
     return convertToResponse(currentStatus);
    }

    @Override
    public DroneStatusResponse moveForward(UUID idDrone) {

        log.debug("Moving forward for drone: {}", idDrone);

       Drone drone = findDroneById(idDrone);
       DroneStatus droneStatus = getDroneStatus(drone);

       Position newPosition = calculateNewPosition(droneStatus);
       validateMove(newPosition);

       updateDronePosition(droneStatus, newPosition);
       saveDroneStatus(droneStatus);

       log.debug("Drone moved successfully to position:({}, {}", newPosition.getX(), newPosition.getY());
       return convertToResponse(droneStatus);
    }

    private Drone findDroneById(UUID idDrone) {
        return droneRepository.findById(idDrone)
                .orElseThrow(() -> new EntityNotFoundException("Drone with id " + idDrone + " not found"));
    }

    private DroneStatus getDroneStatus(Drone drone) {
        DroneStatus droneStatus = drone.getDroneStatus();
        Objects.requireNonNull(droneStatus, "Drone status must not be null");
        return droneStatus;
    }
    private void updateDronePosition(DroneStatus droneStatus, Position newPosition) {
            droneStatus.setCurrentPositionX(newPosition.getX());
            droneStatus.setCurrentPositionY(newPosition.getY());
           incrementDroneMoveCount(droneStatus.getDrone());
            log.debug("New position for drone:({}, {})", newPosition.getX(), newPosition.getY());
    }

    private void incrementDroneMoveCount(Drone drone) {
        drone.setCountMove(drone.getCountMove() + 1);
    }

    private void saveDroneStatus(DroneStatus droneStatus) {
        droneStatusRepository.save(droneStatus);
        log.debug("Drone Status saved: {}", droneStatus);
    }

    private void validateMove(Position newPosition) {
        log.debug("Validating position:({}, {})", newPosition.getX(), newPosition.getY());
        if(!isPositionValid(newPosition)) {
           throw new IllegalArgumentException("Drone cannot move because it's at the edge of the field.");
       }
    }
private boolean isPositionValid(Position position){
        int x = position.getX();
        int y = position.getY();

        return x >= 0 && x < 10 && y >= 0 && y < 10;
}

    private Position calculateNewPosition(DroneStatus droneStatus) {
        int x = droneStatus.getCurrentPositionX();
        int y = droneStatus.getCurrentPositionY();

        switch (droneStatus.getFacingDirection()) {
            case N:
                y += 1;
            break;

            case E:
                x += 1;
            break;

            case S:
                y -= 1;
            break;

            case W:
                x -= 1;
            break;
        }
        return new Position(x, y);
    }


    private DroneStatusResponse convertToResponse(DroneStatus droneStatus) {
        if (droneStatus == null) {
            throw new IllegalArgumentException("Drone must not be null");
        }

        DroneStatusResponse response = DroneStatusResponse.builder()
                .idDrone(droneStatus.getDrone().getIdDrone())
                .currentPositionX(droneStatus.getCurrentPositionX())
                .currentPositionY(droneStatus.getCurrentPositionY())
                .facingDirection(droneStatus.getFacingDirection())
                .build();

        log.debug("Converted drone status to response: {}", response);
        return response;
    }
}
