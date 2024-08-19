package ro.developmentfactory.thedrones.service;

import org.springframework.transaction.annotation.Transactional;
import ro.developmentfactory.thedrones.controller.dto.DroneStatusResponse;
import ro.developmentfactory.thedrones.controller.dto.TurnDirection;

import java.util.UUID;


public interface DronePositionService {

    @Transactional
    DroneStatusResponse turn(UUID idDrone, TurnDirection turnDirection);

        @Transactional
    DroneStatusResponse moveForward(UUID idDrone);

}
