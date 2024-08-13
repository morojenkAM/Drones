package ro.developmentfactory.thedrones.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import ro.developmentfactory.thedrones.controller.dto.TurnDirection;
import ro.developmentfactory.thedrones.repository.entity.Drone;
import ro.developmentfactory.thedrones.repository.entity.DroneStatus;

import java.util.UUID;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ro.developmentfactory.thedrones.TestDefaults.*;

@SpringBootTest
@AutoConfigureMockMvc
public class DronePositionControllerIntegrationTest extends IntegrationTest {

    private UUID insertDefaultDroneAndDroneStatus() {
        DroneStatus droneStatus = DroneStatus.builder()
                .idDroneStatus(DRONE_STATUS_ID)
                .currentPositionX(DRONE_STATUS_POSITION_X)
                .currentPositionY(DRONE_STATUS_POSITION_Y)
                .facingDirection(DRONE_STATUS_FACING_DIRECTION)
                .build();
        Drone drone = Drone.builder()
                .idDrone(DRONE_ID)
                .name(DRONE_NAME)
                .countMove(DRONE_COUNT_MOVE)
                .createdAt(DRONE_CREATED_AT)
                .updatedAt(DRONE_UPDATED_AT)
                .droneStatus(droneStatus)
                .build();
        UUID newDroneId = droneRepository.save(drone).getIdDrone();
        droneStatusRepository.save(droneStatus);
        return newDroneId;
    }

    @ParameterizedTest
    @ValueSource(strings = {"RIGHT", "LEFT"})
    @DisplayName("Test turn to save successfully with different directions")
    public void testTurn(TurnDirection turnDirection) throws Exception {
        UUID droneId = insertDefaultDroneAndDroneStatus();

        mockMvc.perform(MockMvcRequestBuilders.post("/drones/{id}/turn/{direction}", droneId, turnDirection.name())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.facingDirection").value(turnDirection.name()))
                .andReturn();
    }

    @ParameterizedTest
    @ValueSource(strings = {"", " ","\t", "\n"})
    @DisplayName("test save turn bad request for blank or emty drone name")
    public void testSaveTurnBlankOrEmptyDirection(String turnDirection) throws Exception {
        UUID droneId = insertDefaultDroneAndDroneStatus();

        mockMvc.perform(MockMvcRequestBuilders.post("/drones/{id}/turn/{direction}",droneId,turnDirection)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andReturn();
    }
    @Test
    @DisplayName("test save turn direction bad request for null direction")
    public void testSaveTurnNullDirection() throws Exception {
        UUID droneId = insertDefaultDroneAndDroneStatus();

        mockMvc.perform(MockMvcRequestBuilders.post("/drones/{id}/turn/{direction}",droneId,null)
        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andReturn();
    }
}
