package ro.developmentfactory.thedrones.service;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import ro.developmentfactory.thedrones.controller.dto.DroneStatusResponse;
import ro.developmentfactory.thedrones.controller.dto.TurnDirection;
import ro.developmentfactory.thedrones.repository.DroneRepository;
import ro.developmentfactory.thedrones.repository.DroneStatusRepository;
import ro.developmentfactory.thedrones.repository.entity.Direction;
import ro.developmentfactory.thedrones.repository.entity.Drone;
import ro.developmentfactory.thedrones.repository.entity.DroneStatus;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class DronePositionServiceImplTest {

    @Mock
    private DroneStatusRepository droneStatusRepository;

    @Mock
    private DroneRepository droneRepository;

    @InjectMocks
    private DronePositionServiceImpl dronePositionService;

    private Drone drone;
    private DroneStatus droneStatus;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        droneStatus = new DroneStatus();
        droneStatus.setCurrentPositionX(5);
        droneStatus.setCurrentPositionY(5);
        droneStatus.setFacingDirection(Direction.N);

        drone = new Drone();
        drone.setIdDrone(UUID.randomUUID());
        drone.setDroneStatus(droneStatus);
        drone.setCountMove(0);

        droneStatus.setDrone(drone);
    }

    @Test
    @DisplayName("Turning Direction: Valid right turn should update direction")
    void turningDirection_ValidRightTurn_ShouldUpdateDirection() {
        // Given
        when(droneRepository.findById(any(UUID.class))).thenReturn(Optional.of(drone));

        // When
        DroneStatusResponse response = dronePositionService.turn(drone.getIdDrone(), TurnDirection.RIGHT);

        // Then
        assertEquals(Direction.E, response.getFacingDirection());
        verify(droneStatusRepository).save(droneStatus);
    }

    @Test
    @DisplayName("Turning Direction: Invalid drone ID should throw exception")
    void turningDirection_InvalidDroneId_ShouldThrowException() {
        // Given
        when(droneRepository.findById(any(UUID.class))).thenReturn(Optional.empty());

        // When // Then
        assertThrows(EntityNotFoundException.class, () -> dronePositionService.turn(UUID.randomUUID(), TurnDirection.RIGHT));
    }

    @Test
    @DisplayName("Move Forward: Valid move should update position")
    void moveForward_ValidMove_ShouldUpdatePosition() {
        // Given
        when(droneRepository.findById(any(UUID.class))).thenReturn(Optional.of(drone));

        // When
        DroneStatusResponse response = dronePositionService.moveForward(drone.getIdDrone());

        // Then
        assertEquals(5, response.getCurrentPositionX());
        assertEquals(6, response.getCurrentPositionY());
        assertEquals(Direction.N, response.getFacingDirection());
        verify(droneStatusRepository).save(droneStatus);
    }

    @Test
    @DisplayName("Move Forward: Invalid drone ID should throw exception")
    void moveForward_InvalidDroneId_ShouldThrowException() {
        // Given
        when(droneRepository.findById(any(UUID.class))).thenReturn(Optional.empty());

        // When // Then
        assertThrows(EntityNotFoundException.class, () -> dronePositionService.moveForward(UUID.randomUUID()));
    }

    @Test
    @DisplayName("Move Forward: Drone at edge should not update position")
    void moveForward_DroneAtEdge_ShouldNotUpdatePosition() {
        // Given
        droneStatus.setCurrentPositionX(9);
        droneStatus.setCurrentPositionY(9);
        droneStatus.setFacingDirection(Direction.E);
        when(droneRepository.findById(any(UUID.class))).thenReturn(Optional.of(drone));

        // When
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> dronePositionService.moveForward(drone.getIdDrone()));

        // Then
        assertEquals("Drone cannot move because it's at the edge of the field.", exception.getMessage());
        verify(droneStatusRepository, never()).save(any(DroneStatus.class));
    }


    @Test
    @DisplayName("Move Forward: Multiple valid moves should update position correctly")
    void moveForward_MultipleMoves_ShouldUpdatePositionCorrectly() {
        // Given
        droneStatus.setCurrentPositionX(0);
        droneStatus.setCurrentPositionY(0);
        droneStatus.setFacingDirection(Direction.E);
        when(droneRepository.findById(any(UUID.class))).thenReturn(Optional.of(drone));

        // When
        dronePositionService.moveForward(drone.getIdDrone()); // Move to (1, 0)
        dronePositionService.moveForward(drone.getIdDrone()); // Move to (2, 0)

        // Then
        assertEquals(2, droneStatus.getCurrentPositionX());
        assertEquals(0, droneStatus.getCurrentPositionY());
        assertEquals(Direction.E, droneStatus.getFacingDirection());
        assertEquals(2, drone.getCountMove());
    }

    @ParameterizedTest
    @CsvSource({
            "N,E",
            "E,S",
            "S,W",
            "W,N"
    })
    @DisplayName("Turning Direction: Valid turn should update direction correctly")
    void turningDirection_ValidTurn_ShouldUpdateDirection(Direction initialDirection, Direction expectedDirection) {
        // Given
        droneStatus.setFacingDirection(initialDirection);
        when(droneRepository.findById(any(UUID.class))).thenReturn(Optional.of(drone));

        // When
        DroneStatusResponse response = dronePositionService.turn(drone.getIdDrone(), TurnDirection.RIGHT);

        // Then
        assertEquals(expectedDirection, response.getFacingDirection());
        verify(droneStatusRepository).save(droneStatus);
    }

    @ParameterizedTest
    @CsvSource({
            "0,0,E,1,0",
            "0,0,N,0,1",
            "1,1,W,0,1",
            "1,1,S,1,0"
    })
    @DisplayName("Move Forward: Valid moves should update position correctly")
    void moveForward_ValidMoves_ShouldUpdatePositionCorrectly(int startX, int startY, Direction direction, int expectedX, int expectedY) {
        // Given
        droneStatus.setCurrentPositionX(startX);
        droneStatus.setCurrentPositionY(startY);
        droneStatus.setFacingDirection(direction);
        when(droneRepository.findById(any(UUID.class))).thenReturn(Optional.of(drone));

        // When
        DroneStatusResponse response = dronePositionService.moveForward(drone.getIdDrone());

        // Then
        assertEquals(expectedX, response.getCurrentPositionX());
        assertEquals(expectedY, response.getCurrentPositionY());
        assertEquals(direction, response.getFacingDirection());
        verify(droneStatusRepository).save(droneStatus);
    }
}
