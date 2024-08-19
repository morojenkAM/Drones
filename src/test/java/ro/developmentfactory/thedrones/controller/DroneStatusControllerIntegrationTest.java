package ro.developmentfactory.thedrones.controller;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.UUID;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ro.developmentfactory.thedrones.TestDefaults.*;

public class DroneStatusControllerIntegrationTest extends IntegrationTest {

    @Test
    @DisplayName("Should return drone status successfully when a valid drone ID is provided")
    public void testGetDroneStatus_Success() throws Exception {
        UUID droneId = insertDefaultDroneAndDroneStatus();

        mockMvc.perform(MockMvcRequestBuilders.get("/drones/status/{idDrone}", droneId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(result -> {
                    String content = result.getResponse().getContentAsString();
                    Assertions.assertTrue(content.contains("\"idDrone\":\"" + droneId.toString() + "\""));
                    Assertions.assertTrue(content.contains("\"currentPositionX\":" + DRONE_STATUS_POSITION_X));
                    Assertions.assertTrue(content.contains("\"currentPositionY\":" + DRONE_STATUS_POSITION_Y));
                    Assertions.assertTrue(content.contains("\"facingDirection\":\"" + DRONE_STATUS_FACING_DIRECTION + "\""));
                });
    }


    @Test
    @DisplayName("Should return 404 Not Found when an invalid drone ID is provided")
    public void testGetDroneStatus_NotFound() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/drones/status/{idDrone}", UUID.randomUUID())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
}
