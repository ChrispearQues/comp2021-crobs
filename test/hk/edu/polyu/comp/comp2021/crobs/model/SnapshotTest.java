package hk.edu.polyu.comp.comp2021.crobs.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** The deep copy that undo and redo rely on (BON2). */
class SnapshotTest {

    private CROBS crobs;

    @BeforeEach
    void setUp() {
        crobs = new CROBS();
        crobs.login(CROBS.SUPERADMIN_ID);
        crobs.addUser("Alice");
        crobs.addRoom("computerLab", "20", "wifi,projector");
        crobs.login("U001");
        crobs.book("R001", "2026-11-20", "09:00", "10:00", "Group meeting");
    }

    @Test
    void restorePutsEveryCollectionBack() {
        Snapshot snapshot = crobs.snapshot();
        crobs.login(CROBS.SUPERADMIN_ID);
        crobs.deleteRoom("R001");
        crobs.removeUser("U001");

        crobs.restore(snapshot);

        assertEquals("R001 computerLab 20 wifi,projector", crobs.listRoom("R001"));
        assertEquals("U001", crobs.getCurrentUser().getId());
        assertEquals(1, crobs.listBookings().size());
    }

    @Test
    void snapshotIsNotChangedByALaterMutation() {
        Snapshot snapshot = crobs.snapshot();
        crobs.login(CROBS.SUPERADMIN_ID);
        crobs.modifyRoom("R001", "capacity", "50");

        crobs.restore(snapshot);
        assertEquals("R001 computerLab 20 wifi,projector", crobs.listRoom("R001"));
    }

    @Test
    void theCopiesAreIndependentOfTheSnapshot() {
        Snapshot snapshot = crobs.snapshot();
        User user = snapshot.copyUsers().get("U001");
        user.setRole(Role.ADMIN);
        Room room = snapshot.copyRooms().get("R001");
        room.modify("capacity", "99");
        Booking booking = snapshot.copyBookings().get("B001");
        booking.modify("R001", new TimeSlot("2026-11-22", "11:00", "12:00"), "Changed");

        crobs.restore(snapshot);
        assertEquals("U001", crobs.listRoom("R001").split(" ")[0].replace("R001", "U001"));
        assertEquals("R001 computerLab 20 wifi,projector", crobs.listRoom("R001"));
        assertEquals("B001 R001 2026-11-20 09:00-10:00 Group meeting", crobs.listBookings().get(0));
    }

    @Test
    void restoringWhileLoggedOutKeepsTheUserLoggedOut() {
        crobs.logout();
        Snapshot snapshot = crobs.snapshot();
        assertNull(snapshot.getCurrentUserId());

        crobs.restore(snapshot);
        assertNull(crobs.getCurrentUser());
    }

    @Test
    void theIdCountersTravelWithTheSnapshot() {
        Snapshot snapshot = crobs.snapshot();
        crobs.login(CROBS.SUPERADMIN_ID);
        crobs.addUser("Bob");

        crobs.restore(snapshot);
        crobs.login(CROBS.SUPERADMIN_ID);
        assertEquals("User U002 created.", crobs.addUser("Bob"));
    }

    @Test
    void aRoomCopySharesNoFeatureSet() {
        Room original = crobs.snapshot().copyRooms().get("R001");
        Room copy = crobs.snapshot().copyRooms().get("R001");
        assertNotSame(original, copy);
        assertTrue(copy.getFeatures().contains("wifi"));
    }

    @Test
    void theCounterOfAnIdGeneratorCannotGoBelowOne() {
        IdGenerator generator = new IdGenerator("U");
        assertEquals(1, generator.getCounter());
        generator.setCounter(7);
        assertEquals("U007", generator.next());
        assertThrows(IllegalArgumentException.class, () -> generator.setCounter(0));
    }
}
