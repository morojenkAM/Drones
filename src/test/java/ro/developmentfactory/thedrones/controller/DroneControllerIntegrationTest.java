package ro.developmentfactory.thedrones.controller;

import java.util.ArrayList;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import java.util.UUID;
import ro.developmentfactory.thedrones.controller.dto.DroneRequest;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.isA;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static ro.developmentfactory.thedrones.TestDefaults.*;

public class DroneControllerIntegrationTest extends IntegrationTest {

    @Test
    @DisplayName("test saveDrone to save successfully")
    public void testSaveDrone() throws Exception {
        DroneRequest request = new DroneRequest(DRONE_NAME);

        mockMvc.perform(MockMvcRequestBuilders.post("/drones")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idDrone").exists())
                .andExpect(jsonPath("$.name").value(DRONE_NAME))
                .andExpect(jsonPath("$.countMove").value(DRONE_COUNT_MOVE))
                .andExpect(jsonPath("$.createdAt").exists())
                .andExpect(jsonPath("$.updatedAt").exists())
                .andReturn();
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "  ", "\t", "\n"})
    @DisplayName("test saveDrone bad request for blank or empty drone name")
    public void testSaveDroneBlankOrEmptyName(String blankOrEmptyDroneName) throws Exception {
        DroneRequest request = new DroneRequest(blankOrEmptyDroneName);

        mockMvc.perform(MockMvcRequestBuilders.post("/drones")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andReturn();
    }

    @Test
    @DisplayName("test saveDrone bad request for null drone name")
    public void testSaveDroneNullName() throws Exception {
        DroneRequest request = new DroneRequest(null);

        mockMvc.perform(MockMvcRequestBuilders.post("/drones")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andReturn();
    }

    @Test
    @DisplayName("test fetchDronesList to return empty list")
    public void testFetchDronesListEmpty() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/drones")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.*", isA(ArrayList.class)))
                .andExpect(jsonPath("$").isEmpty())
                .andReturn();
    }

    @Test
    @DisplayName("test fetchDronesList to return a list with 1 drone")
    public void testFetchDronesList() throws Exception {
        UUID droneId = insertDefaultDroneAndDroneStatus();

        mockMvc.perform(MockMvcRequestBuilders.get("/drones")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.*", isA(ArrayList.class)))
                .andExpect(jsonPath("$.*", hasSize(1)))
                .andExpect(jsonPath("$[0].idDrone").value(droneId.toString()))
                .andExpect(jsonPath("$[0].name").value(DRONE_NAME))
                .andExpect(jsonPath("$[0].countMove").value(DRONE_COUNT_MOVE))
                .andExpect(jsonPath("$[0].createdAt").value(DRONE_CREATED_AT.toString()))
                .andExpect(jsonPath("$[0].updatedAt").value(DRONE_UPDATED_AT.toString()))
                .andReturn();
    }

    @Test
    @DisplayName("test updateDrone to update successfully")
    public void testUpdateDrone() throws Exception {
        UUID droneId = insertDefaultDroneAndDroneStatus();

        String updatedDroneName = "Updated Drone Name";
        DroneRequest request = new DroneRequest(updatedDroneName);

        mockMvc.perform(MockMvcRequestBuilders.patch("/drones/{id}", droneId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idDrone").value(droneId.toString()))
                .andExpect(jsonPath("$.name").value(updatedDroneName))
                .andExpect(jsonPath("$.countMove").value(DRONE_COUNT_MOVE))
                .andExpect(jsonPath("$.createdAt").value(DRONE_CREATED_AT.toString()))
                .andExpect(jsonPath("$.updatedAt").exists())
                .andReturn();
    }

    @Test
    @DisplayName("test updateDrone not found")
    public void testUpdateDroneNotFound() throws Exception {
        DroneRequest request = new DroneRequest(DRONE_NAME);

        mockMvc.perform(MockMvcRequestBuilders.patch("/drones/{id}", UUID.randomUUID())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andReturn();
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "  ", "\t", "\n"})
    @DisplayName("test updateDrone bad request for blank or empty drone name")
    public void testUpdateDroneBlankOrEmptyName(String blankOrEmptyDroneName) throws Exception {
        UUID droneId = insertDefaultDroneAndDroneStatus();

        DroneRequest request = new DroneRequest(blankOrEmptyDroneName);

        mockMvc.perform(MockMvcRequestBuilders.patch("/drones/{id}", droneId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andReturn();
    }

    @Test
    @DisplayName("test updateDrone bad request for null drone name")
    public void testUpdateDroneNullName() throws Exception {
        UUID droneId = insertDefaultDroneAndDroneStatus();

        DroneRequest request = new DroneRequest(null);

        mockMvc.perform(MockMvcRequestBuilders.patch("/drones/{id}", droneId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andReturn();
    }

    @Test
    @DisplayName("test deleteDroneById")
    public void testDeleteDroneById() throws Exception {
        UUID droneId = insertDefaultDroneAndDroneStatus();

        mockMvc.perform(MockMvcRequestBuilders.delete("/drones/{id}", droneId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
    }

    @Test
    @DisplayName("test deleteDroneById not found")
    public void testDeleteDroneByIdNotFound() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/drones/{id}", UUID.randomUUID())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andReturn();
    }

}


