package ro.developmentfactory.thedrones.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ro.developmentfactory.thedrones.repository.DroneRepository;
import ro.developmentfactory.thedrones.repository.entity.Direction;
import ro.developmentfactory.thedrones.repository.entity.Drone;
import ro.developmentfactory.thedrones.repository.entity.DroneStatus;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.UUID;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class DroneStatusControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private DroneRepository droneRepository;

    @Autowired
    private DroneStatusService droneStatusService;

    @BeforeEach
    public void setUp() {
        Drone drone = Drone.builder()
                .name("Test Drone")
                .createdAt(OffsetDateTime.now(ZoneOffset.UTC))
                .updatedAt(OffsetDateTime.now(ZoneOffset.UTC))
                .build();
        drone = droneRepository.save(drone);

        DroneStatus droneStatus = DroneStatus.builder()
                .drone(drone)
                .currentPositionX(5)
                .currentPositionY(5)
                .facingDirection(Direction.N)
                .build();
        droneStatusService.saveDroneStatus(droneStatus);
    }

    @Test
    public void testGetDroneStatus() throws Exception {
        List<Drone> drones = (List<Drone>) droneRepository.findAll();
        UUID testId = drones.getFirst().getIdDrone();

        mockMvc.perform(MockMvcRequestBuilders.get("/drones/status/{idDrone}", testId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.currentPositionX").value(0))
                .andExpect(jsonPath("$.currentPositionY").value(0))
                .andExpect(jsonPath("$.facingDirection").value("N"))
                .andExpect(jsonPath("$.idDrone").value(testId.toString()))
                .andReturn();
    }
}
