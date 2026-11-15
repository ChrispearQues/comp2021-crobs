package hk.edu.polyu.comp.comp2021.crobs.model;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Drives every requirement through the {@link CROBS} facade, which is exactly
 * how the command layer uses it.
 */
class CROBSTest {

    private static final String DATE = "2026-11-16";
    private static final String WEEK = "2026-11-16";

    private static final Class<CrobsException.PermissionDeniedException> DENIED =
            CrobsException.PermissionDeniedException.class;
    private static final Class<CrobsException.NotFoundException> NOT_FOUND =
            CrobsException.NotFoundException.class;
    private static final Class<CrobsException.ConflictException> CONFLICT =
            CrobsException.ConflictException.class;
    private static final Class<CrobsException.MalformedCommandException> MALFORMED =
            CrobsException.MalformedCommandException.class;

    private CROBS crobs;

    @BeforeEach
    void setUp() {
        crobs = new CROBS();
    }

    /** Logs in as the automatically created superadmin (U001). */
    private void superadmin() {
        crobs.login("U001");
    }

    /** Creates U002, an empty room R001 and logs back in as the superadmin. */
    private void standardFixture() {
        superadmin();
        crobs.addUser("Alice");
        crobs.addRoom("seminarRoom", "40", "projector,whiteboard");
    }

    // ---- REQ5 / REQ6: login and logout -------------------------------------

    @Test
    void startsWithoutAUserLoggedIn() {
        assertNull(crobs.getCurrentUser());
    }

    @Test
    void theSuperAdminIsCreatedAutomatically() {
        superadmin();

        assertEquals("U001", crobs.getCurrentUser().getId());
        assertEquals(Role.SUPERADMIN, crobs.getCurrentUser().getRole());
    }

    @Test
    void loginRejectsAnUnknownUser() {
        assertThrows(NOT_FOUND, () -> crobs.login("U999"));
    }

    @Test
    void loginTwiceAsTheSameUserIsRejected() {
        superadmin();

        assertThrows(MALFORMED, () -> crobs.login("U001"));
    }

    @Test
    void logoutForgetsTheCurrentUser() {
        superadmin();

        crobs.logout();

        assertNull(crobs.getCurrentUser());
    }

    @Test
    void logoutWithoutLoginIsHarmless() {
        crobs.logout();

        assertNull(crobs.getCurrentUser());
    }

    // ---- REQ2 / REQ3: users -------------------------------------------------

    @Test
    void addUserCreatesARegularUser() {
        superadmin();

        assertEquals("User U002 created.", crobs.addUser("Alice"));

        crobs.logout();
        crobs.login("U002");
        assertEquals(Role.REGULAR, crobs.getCurrentUser().getRole());
        assertEquals("Alice", crobs.getCurrentUser().getName());
    }

    @Test
    void addUserRequiresAnAdmin() {
        assertThrows(DENIED, () -> crobs.addUser("Alice"));

        superadmin();
        crobs.addUser("Alice");
        crobs.logout();
        crobs.login("U002");
        assertThrows(DENIED, () -> crobs.addUser("Bob"));
    }

    @Test
    void addUserRejectsAnEmptyName() {
        superadmin();

        assertThrows(MALFORMED, () -> crobs.addUser("   "));
        assertThrows(MALFORMED, () -> crobs.addUser(null));
    }

    @Test
    void removeUserDeletesTheUserAndTheBookings() {
        standardFixture();
        crobs.logout();
        crobs.login("U002");
        crobs.book("R001", DATE, "09:00", "11:00", "group meeting");
        crobs.logout();
        superadmin();

        crobs.removeUser("U002");

        assertThrows(NOT_FOUND, () -> crobs.login("U002"));
        assertEquals(Collections.singletonList(CrobsMessages.NO_BOOKING_IN_WEEK),
                crobs.schedule("R001", WEEK));
    }

    @Test
    void removeUserRejectsAnAdmin() {
        standardFixture();
        crobs.changeRole("U002", "admin");

        assertThrows(DENIED, () -> crobs.removeUser("U002"));
    }

    @Test
    void removeUserRejectsAnUnknownUser() {
        superadmin();

        assertThrows(NOT_FOUND, () -> crobs.removeUser("U999"));
    }

    @Test
    void removeUserRequiresAnAdmin() {
        standardFixture();
        crobs.logout();
        crobs.login("U002");

        assertThrows(DENIED, () -> crobs.removeUser("U003"));
    }

    // ---- REQ4: roles --------------------------------------------------------

    @Test
    void changeRolePromotesAndDemotes() {
        standardFixture();

        crobs.changeRole("U002", "ADMIN");
        crobs.logout();
        crobs.login("U002");
        assertEquals(Role.ADMIN, crobs.getCurrentUser().getRole());

        crobs.logout();
        superadmin();
        crobs.changeRole("U002", "regular");
        crobs.logout();
        crobs.login("U002");
        assertEquals(Role.REGULAR, crobs.getCurrentUser().getRole());
    }

    @Test
    void changeRoleRequiresTheSuperAdmin() {
        standardFixture();
        crobs.logout();
        crobs.login("U002");

        assertThrows(DENIED, () -> crobs.changeRole("U002", "admin"));
    }

    @Test
    void changeRoleRejectsAnUnknownRole() {
        standardFixture();

        assertThrows(MALFORMED, () -> crobs.changeRole("U002", "manager"));
    }

    @Test
    void changeRoleRejectsTheSuperAdmin() {
        superadmin();

        assertThrows(DENIED, () -> crobs.changeRole("U001", "regular"));
    }

    @Test
    void changeRoleRejectsAnUnknownUser() {
        superadmin();

        assertThrows(NOT_FOUND, () -> crobs.changeRole("U999", "admin"));
    }

    // ---- REQ7 / REQ8 / REQ9: rooms -----------------------------------------

    @Test
    void addRoomCreatesARoomWithItsFeatures() {
        superadmin();

        assertEquals("Room R001 created.", crobs.addRoom("computerLab", "40", "projector,Whiteboard"));
        assertEquals("R001 computerLab 40 projector,whiteboard", crobs.listRoom("R001"));
    }

    @Test
    void addRoomRequiresAnAdmin() {
        assertThrows(DENIED, () -> crobs.addRoom("computerLab", "40", "projector"));
    }

    @Test
    void addRoomRejectsAnUnknownType() {
        superadmin();

        assertThrows(MALFORMED, () -> crobs.addRoom("cinema", "40", "projector"));
    }

    @Test
    void addRoomRejectsAnInvalidCapacity() {
        superadmin();

        assertThrows(MALFORMED, () -> crobs.addRoom("computerLab", "many", "projector"));
        assertThrows(MALFORMED, () -> crobs.addRoom("computerLab", "0", "projector"));
    }

    @Test
    void modifyRoomChangesOneField() {
        standardFixture();

        crobs.modifyRoom("R001", "capacity", "80");

        assertEquals("R001 seminarRoom 80 projector,whiteboard", crobs.listRoom("R001"));
    }

    @Test
    void modifyRoomRejectsAnUnknownRoom() {
        superadmin();

        assertThrows(NOT_FOUND, () -> crobs.modifyRoom("R999", "capacity", "80"));
    }

    @Test
    void modifyRoomRequiresAnAdmin() {
        standardFixture();
        crobs.logout();
        crobs.login("U002");

        assertThrows(DENIED, () -> crobs.modifyRoom("R001", "capacity", "80"));
    }

    @Test
    void deleteRoomCancelsItsFutureBookings() {
        standardFixture();
        String future = LocalDate.now().plusDays(10).toString();
        crobs.book("R001", future, "09:00", "11:00", "group meeting");

        crobs.deleteRoom("R001");

        assertThrows(NOT_FOUND, () -> crobs.listRoom("R001"));
        assertEquals(Collections.singletonList(CrobsMessages.NO_BOOKING), crobs.listBookings());
    }

    @Test
    void deleteRoomKeepsTheBookingsThatAreAlreadyInThePast() {
        standardFixture();
        crobs.book("R001", "2000-01-03", "09:00", "11:00", "long gone");

        crobs.deleteRoom("R001");

        assertEquals(1, crobs.listBookings().size());
    }

    @Test
    void deleteRoomRejectsAnUnknownRoom() {
        superadmin();

        assertThrows(NOT_FOUND, () -> crobs.deleteRoom("R999"));
    }

    @Test
    void deleteRoomRequiresAnAdmin() {
        standardFixture();
        crobs.logout();
        crobs.login("U002");

        assertThrows(DENIED, () -> crobs.deleteRoom("R001"));
    }

    // ---- REQ15 / REQ16: listing rooms --------------------------------------

    @Test
    void listRoomRequiresLogin() {
        assertThrows(DENIED, () -> crobs.listRoom("R001"));
    }

    @Test
    void listRoomRejectsAnUnknownRoom() {
        superadmin();

        assertThrows(NOT_FOUND, () -> crobs.listRoom("R999"));
    }

    @Test
    void listAllRoomsIsSortedByRoomId() {
        superadmin();
        crobs.addRoom("seminarRoom", "10", "-");
        crobs.addRoom("computerLab", "20", "projector");

        assertEquals(Arrays.asList(
                "R001 seminarRoom 10 -",
                "R002 computerLab 20 projector"), crobs.listAllRooms());
    }

    @Test
    void listAllRoomsRequiresLogin() {
        assertThrows(DENIED, () -> crobs.listAllRooms());
    }

    // ---- REQ10: booking -----------------------------------------------------

    @Test
    void bookCreatesABookingForTheCurrentUser() {
        standardFixture();

        assertEquals("Booking B001 created.",
                crobs.book("R001", DATE, "09:00", "11:00", "group meeting"));
        assertEquals(Collections.singletonList(
                "B001 R001 " + DATE + " 09:00-11:00 group meeting"), crobs.listBookings());
    }

    @Test
    void bookRejectsAnOverlappingBooking() {
        standardFixture();
        crobs.book("R001", DATE, "09:00", "11:00", "first");

        assertThrows(CONFLICT, () -> crobs.book("R001", DATE, "10:00", "12:00", "second"));
    }

    @Test
    void bookAllowsABookingThatStartsWhenTheOtherEnds() {
        standardFixture();
        crobs.book("R001", DATE, "09:00", "11:00", "first");

        crobs.book("R001", DATE, "11:00", "13:00", "second");

        assertEquals(2, crobs.listBookings().size());
    }

    @Test
    void bookAllowsTheSameSlotInAnotherRoom() {
        standardFixture();
        crobs.addRoom("meetingRoom", "10", "-");
        crobs.book("R001", DATE, "09:00", "11:00", "first");

        crobs.book("R002", DATE, "09:00", "11:00", "second");

        assertEquals(2, crobs.listBookings().size());
    }

    @Test
    void bookRejectsAnUnknownRoom() {
        standardFixture();

        assertThrows(NOT_FOUND, () -> crobs.book("R999", DATE, "09:00", "11:00", "meeting"));
    }

    @Test
    void bookRequiresLogin() {
        assertThrows(DENIED, () -> crobs.book("R001", DATE, "09:00", "11:00", "meeting"));
    }

    @Test
    void bookRejectsAnInvalidDate() {
        standardFixture();

        assertThrows(MALFORMED, () -> crobs.book("R001", "2026-13-01", "09:00", "11:00", "meeting"));
    }

    @Test
    void bookRejectsAnInvalidTime() {
        standardFixture();

        assertThrows(MALFORMED, () -> crobs.book("R001", DATE, "9:00", "11:00", "meeting"));
        assertThrows(MALFORMED, () -> crobs.book("R001", DATE, "09:00", "24:00", "meeting"));
    }

    @Test
    void bookRejectsAReversedInterval() {
        standardFixture();

        assertThrows(MALFORMED, () -> crobs.book("R001", DATE, "11:00", "09:00", "meeting"));
    }

    @Test
    void bookRejectsAnEmptyPurpose() {
        standardFixture();

        assertThrows(MALFORMED, () -> crobs.book("R001", DATE, "09:00", "11:00", "  "));
    }

    // ---- REQ11 / REQ12: modifying and deleting a booking --------------------

    @Test
    void modifyBookingUpdatesTheBooking() {
        standardFixture();
        crobs.book("R001", DATE, "09:00", "11:00", "group meeting");

        crobs.modifyBooking("B001", "R001", DATE, "14:00", "16:00", "review");

        assertEquals(Collections.singletonList(
                "B001 R001 " + DATE + " 14:00-16:00 review"), crobs.listBookings());
    }

    @Test
    void modifyBookingKeepsItsOwnSlotWithoutConflicting() {
        standardFixture();
        crobs.book("R001", DATE, "09:00", "11:00", "group meeting");

        crobs.modifyBooking("B001", "R001", DATE, "09:30", "11:30", "moved");

        assertEquals(Collections.singletonList(
                "B001 R001 " + DATE + " 09:30-11:30 moved"), crobs.listBookings());
    }

    @Test
    void modifyBookingRejectsAnotherUsersBooking() {
        standardFixture();
        crobs.book("R001", DATE, "09:00", "11:00", "group meeting");
        crobs.logout();
        crobs.login("U002");

        assertThrows(DENIED, () -> crobs.modifyBooking("B001", "R001", DATE, "14:00", "16:00", "hijack"));
    }

    @Test
    void modifyBookingRejectsAConflict() {
        standardFixture();
        crobs.book("R001", DATE, "09:00", "11:00", "first");
        crobs.book("R001", DATE, "14:00", "16:00", "second");

        assertThrows(CONFLICT, () -> crobs.modifyBooking("B002", "R001", DATE, "10:00", "12:00", "clash"));
    }

    @Test
    void modifyBookingRejectsAnUnknownBooking() {
        standardFixture();

        assertThrows(NOT_FOUND, () -> crobs.modifyBooking("B999", "R001", DATE, "09:00", "11:00", "x"));
    }

    @Test
    void deleteBookingRemovesIt() {
        standardFixture();
        crobs.book("R001", DATE, "09:00", "11:00", "group meeting");

        crobs.deleteBooking("B001");

        assertEquals(Collections.singletonList(CrobsMessages.NO_BOOKING), crobs.listBookings());
    }

    @Test
    void deleteBookingRejectsAnotherUsersBooking() {
        standardFixture();
        crobs.book("R001", DATE, "09:00", "11:00", "group meeting");
        crobs.logout();
        crobs.login("U002");

        assertThrows(DENIED, () -> crobs.deleteBooking("B001"));
    }

    @Test
    void deleteBookingRejectsAnUnknownBooking() {
        standardFixture();

        assertThrows(NOT_FOUND, () -> crobs.deleteBooking("B999"));
    }

    // ---- REQ13: search ------------------------------------------------------

    @Test
    void searchFindsTheFreeRoom() {
        standardFixture();

        assertEquals(Collections.singletonList("R001 seminarRoom 40 projector,whiteboard"),
                crobs.search(DATE, "09:00", "11:00", "20", "projector"));
    }

    @Test
    void searchSkipsABookedRoom() {
        standardFixture();
        crobs.book("R001", DATE, "09:00", "11:00", "group meeting");

        assertEquals(Collections.singletonList(CrobsMessages.NO_MATCHING_ROOM),
                crobs.search(DATE, "09:00", "11:00", "20", "projector"));
    }

    @Test
    void searchRequiresLogin() {
        assertThrows(DENIED, () -> crobs.search(DATE, "09:00", "11:00", "20", "-"));
    }

    // ---- REQ14: weekly schedule --------------------------------------------

    @Test
    void scheduleHidesOtherUsersFromARegularUser() {
        standardFixture();
        crobs.book("R001", DATE, "09:00", "11:00", "group meeting");
        crobs.logout();
        crobs.login("U002");

        assertEquals(Collections.singletonList(
                "B001 " + DATE + " 09:00-11:00 *** group meeting"), crobs.schedule("R001", WEEK));
    }

    @Test
    void scheduleShowsEveryUserToTheSuperAdmin() {
        standardFixture();
        crobs.logout();
        crobs.login("U002");
        crobs.book("R001", DATE, "09:00", "11:00", "group meeting");
        crobs.logout();
        superadmin();

        assertEquals(Collections.singletonList(
                "B001 " + DATE + " 09:00-11:00 U002 group meeting"), crobs.schedule("R001", WEEK));
    }

    @Test
    void scheduleAcceptsALowerCaseRoomId() {
        standardFixture();
        crobs.book("R001", DATE, "09:00", "11:00", "group meeting");

        assertEquals(Collections.singletonList(
                "B001 " + DATE + " 09:00-11:00 U001 group meeting"), crobs.schedule("r001", WEEK));
    }

    @Test
    void errorMessagesLeaveThePrefixToTheDispatcher() {
        assertFalse(CrobsMessages.INVALID_DATE.startsWith("Error:"));
        assertFalse(CrobsMessages.UNKNOWN_ROOM.startsWith("Error:"));
        assertEquals("Invalid date \"abc\", expected yyyy-MM-dd.",
                String.format(CrobsMessages.INVALID_DATE, "abc"));
    }

    @Test
    void scheduleRejectsAnUnknownRoom() {
        superadmin();

        assertThrows(NOT_FOUND, () -> crobs.schedule("R999", WEEK));
    }

    @Test
    void scheduleRequiresLogin() {
        assertThrows(DENIED, () -> crobs.schedule("R001", WEEK));
    }

    // ---- REQ17: my bookings -------------------------------------------------

    @Test
    void listBookingsIsSortedAndComesFromTheCurrentUserOnly() {
        standardFixture();
        crobs.addRoom("meetingRoom", "10", "-");
        crobs.book("R002", DATE, "14:00", "16:00", "second");
        crobs.book("R001", DATE, "09:00", "11:00", "first");

        List<String> lines = crobs.listBookings();

        assertEquals(Arrays.asList(
                "B002 R001 " + DATE + " 09:00-11:00 first",
                "B001 R002 " + DATE + " 14:00-16:00 second"), lines);
    }

    @Test
    void listBookingsReportsAnEmptyList() {
        superadmin();

        assertEquals(Collections.singletonList(CrobsMessages.NO_BOOKING), crobs.listBookings());
    }

    @Test
    void listBookingsRequiresLogin() {
        assertThrows(DENIED, () -> crobs.listBookings());
    }

    @Test
    void externalErrorsAreCarriedByTheRightExceptionType() {
        CrobsException base = new CrobsException("boom");

        assertTrue(base instanceof RuntimeException);
        assertEquals("boom", base.getMessage());
    }
}
