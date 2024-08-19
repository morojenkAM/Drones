package ro.developmentfactory.thedrones.service;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import ro.developmentfactory.thedrones.controller.dto.DroneStatusResponse;
import ro.developmentfactory.thedrones.repository.entity.Drone;
import ro.developmentfactory.thedrones.repository.entity.DroneStatus;
import ro.developmentfactory.thedrones.repository.DroneRepository;
import ro.developmentfactory.thedrones.repository.DroneStatusRepository;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class DronesStatusServiceImplTest {

    @Mock
    private DroneStatusRepository droneStatusRepository;

    @Mock
    private DroneRepository droneRepository;

    @InjectMocks
    private DroneStatusServiceImpl droneStatusService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Fetch drone status should return DroneStatus when ID exists")
    void fetchDroneStatus_WhenIdExists_ReturnsDroneStatus() {
        // Given
        UUID droneId = UUID.randomUUID();
        UUID droneStatusId = UUID.randomUUID();

        Drone drone = new Drone();
        drone.setIdDrone(droneId);

        DroneStatus expectedDroneStatus = new DroneStatus();
        expectedDroneStatus.setIdDroneStatus(droneStatusId);
        expectedDroneStatus.setDrone(drone);


       drone.setDroneStatus(expectedDroneStatus);


        when(droneRepository.findById(droneId)).thenReturn(Optional.of(drone));

        // When
        DroneStatusResponse actualDroneStatusResponse = droneStatusService.fetchDroneStatus(droneId);

        // Then
        assertNotNull(actualDroneStatusResponse, "DroneStatusResponse should not be null");
       assertEquals(droneId, actualDroneStatusResponse.getIdDrone(), "DroneStatusResponse drone ID should match the expected value");

    }


    @Test
    @DisplayName("Fetch drone status should return empty when ID does not exist")
    void fetchDroneStatus_WhenIdDoesNotExist_ReturnsEmpty() {
        // Given
        UUID droneId = UUID.randomUUID();
        when(droneStatusRepository.findById(droneId)).thenReturn(Optional.empty());

        // When & Then
        EntityNotFoundException thrown = assertThrows(
                EntityNotFoundException.class,
                () -> droneStatusService.fetchDroneStatus(droneId),
                "Expected fetchDroneStatus() to throw IllegalArgumentException"
        );
        assertEquals("Drone not found for this ID", thrown.getMessage());
    }

    @Test
    @DisplayName("Save drone status should save when drone exists")
    void saveDroneStatus_WhenDroneExists_SavesDroneStatus() {
        // Given
        UUID droneId = UUID.randomUUID();
        Drone drone = new Drone();
        drone.setIdDrone(droneId);

        DroneStatus droneStatus = new DroneStatus();
        droneStatus.setDrone(drone);

        when(droneRepository.findById(droneId)).thenReturn(Optional.of(drone));

        // When
        droneStatusService.saveDroneStatus(droneStatus);

        // Then
        verify(droneStatusRepository, times(1)).save(droneStatus);
    }

    @Test
    @DisplayName("Save drone status should throw exception if drone does not exist")
    void saveDroneStatus_WhenDroneDoesNotExist_ThrowsException() {
        // Given
        UUID droneId = UUID.randomUUID();
        DroneStatus droneStatus = new DroneStatus();
        droneStatus.setDrone(new Drone());
        droneStatus.getDrone().setIdDrone(droneId);

        when(droneRepository.findById(droneId)).thenReturn(Optional.empty());

        // When & Then
        IllegalArgumentException thrown = assertThrows(
                IllegalArgumentException.class,
                () -> droneStatusService.saveDroneStatus(droneStatus),
                "Expected saveDroneStatus() to throw IllegalArgumentException"
        );
        assertEquals("Drone with this ID does not exist.", thrown.getMessage());
    }

    @Test
    @DisplayName("Save drone status with null drone should throw exception")
    void saveDroneStatus_WhenDroneIsNull_ShouldThrowException() {
        // Given
        DroneStatus droneStatus = new DroneStatus();

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> droneStatusService.saveDroneStatus(droneStatus),
                "Drone must not be null");
        assertEquals("Drone must not be null", exception.getMessage());
    }

    @Test
    @DisplayName("Update drone status when it exists should update the status")
    void updateDroneStatus_WhenDroneStatusExists_UpdatesDroneStatus() {
        // Given
        UUID droneId = UUID.randomUUID();
        Drone drone = new Drone();
        drone.setIdDrone(droneId);

        DroneStatus existingDroneStatus = new DroneStatus();
        existingDroneStatus.setIdDroneStatus(droneId);
        existingDroneStatus.setDrone(drone);
        existingDroneStatus.setCurrentPositionX(1);

        DroneStatus updatedDroneStatus = new DroneStatus();
        updatedDroneStatus.setIdDroneStatus(droneId);
        updatedDroneStatus.setDrone(drone);
        updatedDroneStatus.setCurrentPositionX(2);

        when(droneStatusRepository.findById(droneId)).thenReturn(Optional.of(existingDroneStatus));
        when(droneRepository.findById(droneId)).thenReturn(Optional.of(drone));

        // When
        droneStatusService.saveDroneStatus(updatedDroneStatus);

        // Then
        verify(droneStatusRepository, times(1)).save(updatedDroneStatus);
        assertEquals(2, updatedDroneStatus.getCurrentPositionX(), "DroneStatus position should be updated");
    }

    @Test
    @DisplayName("Fetch drone status when repository returns empty should return empty optional")
    void fetchDroneStatus_WhenRepositoryReturnsEmpty_HandlesGracefully() {
        // Given
        UUID droneId = UUID.randomUUID();
        when(droneStatusRepository.findById(droneId)).thenReturn(Optional.empty());

        // When & Then
        EntityNotFoundException thrown = assertThrows(
                EntityNotFoundException.class,
                () -> droneStatusService.fetchDroneStatus(droneId),
                "Expected fetchDroneStatus() to throw IllegalArgumentException"
        );
        assertEquals("Drone not found for this ID", thrown.getMessage());
    }

    @Test
    @DisplayName("Delete drone status should succeed when ID exists")
    void deleteDroneStatus_WhenIdExists_DeletesDroneStatus() {
        // Given
        UUID droneStatusId = UUID.randomUUID();
        when(droneStatusRepository.existsById(droneStatusId)).thenReturn(true);

        // When
        droneStatusService.deleteDroneStatus(droneStatusId);

        // Then
        verify(droneStatusRepository, times(1)).deleteById(droneStatusId);
    }

    @Test
    @DisplayName("Delete drone status should throw exception when ID does not exist")
    void deleteDroneStatus_WhenIdDoesNotExist_ThrowsException() {
        // Given
        UUID droneStatusId = UUID.randomUUID();
        when(droneStatusRepository.existsById(droneStatusId)).thenReturn(false);

        // When & Then
        EntityNotFoundException thrown = assertThrows(
                EntityNotFoundException.class,
                () -> droneStatusService.deleteDroneStatus(droneStatusId),
                "Expected deleteDroneStatus() to throw EntityNotFoundException"
        );
        assertEquals("DroneStatus not found for this ID", thrown.getMessage());
    }

}
