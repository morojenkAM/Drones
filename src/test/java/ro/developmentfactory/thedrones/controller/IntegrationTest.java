package ro.developmentfactory.thedrones.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import ro.developmentfactory.thedrones.repository.DroneRepository;
import ro.developmentfactory.thedrones.repository.DroneStatusRepository;
import ro.developmentfactory.thedrones.repository.entity.Drone;
import ro.developmentfactory.thedrones.repository.entity.DroneStatus;

import static ro.developmentfactory.thedrones.TestDefaults.*;

@SpringBootTest
@AutoConfigureMockMvc
public class IntegrationTest {

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected DroneRepository droneRepository;

    @Autowired
    protected DroneStatusRepository droneStatusRepository;

    @Autowired
    protected ObjectMapper objectMapper;

    @BeforeEach
    protected void beforeEach() {
        droneRepository.deleteAll();
        droneStatusRepository.deleteAll();
    }

    protected UUID insertDefaultDroneAndDroneStatus() {
        Drone drone = Drone.builder()
                .name(DRONE_NAME)
                .countMove(DRONE_COUNT_MOVE)
                .createdAt(DRONE_CREATED_AT)
                .updatedAt(DRONE_UPDATED_AT)
                .build();
        Drone savedDrone = droneRepository.save(drone);
        DroneStatus droneStatus = DroneStatus.builder()
                .currentPositionX(DRONE_STATUS_POSITION_X)
                .currentPositionY(DRONE_STATUS_POSITION_Y)
                .facingDirection(DRONE_STATUS_FACING_DIRECTION)
                .drone(savedDrone)
                .build();
        droneStatusRepository.save(droneStatus);
        return savedDrone.getIdDrone();
    }


}
