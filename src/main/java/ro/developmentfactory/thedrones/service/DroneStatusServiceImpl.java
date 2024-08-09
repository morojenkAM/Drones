package ro.developmentfactory.thedrones.service;

import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ro.developmentfactory.thedrones.controller.dto.DroneStatusResponse;
import ro.developmentfactory.thedrones.repository.entity.Drone;
import ro.developmentfactory.thedrones.repository.entity.DroneStatus;
import ro.developmentfactory.thedrones.repository.DroneRepository;
import ro.developmentfactory.thedrones.repository.DroneStatusRepository;

import java.util.Optional;
import java.util.UUID;

@Service
public class DroneStatusServiceImpl implements DroneStatusService {

    private final DroneStatusRepository droneStatusRepository;
    private final DroneRepository droneRepository;
    private static final Logger logger = LoggerFactory.getLogger(DroneStatusServiceImpl.class);

    public DroneStatusServiceImpl(DroneStatusRepository droneStatusRepository, DroneRepository droneRepository) {
        this.droneStatusRepository = droneStatusRepository;
        this.droneRepository = droneRepository;
    }

    // Read
    @Override
    public DroneStatusResponse fetchDroneStatus(UUID idDrone) {
        logger.debug("Fetching drone with ID: {}",idDrone);
        Drone drone = droneRepository.findById(idDrone)
                .orElseThrow(() ->{
                    logger.error("Drone not fount for ID: {}",idDrone);
                    return new EntityNotFoundException("Drone not found for this ID");

                });
        logger.debug("Drone found : {}",drone);
        DroneStatus droneStatus = drone.getDroneStatus();

        if (droneStatus == null) {
            logger.error("No DroneStatus found for this ID: {}", idDrone);
            throw new EntityNotFoundException("No DroneStatus found for this Drone");
        }

        logger.debug("DroneStatus found : {}",droneStatus);

        return convertToResponse(droneStatus);
    }



    // Save
    @Override
    public void saveDroneStatus(DroneStatus droneStatus) {
        if (droneStatus.getDrone() == null) {
            throw new IllegalArgumentException("Drone must not be null");
        }

        Optional<Drone> droneOptional = droneRepository.findById(droneStatus.getDrone().getIdDrone());
        if (droneOptional.isPresent()) {
            droneStatus.setDrone(droneOptional.get());
        } else {
            throw new IllegalArgumentException("Drone with this ID does not exist.");
        }

        droneStatusRepository.save(droneStatus);
    }

    @Override
    public void deleteDroneStatus(UUID idDroneStatus) {
        if (!droneStatusRepository.existsById(idDroneStatus)) {
            throw new EntityNotFoundException("DroneStatus not found for this ID");
        }
        droneStatusRepository.deleteById(idDroneStatus);
    }


    private DroneStatusResponse convertToResponse(DroneStatus droneStatus) {
        if (droneStatus.getDrone() == null) {
            throw new IllegalArgumentException("Drone must not be null");
        }
        DroneStatusResponse response = DroneStatusResponse.builder()
                .idDroneStatus(droneStatus.getIdDroneStatus())
                .idDrone(droneStatus.getDrone().getIdDrone())
                .currentPositionX(droneStatus.getCurrentPositionX())
                .currentPositionY(droneStatus.getCurrentPositionY())
                .facingDirection(droneStatus.getFacingDirection())
                .build();
        logger.debug("Converted drone status to response: {}", response);

        return response;
    }

}
