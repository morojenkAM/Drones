package ro.developmentfactory.thedrones.repository.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString

public class Drone{
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id_drone")
    private UUID idDrone;

    @Column(name = "name")
    private String name;

    @Column(name = "count_move")
    private int countMove;

    @Column(name = "created_at", columnDefinition = "TIMESTAMP")
    private OffsetDateTime createdAt;

    @Column(name = "updated_at",columnDefinition =  "TIMESTAMP")
    private OffsetDateTime updatedAt;

    @JsonBackReference
    @OneToOne(mappedBy = "drone", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private DroneStatus droneStatus;

}
