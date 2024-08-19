    package ro.developmentfactory.thedrones.repository.entity;

    import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
    import jakarta.persistence.*;
    import lombok.AllArgsConstructor;
    import lombok.Builder;
    import lombok.Data;
    import lombok.NoArgsConstructor;

    import java.util.UUID;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Entity
    public class DroneStatus {

        @Id
        @GeneratedValue(strategy = GenerationType.UUID)
        @Column(name = "id_drone_status" , columnDefinition = "BINARY(16)")
        private UUID idDroneStatus;

        @Column(name = "current_position_x")
        private Integer currentPositionX;

        @Column(name = "current_position_y")
        private Integer currentPositionY;

        @Enumerated(EnumType.STRING)
        @Column(name = "facing_direction")
        private Direction facingDirection;

        @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
        @OneToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "id_drone")
        private Drone drone;
    }
