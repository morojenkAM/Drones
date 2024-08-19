package ro.developmentfactory.thedrones.service;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ro.developmentfactory.thedrones.controller.dto.DroneRequest;
import ro.developmentfactory.thedrones.controller.dto.DroneResponse;
import ro.developmentfactory.thedrones.repository.entity.Direction;
import ro.developmentfactory.thedrones.repository.entity.Drone;
import ro.developmentfactory.thedrones.repository.entity.DroneStatus;
import ro.developmentfactory.thedrones.repository.DroneRepository;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class DroneServiceImplTest {

    @Mock
    private DroneRepository droneRepository;

    @Mock
    private DroneStatusService droneStatusService;

    @InjectMocks
    private DroneServiceImpl droneService;

    @Test
    @DisplayName("Given a list of drones, when fetchDroneList is called, then return the list of drones")
    void givenDroneList_whenFetchDroneList_thenReturnDroneList() {
        // Given
        Drone drone1 = new Drone();
        drone1.setIdDrone(UUID.randomUUID());
        drone1.setName("Drone1");
        drone1.setCountMove(5);
        drone1.setCreatedAt(OffsetDateTime.now(ZoneOffset.UTC));
        drone1.setUpdatedAt(OffsetDateTime.now(ZoneOffset.UTC));

        Drone drone2 = new Drone();
        drone2.setIdDrone(UUID.randomUUID());
        drone2.setName("Drone2");
        drone2.setCountMove(5);
        drone2.setCreatedAt(OffsetDateTime.now(ZoneOffset.UTC));
        drone2.setUpdatedAt(OffsetDateTime.now(ZoneOffset.UTC));


        List<Drone> droneList = List.of(drone1, drone2);
        when(droneRepository.findAll()).thenReturn(droneList);

        // When
        List<Drone> result = droneService.fetchDroneList();

        // Then
        assertEquals(droneList, result);
        verify(droneRepository).findAll();
    }

    @Test
    @DisplayName("Given a DroneRequest, when saveDrone is called, then save the drone and its status and return the response")
    void givenDroneRequest_whenSaveDrone_thenSaveDroneAndReturnResponse() {
        // Given
        DroneRequest droneRequest = new DroneRequest("Drone1");
        Drone drone = Drone.builder()
                .idDrone(UUID.randomUUID())
                .name("Drone1")
                .countMove(0)
                .createdAt(OffsetDateTime.now())
                .updatedAt(OffsetDateTime.now())
                .build();

        DroneStatus expectedDroneStatus = DroneStatus.builder()
                .drone(drone)
                .currentPositionX(0)
                .currentPositionY(0)
                .facingDirection(Direction.N)
                .build();

        when(droneRepository.save(any(Drone.class))).thenReturn(drone);
        doNothing().when(droneStatusService).saveDroneStatus(any(DroneStatus.class));

        // When
        DroneResponse result = droneService.saveDrone(droneRequest);

        // Then
        assertNotNull(result);
        assertEquals(drone.getIdDrone(), result.getIdDrone());
        assertEquals(droneRequest.getName(), result.getName());

        // Verify droneRepository.save was called with the correct drone
        verify(droneRepository).save(argThat(d ->
                d.getName().equals(drone.getName()) &&
                        d.getCountMove() == drone.getCountMove() &&
                        d.getCreatedAt().toEpochSecond() == drone.getCreatedAt().toEpochSecond() &&
                        d.getUpdatedAt().toEpochSecond() == drone.getUpdatedAt().toEpochSecond()
        ));

        // Verify droneStatusService.saveDroneStatus was called with the correct status
        verify(droneStatusService).saveDroneStatus(argThat(ds ->
                Objects.equals(ds.getCurrentPositionX(), expectedDroneStatus.getCurrentPositionX()) &&
                        Objects.equals(ds.getCurrentPositionY(), expectedDroneStatus.getCurrentPositionY()) &&
                        ds.getFacingDirection() == expectedDroneStatus.getFacingDirection() &&
                        ds.getDrone().equals(drone)));
    }


    @Test
    @DisplayName("Given a valid DroneRequest and Drone id, when updateNameDrone is called, then update the drone's name and return the updated response")
    void givenValidDroneRequestAndId_whenUpdateNameDrone_thenUpdateDroneAndReturnResponse() {
        // Given
        UUID droneId = UUID.randomUUID();
        DroneRequest droneRequest = new DroneRequest("NewName");
        Drone drone = Drone.builder()
                .idDrone(droneId)
                .name("OldName")
                .countMove(5)
                .createdAt(OffsetDateTime.now())
                .updatedAt(OffsetDateTime.now().minusDays(1))
                .build();
        when(droneRepository.findById(droneId)).thenReturn(Optional.of(drone));
        when(droneRepository.save(any(Drone.class))).thenReturn(drone);

        // When
        DroneResponse result = droneService.updateNameDrone(droneRequest, droneId);

        // Then
        assertEquals("NewName", result.getName());

        verify(droneRepository).findById(droneId);
        verify(droneRepository).save(any(Drone.class));
    }

    @Test
    @DisplayName("Given a valid Drone id, when deleteDrone is called, then delete the drone")
    void givenValidDroneId_whenDeleteDrone_thenDeleteDrone() {
        // Given
        UUID droneId = UUID.randomUUID();
        UUID droneStatusId = UUID.randomUUID();

        DroneStatus droneStatus = DroneStatus.builder()
                .idDroneStatus(droneStatusId)
                .build();

        Drone drone = Drone.builder()
                .idDrone(droneId)
                .droneStatus(droneStatus)
                .build();

        when(droneRepository.findById(droneId)).thenReturn(Optional.of(drone));
        doNothing().when(droneStatusService).deleteDroneStatus(any(UUID.class));

        // When
        droneService.deleteDrone(droneId);

        // Then
        verify(droneRepository).deleteById(droneId);
        verify(droneStatusService, times(1)).deleteDroneStatus(any(UUID.class));
    }




    @Test
    @DisplayName("Given an invalid Drone id, when deleteDrone is called, then throw EntityNotFoundException")
    void givenInvalidDroneId_whenDeleteDrone_thenThrowEntityNotFoundException() {
        // Given
        UUID droneId = UUID.randomUUID();
        when(droneRepository.findById(droneId)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(EntityNotFoundException.class, () -> droneService.deleteDrone(droneId));
        verify(droneRepository).findById(droneId);
    }


    @Test
    @DisplayName("Given an empty list of drones, when fetchDroneList is called, then return an empty list")
    void givenEmptyDroneList_whenFetchDroneList_thenReturnEmptyDroneList() {
        // Given
        List<Drone> emptyDroneList = List.of();
        when(droneRepository.findAll()).thenReturn(emptyDroneList);

        // When
        List<Drone> result = droneService.fetchDroneList();

        // Then
        assertTrue(result.isEmpty());
        verify(droneRepository).findAll();
    }

    @Test
    @DisplayName("Given a Drone with an old updatedAt timestamp, when updateNameDrone is called with a new name, then updatedAt should be refreshed")
    void givenDroneWithOldUpdatedAt_whenUpdateNameDrone_thenUpdatedAtShouldBeRefreshed() {
        // Given
        UUID droneId = UUID.randomUUID();
        DroneRequest droneRequest = new DroneRequest("UpdatedName");
        OffsetDateTime oldUpdatedAt = OffsetDateTime.now().minusDays(1);
        Drone drone = new Drone();
        drone.setIdDrone(droneId);
        drone.setName("OldName");
        drone.setUpdatedAt(oldUpdatedAt);
        when(droneRepository.findById(droneId)).thenReturn(Optional.of(drone));
        when(droneRepository.save(any(Drone.class))).thenReturn(drone);

        // When
        DroneResponse result = droneService.updateNameDrone(droneRequest, droneId);

        // Then
        assertNotEquals(oldUpdatedAt, result.getUpdatedAt());
        assertEquals("UpdatedName", result.getName());
        verify(droneRepository).findById(droneId);
        verify(droneRepository).save(any(Drone.class));
    }

    @Test
    @DisplayName("Given a non-empty list of drones, when fetchDroneList is called, then return the list of drones")
    void givenNonEmptyDroneList_whenFetchDroneList_thenReturnNonEmptyDroneList() {
        // Given
        Drone drone1 = new Drone();
        Drone drone2 = new Drone();
        List<Drone> droneList = List.of(drone1, drone2);
        when(droneRepository.findAll()).thenReturn(droneList);

        // When
        List<Drone> result = droneService.fetchDroneList();

        // Then
        assertFalse(result.isEmpty());
        assertEquals(2, result.size());
        verify(droneRepository).findAll();
    }

    @Test
    @DisplayName("Given a Drone with existing name, when updateNameDrone is called with the same name, then updatedAt should be refreshed")
    void givenDroneWithExistingName_whenUpdateNameDrone_thenUpdatedAtShouldBeRefreshed() {
        // Given
        UUID droneId = UUID.randomUUID();
        DroneRequest droneRequest = new DroneRequest("ExistingName");
        OffsetDateTime oldUpdatedAt = OffsetDateTime.now().minusDays(1);
        Drone drone = new Drone();
        drone.setIdDrone(droneId);
        drone.setName("ExistingName");
        drone.setUpdatedAt(oldUpdatedAt);
        when(droneRepository.findById(droneId)).thenReturn(Optional.of(drone));
        when(droneRepository.save(any(Drone.class))).thenReturn(drone);

        // When
        DroneResponse result = droneService.updateNameDrone(droneRequest, droneId);

        // Then
        assertNotEquals(oldUpdatedAt, result.getUpdatedAt());
        assertEquals("ExistingName", result.getName());
        verify(droneRepository).findById(droneId);
        verify(droneRepository).save(any(Drone.class));
    }

    @Test
    @DisplayName("Given a Drone with a new name, when updateNameDrone is called with the same name, then updatedAt should be refreshed")
    void givenDroneWithSameName_whenUpdateNameDroneCalled_thenUpdatedAtShouldBeRefreshed() {
        // Given
        UUID droneId = UUID.randomUUID();
        DroneRequest droneRequest = new DroneRequest("SameName");
        OffsetDateTime oldUpdatedAt = OffsetDateTime.now().minusDays(1);
        Drone drone = Drone.builder()
                .idDrone(droneId)
                .name("SameName")
                .updatedAt(oldUpdatedAt)
                .build();
        when(droneRepository.findById(droneId)).thenReturn(Optional.of(drone));
        when(droneRepository.save(any(Drone.class))).thenReturn(drone);

        // When
        DroneResponse result = droneService.updateNameDrone(droneRequest, droneId);

        // Then
        assertNotEquals(oldUpdatedAt, result.getUpdatedAt());
        assertEquals("SameName", result.getName());
        verify(droneRepository).findById(droneId);
        verify(droneRepository).save(any(Drone.class));
    }
}
