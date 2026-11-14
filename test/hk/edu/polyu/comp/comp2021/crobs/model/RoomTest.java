package hk.edu.polyu.comp.comp2021.crobs.model;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class RoomTest {
    @Test
    void testValidRoomCreationAndHelpers() {
        Set<String> features = Room.parseFeatures("projector, whiteboard, projector");
        Room room = new Room("R001", RoomType.LECTURE_THEATRE, 120, features);

        assertEquals("R001", room.getId());
        assertEquals(RoomType.LECTURE_THEATRE, room.getType());
        assertEquals(120, room.getCapacity());
        assertEquals(2, room.getFeatures().size());
        assertTrue(room.hasAllFeatures(Arrays.asList("projector", "whiteboard")));
        assertFalse(room.hasAllFeatures(Collections.singletonList("computers")));
        assertEquals("R001 | lectureTheatre | capacity=120 | features=projector,whiteboard", room.toString());
    }

    @Test
    void testModifyRoomAndEmptyFeaturesBoundary() {
        Room room = new Room("R002", RoomType.MEETING_ROOM, 1, null);
        assertTrue(room.getFeatures().isEmpty());
        assertTrue(room.hasAllFeatures(null));
        assertTrue(room.hasAllFeatures(Collections.emptyList()));
        assertEquals("R002 | meetingRoom | capacity=1 | features=none", room.toString());

        room.modify("roomType", "computerLab");
        assertEquals(RoomType.COMPUTER_LAB, room.getType());

        room.modify("capacity", "45");
        assertEquals(45, room.getCapacity());

        room.modify("features", "computers, microphone");
        assertEquals(2, room.getFeatures().size());

        room.modify("features", "-");
        assertTrue(room.getFeatures().isEmpty());
    }

    @Test
    void testInvalidRoomAndRoomTypeThrowException() {
        // Invalid RoomType labels
        assertThrows(CrobsException.MalformedCommandException.class, () -> RoomType.fromLabel(null));
        assertThrows(CrobsException.MalformedCommandException.class, () -> RoomType.fromLabel("   "));
        assertThrows(CrobsException.MalformedCommandException.class, () -> RoomType.fromLabel("auditorium"));

        // Invalid Room constructor arguments
        assertThrows(CrobsException.MalformedCommandException.class,
                () -> new Room(null, RoomType.SEMINAR_ROOM, 20, null));
        assertThrows(CrobsException.MalformedCommandException.class,
                () -> new Room("   ", RoomType.SEMINAR_ROOM, 20, null));
        assertThrows(CrobsException.MalformedCommandException.class,
                () -> new Room("R003", null, 20, null));
        assertThrows(CrobsException.MalformedCommandException.class,
                () -> new Room("R003", RoomType.SEMINAR_ROOM, 0, null));
        assertThrows(CrobsException.MalformedCommandException.class,
                () -> new Room("R003", RoomType.SEMINAR_ROOM, -10, null));

        // Invalid capacity parsing and modify arguments
        assertThrows(CrobsException.MalformedCommandException.class, () -> Room.parseCapacity(null));
        assertThrows(CrobsException.MalformedCommandException.class, () -> Room.parseCapacity("abc"));
        assertThrows(CrobsException.MalformedCommandException.class, () -> Room.parseCapacity("0"));

        Room validRoom = new Room("R003", RoomType.SEMINAR_ROOM, 20, null);
        assertThrows(CrobsException.MalformedCommandException.class, () -> validRoom.modify(null, "10"));
        assertThrows(CrobsException.MalformedCommandException.class, () -> validRoom.modify("capacity", ""));
        assertThrows(CrobsException.MalformedCommandException.class, () -> validRoom.modify("unknownField", "10"));
        assertThrows(CrobsException.MalformedCommandException.class, () -> validRoom.modify("capacity", "-5"));
        assertThrows(CrobsException.MalformedCommandException.class, () -> validRoom.modify("roomType", "badType"));
    }
}
