package hk.edu.polyu.comp.comp2021.crobs.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class RoomTypeTest {

    @Test
    void mapsEveryLabelBackToItsConstant() {
        assertEquals(RoomType.LECTURE_THEATRE, RoomType.fromLabel("lectureTheatre"));
        assertEquals(RoomType.COMPUTER_LAB, RoomType.fromLabel("computerLab"));
        assertEquals(RoomType.SEMINAR_ROOM, RoomType.fromLabel("seminarRoom"));
        assertEquals(RoomType.MEETING_ROOM, RoomType.fromLabel("meetingRoom"));
    }

    @Test
    void ignoresTheCaseOfTheLabel() {
        assertEquals(RoomType.MEETING_ROOM, RoomType.fromLabel("MEETINGROOM"));
    }

    @Test
    void rejectsAnUnsupportedLabel() {
        assertThrows(CrobsException.MalformedCommandException.class,
                () -> RoomType.fromLabel("cinema"));
    }
}
