package hk.edu.polyu.comp.comp2021.crobs.controller;

import hk.edu.polyu.comp.comp2021.crobs.model.CROBS;
import hk.edu.polyu.comp.comp2021.crobs.model.CrobsException;
import hk.edu.polyu.comp.comp2021.crobs.model.CrobsMessages;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * BON2: every command that changes the state can be undone and redone, and a
 * command that failed is not recorded.
 */
class UndoRedoTest {

    private CROBS crobs;
    private CommandHistory history;
    private CommandParser parser;

    @BeforeEach
    void setUp() {
        crobs = new CROBS();
        history = new CommandHistory();
        parser = new CommandParser(crobs, history);
    }

    /** Runs one line the way the dispatcher does: execute, then record on success. */
    private String run(String line) {
        Command command = parser.parse(line);
        String result = command.execute();
        if (command.isUndoable()) {
            history.record(command);
        }
        return result;
    }

    private void loginSuperadmin() {
        run("login U000");
    }

    @Test
    void addUserIsUndoneAndRedone() {
        loginSuperadmin();
        assertEquals("User U001 created.", run("addUser Alice"));

        assertEquals(CrobsMessages.UNDONE, run("undo"));
        assertThrows(CrobsException.NotFoundException.class, () -> crobs.login("U001"));

        assertEquals(CrobsMessages.REDONE, run("redo"));
        crobs.login("U001");
        assertEquals("U001", crobs.getCurrentUser().getId());
    }

    @Test
    void removeUserUndoBringsTheBookingsBack() {
        loginSuperadmin();
        run("addUser Alice");
        run("addRoom computerLab 20 wifi");
        run("login U001");
        run("book R001 2026-11-20 09:00 10:00 \"Group meeting\"");
        loginSuperadmin();
        run("removeUser U001");
        assertThrows(CrobsException.NotFoundException.class, () -> crobs.login("U001"));

        run("undo");
        run("login U001");
        assertEquals(1, crobs.listBookings().size());
    }

    @Test
    void changeRoleIsUndone() {
        loginSuperadmin();
        run("addUser Alice");
        run("changeRole U001 admin");
        assertEquals("ADMIN", crobs.snapshot().copyUsers().get("U001").getRole().name());

        run("undo");
        assertEquals("REGULAR", crobs.snapshot().copyUsers().get("U001").getRole().name());
    }

    @Test
    void modifyRoomIsUndone() {
        loginSuperadmin();
        run("addRoom computerLab 20 wifi");
        run("modifyRoom R001 capacity 40");
        assertEquals(40, crobs.snapshot().copyRooms().get("R001").getCapacity());

        run("undo");
        assertEquals(20, crobs.snapshot().copyRooms().get("R001").getCapacity());
    }

    @Test
    void deleteRoomIsUndoneWithItsFutureBookings() {
        loginSuperadmin();
        run("addUser Alice");
        run("addRoom seminarRoom 12 -");
        run("login U001");
        run("book R001 2026-11-20 09:00 10:00 \"Talk\"");
        loginSuperadmin();
        run("deleteRoom R001");
        assertThrows(CrobsException.NotFoundException.class, () -> crobs.listRoom("R001"));

        run("undo");
        assertEquals("R001 seminarRoom 12 -", crobs.listRoom("R001"));
        run("login U001");
        assertEquals(1, crobs.listBookings().size());
    }

    @Test
    void bookIsUndoneAndRedone() {
        loginSuperadmin();
        run("addUser Alice");
        run("addRoom computerLab 20 wifi");
        run("login U001");
        assertEquals("Booking B001 created.",
                run("book R001 2026-11-20 09:00 10:00 \"Group meeting\""));

        run("undo");
        assertEquals(CrobsMessages.NO_BOOKING, crobs.listBookings().get(0));
        run("redo");
        assertEquals(1, crobs.listBookings().size());
        assertEquals("B001", crobs.listBookings().get(0).split(" ")[0]);
    }

    @Test
    void modifyBookingIsUndone() {
        loginSuperadmin();
        run("addUser Alice");
        run("addRoom computerLab 20 wifi");
        run("login U001");
        run("book R001 2026-11-20 09:00 10:00 \"First\"");
        run("modifyBooking B001 R001 2026-11-21 11:00 12:00 \"Second\"");

        run("undo");
        assertEquals("B001 R001 2026-11-20 09:00-10:00 First", crobs.listBookings().get(0));
    }

    @Test
    void deleteBookingIsUndone() {
        loginSuperadmin();
        run("addUser Alice");
        run("addRoom computerLab 20 wifi");
        run("login U001");
        run("book R001 2026-11-20 09:00 10:00 \"First\"");
        run("deleteBooking B001");
        assertEquals(CrobsMessages.NO_BOOKING, crobs.listBookings().get(0));

        run("undo");
        assertEquals(1, crobs.listBookings().size());
    }

    @Test
    void undoingTwiceGoesBackTwoSteps() {
        loginSuperadmin();
        run("addRoom computerLab 20 wifi");
        run("addRoom seminarRoom 12 -");
        run("undo");
        run("undo");
        loginsAreStillWorking();
    }

    private void loginsAreStillWorking() {
        assertThrows(CrobsException.NotFoundException.class, () -> crobs.listRoom("R001"));
        assertThrows(CrobsException.NotFoundException.class, () -> crobs.listRoom("R002"));
    }

    @Test
    void aNewCommandClearsTheRedoStack() {
        loginSuperadmin();
        run("addRoom computerLab 20 wifi");
        run("undo");
        run("addRoom seminarRoom 12 -");
        assertEquals(CrobsMessages.NOTHING_TO_REDO, run("redo"));
    }

    @Test
    void emptyStacksReportIt() {
        loginSuperadmin();
        assertEquals(CrobsMessages.NOTHING_TO_UNDO, run("undo"));
        assertEquals(CrobsMessages.NOTHING_TO_REDO, run("redo"));
    }

    @Test
    void undoNeedsALoggedInUser() {
        assertThrows(CrobsException.PermissionDeniedException.class, () -> run("undo"));
        assertThrows(CrobsException.PermissionDeniedException.class, () -> run("redo"));
    }

    @Test
    void aCommandThatFailedIsNotRecorded() {
        loginSuperadmin();
        run("addUser Alice");
        run("addRoom computerLab 20 wifi");
        run("login U001");
        run("book R001 2026-11-20 09:00 10:00 \"First\"");
        int before = history.undoableCount();
        assertThrows(CrobsException.ConflictException.class,
                () -> run("book R001 2026-11-20 09:30 10:30 \"Clash\""));
        assertEquals(before, history.undoableCount());
    }

    @Test
    void onlyStateChangingCommandsAreUndoable() {
        loginSuperadmin();
        run("addUser Alice");
        run("addRoom computerLab 20 wifi");
        run("login U001");
        run("book R001 2026-11-20 09:00 10:00 \"First\"");

        assertTrue(parser.parse("addUser Bob").isUndoable());
        assertTrue(parser.parse("removeUser U001").isUndoable());
        assertTrue(parser.parse("changeRole U001 admin").isUndoable());
        assertTrue(parser.parse("addRoom seminarRoom 12 -").isUndoable());
        assertTrue(parser.parse("modifyRoom R001 capacity 30").isUndoable());
        assertTrue(parser.parse("deleteRoom R001").isUndoable());
        assertTrue(parser.parse("book R001 2026-11-21 09:00 10:00 \"x\"").isUndoable());
        assertTrue(parser.parse("modifyBooking B001 R001 2026-11-21 09:00 10:00 \"x\"").isUndoable());
        assertTrue(parser.parse("deleteBooking B001").isUndoable());

        assertFalse(parser.parse("login U000").isUndoable());
        assertFalse(parser.parse("logout").isUndoable());
        assertFalse(parser.parse("search 2026-11-20 09:00 10:00 1 -").isUndoable());
        assertFalse(parser.parse("schedule R001 2026-11-20").isUndoable());
        assertFalse(parser.parse("listRoom R001").isUndoable());
        assertFalse(parser.parse("listAllRooms").isUndoable());
        assertFalse(parser.parse("listBookings").isUndoable());
        assertFalse(parser.parse("undo").isUndoable());
        assertFalse(parser.parse("redo").isUndoable());
    }

    @Test
    void undoRestoresTheIdCounter() {
        loginSuperadmin();
        run("addUser Alice");
        run("undo");
        assertEquals("User U001 created.", run("addUser Bob"));
    }

    @Test
    void undoAndRedoWorkThroughTheParser() {
        loginSuperadmin();
        Command add = parser.parse("addRoom computerLab 20 wifi");
        add.execute();
        history.record(add);

        assertEquals(CrobsMessages.UNDONE, parser.parse("undo").execute());
        assertEquals(CrobsMessages.REDONE, parser.parse("redo").execute());
        assertEquals("R001 computerLab 20 wifi", crobs.listRoom("R001"));
    }
}
