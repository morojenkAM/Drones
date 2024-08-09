package ro.developmentfactory.thedrones.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.UUID;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class DroneControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;


    @Test
    public void testSaveDrone() throws Exception {
        String droneRequestJson = "{ \"name\": \"Test Drone\" }";

     mockMvc.perform(MockMvcRequestBuilders.post("/drones")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(droneRequestJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Test Drone"))
                .andExpect(jsonPath("$.countMove").value(0))
                .andExpect(jsonPath("$.createdAt").exists())
                .andExpect(jsonPath("$.updatedAt").exists())
                .andReturn();
    }


    @Test
        public void testFetchDronesList() throws Exception {
            mockMvc.perform(MockMvcRequestBuilders.post("/drones")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{ \"name\": \"Test Drone\" }"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.name").value("Test Drone"))
                    .andExpect(jsonPath("$.countMove").value(0))
                    .andExpect(jsonPath("$.createdAt").exists())
                    .andExpect(jsonPath("$.updatedAt").exists())
                    .andReturn();
        }

    @Test
    public void testUpdateDrone() throws Exception {
        UUID droneId = UUID.fromString("e3071463-a847-4914-b664-8aadd4ca0c87");

        String updatedDroneJson = "{ \"name\": \"Updated Drone Name\" }";

        mockMvc.perform(MockMvcRequestBuilders.patch("/drones/{id}", droneId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updatedDroneJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated Drone Name"))
                .andReturn();
    }

    @Test
    public void testDeleteDroneById() throws Exception {
        UUID droneId = UUID.fromString("60a0e1bd-d65b-4872-9730-54dd054e8c17");

        mockMvc.perform(MockMvcRequestBuilders.delete("/drones/{id}", droneId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
    }


}


