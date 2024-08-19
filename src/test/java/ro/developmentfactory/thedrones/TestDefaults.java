package ro.developmentfactory.thedrones;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.UUID;
import ro.developmentfactory.thedrones.repository.entity.Direction;
import ro.developmentfactory.thedrones.service.Defaults;

public class TestDefaults {

    private static final Timestamp baseTimestamp = Timestamp.valueOf("2024-08-11 09:18:03");

    // Drone
    public static final UUID DRONE_ID = UUID.fromString("60a0e1bd-d65b-4872-9730-54dd054e8c17");
    public static final String DRONE_NAME = "Test Drone";
    public static final int DRONE_COUNT_MOVE = 0;
    public static final OffsetDateTime DRONE_CREATED_AT =
            OffsetDateTime.ofInstant(Instant.ofEpochMilli(baseTimestamp.getTime()), ZoneOffset.UTC);
    public static final OffsetDateTime DRONE_UPDATED_AT =
            OffsetDateTime.ofInstant(Instant.ofEpochMilli(baseTimestamp.getTime()), ZoneOffset.UTC).plusHours(1);

    // DroneStatus
    public static final UUID DRONE_STATUS_ID = UUID.fromString("60a0e1bd-d65b-4872-9730-54dd054e8c17");
    public static final Integer DRONE_STATUS_POSITION_X = Defaults.DEFAULT_POSITION_X;
    public static final Integer DRONE_STATUS_POSITION_Y = Defaults.DEFAULT_POSITION_Y;
    public static final Direction DRONE_STATUS_FACING_DIRECTION = Defaults.DEFAULT_FACING_DIRECTION;

}
