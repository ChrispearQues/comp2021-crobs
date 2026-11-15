package hk.edu.polyu.comp.comp2021.crobs.model;

import java.util.LinkedHashSet;
import java.util.Set;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RoomTest {

    private static final Class<CrobsException.MalformedCommandException> MALFORMED =
            CrobsException.MalformedCommandException.class;

    private static Room room(String... features) {
        Set<String> set = new LinkedHashSet<>();
        for (String feature : features) {
            set.add(feature);
        }
        return new Room("R001", RoomType.COMPUTER_LAB, 40, set);
    }

    @Test
    void describesTheRoomInOneLine() {
        assertEquals("R001 computerLab 40 projector,whiteboard",
                room("projector", "whiteboard").describe());
    }

    @Test
    void describesARoomWithoutFeaturesWithADash() {
        assertEquals("R001 computerLab 40 -", room().describe());
    }

    @Test
    void parsesFeaturesByTrimmingAndLowerCasing() {
        Set<String> parsed = Room.parseFeatures(" Projector , WHITEBOARD ");

        assertEquals(2, parsed.size());
        assertTrue(parsed.contains("projector"));
        assertTrue(parsed.contains("whiteboard"));
    }

    @Test
    void treatsADashAsNoFeature() {
        assertTrue(Room.parseFeatures("-").isEmpty());
    }

    @Test
    void treatsAnEmptyListAsNoFeature() {
        assertTrue(Room.parseFeatures("").isEmpty());
        assertTrue(Room.parseFeatures(null).isEmpty());
    }

    @Test
    void ignoresEmptyFeatureTokens() {
        assertEquals(2, Room.parseFeatures("a,,b,").size());
    }

    @Test
    void checksWhetherAllFeaturesArePresent() {
        Room room = room("projector", "whiteboard");

        assertTrue(room.hasAllFeatures(Room.parseFeatures("projector")));
        assertTrue(room.hasAllFeatures(Room.parseFeatures("-")));
        assertFalse(room.hasAllFeatures(Room.parseFeatures("projector,wifi")));
    }

    @Test
    void modifiesTheType() {
        Room room = room("projector");

        room.modify("TYPE", "seminarRoom");

        assertEquals(RoomType.SEMINAR_ROOM, room.getType());
    }

    @Test
    void modifiesTheCapacity() {
        Room room = room("projector");

        room.modify("capacity", "120");

        assertEquals(120, room.getCapacity());
    }

    @Test
    void modifiesTheFeatures() {
        Room room = room("projector");

        room.modify("features", "wifi,Projector");

        assertEquals("wifi,projector", Room.renderFeatures(room.getFeatures()));
    }

    @Test
    void rejectsAnUnknownField() {
        Room room = room("projector");

        assertThrows(MALFORMED, () -> room.modify("colour", "blue"));
    }

    @Test
    void rejectsAnUnknownType() {
        Room room = room("projector");

        assertThrows(MALFORMED, () -> room.modify("type", "cinema"));
    }

    @Test
    void rejectsACapacityThatIsNotANumber() {
        Room room = room("projector");

        assertThrows(MALFORMED, () -> room.modify("capacity", "many"));
    }

    @Test
    void rejectsACapacityOfZeroOrLess() {
        Room room = room("projector");

        assertThrows(MALFORMED, () -> room.modify("capacity", "0"));
        assertThrows(MALFORMED, () -> room.modify("capacity", "-5"));
    }

    @Test
    void parsesACapacity() {
        assertEquals(30, Room.parseCapacity("30", CrobsMessages.INVALID_CAPACITY));
        assertThrows(MALFORMED, () -> Room.parseCapacity("3.5", CrobsMessages.INVALID_CAPACITY));
    }

    @Test
    void rendersAnEmptyFeatureSetAsADash() {
        assertEquals("-", Room.renderFeatures(new LinkedHashSet<String>()));
    }
}
