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
import static ro.developmentfactory.thedrones.service.Defaults.*;

@SpringBootTest
@AutoConfigureMockMvc
public class DronePositionControllerIntegrationTest extends IntegrationTest {

    @ParameterizedTest
    @ValueSource(strings = {"RIGHT"})
    @DisplayName("Test turn to save successfully with different directions")
    public void testTurn(TurnDirection turnDirection) throws Exception {
        UUID droneId = insertDefaultDroneAndDroneStatus();

        mockMvc.perform(MockMvcRequestBuilders.post("/drones/{id}/turn/{direction}", droneId, turnDirection.name())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.facingDirection").value("E"))
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
