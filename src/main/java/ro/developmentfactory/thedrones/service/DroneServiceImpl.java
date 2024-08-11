package ro.developmentfactory.thedrones.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ro.developmentfactory.thedrones.controller.dto.DroneRequest;
import ro.developmentfactory.thedrones.controller.dto.DroneResponse;
import ro.developmentfactory.thedrones.repository.entity.Drone;
import ro.developmentfactory.thedrones.repository.entity.DroneStatus;
import ro.developmentfactory.thedrones.repository.DroneRepository;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import static ro.developmentfactory.thedrones.service.Defaults.*;

@Service
public class DroneServiceImpl implements DroneService {

    private static final Logger log = LoggerFactory.getLogger(DroneServiceImpl.class);

    private final DroneRepository droneRepository;
    private final DroneStatusService droneStatusService;

    public DroneServiceImpl(DroneRepository droneRepository, DroneStatusService droneStatusService) {
        this.droneRepository = droneRepository;
        this.droneStatusService = droneStatusService;
    }

    //Read operation
    @Override
    public List<Drone> fetchDroneList(){
        return (List<Drone>) droneRepository.findAll();
    }

    @Override
    @Transactional
    public DroneResponse saveDrone(DroneRequest droneRequest) {
        OffsetDateTime currentTime = OffsetDateTime.ofInstant(Instant.now(), ZoneOffset.UTC);
        Drone drone = Drone.builder()
                .name(droneRequest.getName())
                .createdAt(currentTime)
                .updatedAt(currentTime)
                .build();

        log.info("Saving drone {}", drone);
        Drone savedDrone = droneRepository.save(drone);

        DroneStatus droneStatus = DroneStatus.builder()
                .drone(savedDrone)
                .currentPositionX(DEFAULT_POSITION_X)
                .currentPositionY(DEFAULT_POSITION_Y)
                .facingDirection(DEFAULT_FACING_DIRECTION)
                .build();
        droneStatusService.saveDroneStatus(droneStatus);
        log.info("Saving drone status : {} ",droneStatus);
        return convertToResponse(savedDrone);
    }

    @Override
    public DroneResponse updateNameDrone(DroneRequest droneRequest, UUID idDrone){
        Drone droneDB = droneRepository.findById(idDrone).orElseThrow(() ->
                new EntityNotFoundException("Drone with id " + idDrone + " not found"));

        if (Objects.nonNull(droneRequest.getName()) && !"".equalsIgnoreCase(droneRequest.getName())) {
            droneDB.setName(droneRequest.getName());
        }
        OffsetDateTime now = OffsetDateTime.now(ZoneOffset.UTC);
        droneDB.setUpdatedAt(now);
        Drone updatedDrone = droneRepository.save(droneDB);
        log.info("Drone with ID: {} updated", idDrone);
        return convertToResponse(updatedDrone);
    }

    @Override
    @Transactional
    public void deleteDrone(UUID idDrone) {
        Drone drone = droneRepository.findById(idDrone)
                .orElseThrow(() -> new EntityNotFoundException("Drone with id " + idDrone + " not found"));

        DroneStatus droneStatus = drone.getDroneStatus();
        if (droneStatus != null) {
            droneStatusService.deleteDroneStatus(droneStatus.getIdDroneStatus());

        }


        droneRepository.deleteById(idDrone);
        log.info("Drone with ID: {} deleted", idDrone);
    }

    private DroneResponse convertToResponse(Drone drone){
        return DroneResponse.builder()
                .idDrone(drone.getIdDrone())
                .name(drone.getName())
                .countMove(drone.getCountMove())
                .createdAt(drone.getCreatedAt())
                .updatedAt(drone.getUpdatedAt())
                .build();
    }

}
