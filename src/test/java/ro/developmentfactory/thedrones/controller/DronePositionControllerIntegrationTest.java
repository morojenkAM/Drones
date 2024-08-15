package ro.developmentfactory.thedrones.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import ro.developmentfactory.thedrones.controller.dto.TurnDirection;
import ro.developmentfactory.thedrones.repository.entity.Direction;
import ro.developmentfactory.thedrones.repository.entity.Drone;
import ro.developmentfactory.thedrones.repository.entity.DroneStatus;
import ro.developmentfactory.thedrones.service.DronePositionServiceImpl;
import java.time.OffsetDateTime;
import java.util.UUID;
import static ro.developmentfactory.thedrones.TestDefaults.*;

@SpringBootTest
@AutoConfigureMockMvc
public class DronePositionControllerIntegrationTest extends IntegrationTest {

    @Autowired
    private DronePositionServiceImpl dronePositionServiceImpl;

    @ParameterizedTest
    @ValueSource(strings = {"RIGHT"})
    @DisplayName("Test turn to save successfully with different directions")
    public void testTurnRight(TurnDirection turnDirection) throws Exception {
        UUID droneId = insertDefaultDroneAndDroneStatus();

        mockMvc.perform(MockMvcRequestBuilders.post("/drones/{id}/turn/{direction}", droneId, turnDirection.name())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.facingDirection").value("E"))
                .andReturn();
    }

    @ParameterizedTest
    @ValueSource(strings = {"LEFT"})
    @DisplayName("Test turn to save successfully with different directions")
    public void testTurnLeft(TurnDirection turnDirection) throws Exception {
        UUID droneId = insertDefaultDroneAndDroneStatus();

        mockMvc.perform(MockMvcRequestBuilders.post("/drones/{id}/turn/{direction}", droneId, turnDirection.name())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.facingDirection").value("W"))
                .andReturn();
    }

    @Test
    @DisplayName("Test move forward successfully")
    public void testMoveForward() throws Exception {
        UUID droneId = insertDefaultDroneAndDroneStatus();

        mockMvc.perform(MockMvcRequestBuilders.post("/drones/{id}/goForward", droneId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.currentPositionX").value(DRONE_STATUS_POSITION_X ))
                .andExpect(MockMvcResultMatchers.jsonPath("$.currentPositionY").value(DRONE_STATUS_POSITION_Y + 1))
                .andReturn();
    }

    @Test
    @DisplayName("Test move forward with non-existing drone")
    public void testMoveForwardNonExistingDrone() throws Exception {
        UUID nonExistingDroneId = UUID.randomUUID();

        mockMvc.perform(MockMvcRequestBuilders.post("/drones/{id}/goForward", nonExistingDroneId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andReturn();
    }

    @Test
    @DisplayName("Test turn multiple times")
    public void testTurnMultipleTimes() throws Exception {
        UUID droneId = insertDefaultDroneAndDroneStatus();

        mockMvc.perform(MockMvcRequestBuilders.post("/drones/{id}/turn/{direction}", droneId, TurnDirection.RIGHT.name())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk());

        mockMvc.perform(MockMvcRequestBuilders.post("/drones/{id}/turn/{direction}", droneId, TurnDirection.RIGHT.name())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.facingDirection").value("S"))
                .andReturn();
    }

    @Test
    @DisplayName("Test move forward at the edge of the field")
    public void testMoveForwardAtEdge() throws Exception {
        UUID droneId = insertDefaultDroneAndDroneStatusAtEdge();

        mockMvc.perform(MockMvcRequestBuilders.post("/drones/{id}/goForward", droneId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andReturn();
    }

    private UUID insertDefaultDroneAndDroneStatusAtEdge() {
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
