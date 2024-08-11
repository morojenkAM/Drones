package ro.developmentfactory.thedrones.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ro.developmentfactory.thedrones.controller.dto.TurnDirection;

import java.util.UUID;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class DronePositionControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void testTurnLeft() throws Exception {
        UUID droneId = UUID.fromString("9f14a6f8-c5f8-4582-9f7f-0221080b574f");
        mockMvc.perform(MockMvcRequestBuilders.post("/drones/{id}/turn/{direction}", droneId, TurnDirection.LEFT)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.facingDirection").value("N"))
                .andReturn();
    }

    @Test
    public void testTurnRight() throws Exception {
        UUID droneId = UUID.fromString("9f14a6f8-c5f8-4582-9f7f-0221080b574f");
        mockMvc.perform(MockMvcRequestBuilders.post("/drones/{id}/turn/{direction}", droneId, TurnDirection.RIGHT)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.facingDirection").value("E"));
    }

    @Test
    public void testMoveForward() throws Exception {
        UUID droneId = UUID.fromString("9f14a6f8-c5f8-4582-9f7f-0221080b574f");
        mockMvc.perform(MockMvcRequestBuilders.post("/drones/{id}/goForward", droneId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.currentPositionX").value(0));
    }
}
