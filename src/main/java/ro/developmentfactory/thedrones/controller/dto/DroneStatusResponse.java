package ro.developmentfactory.thedrones.controller.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ro.developmentfactory.thedrones.repository.entity.Direction;

import java.util.UUID;
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder

@JsonInclude(JsonInclude.Include.NON_NULL)
public class DroneStatusResponse {
    private Integer currentPositionX;
    private Integer currentPositionY;
    private Direction facingDirection;
    private UUID idDrone;


}


