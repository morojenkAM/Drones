package ro.developmentfactory.thedrones.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import ro.developmentfactory.thedrones.repository.DroneRepository;
import ro.developmentfactory.thedrones.repository.DroneStatusRepository;

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

}
