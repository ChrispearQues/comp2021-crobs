package hk.edu.polyu.comp.comp2021.crobs.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CROBSTest {

    @Test
    void testCROBSConstructor() {
        CROBS crobs = new CROBS();

        assertNotNull(crobs);
        assertNull(crobs.getCurrentUser());
    }

    @Test
    void superadminIsCreatedWithFixedId() {
        CROBS crobs = new CROBS();
        crobs.login(CROBS.SUPERADMIN_ID);

        User current = crobs.getCurrentUser();
        assertNotNull(current);
        assertEquals("U000", current.getId());
        assertEquals("superadmin", current.getName());
        assertEquals(Role.SUPERADMIN, current.getRole());
    }

    @Test
    void loginAcceptsIdsCaseInsensitively() {
        CROBS crobs = new CROBS();
        crobs.login("u000");

        assertEquals("U000", crobs.getCurrentUser().getId());
    }

    @Test
    void loginUnknownUserThrowsNotFound() {
        CROBS crobs = new CROBS();

        assertThrows(CrobsException.NotFoundException.class, () -> crobs.login("U999"));
        assertNull(crobs.getCurrentUser());
    }

    @Test
    void secondLoginWithoutLogoutIsRejected() {
        CROBS crobs = new CROBS();
        crobs.login("U000");

        assertThrows(CrobsException.MalformedCommandException.class, () -> crobs.login("U000"));
    }

    @Test
    void logoutClearsCurrentUser() {
        CROBS crobs = new CROBS();
        crobs.login("U000");
        crobs.logout();

        assertNull(crobs.getCurrentUser());
    }

    @Test
    void loginAfterLogoutWorks() {
        CROBS crobs = new CROBS();
        crobs.login("U000");
        crobs.logout();
        crobs.login("U000");

        assertEquals("U000", crobs.getCurrentUser().getId());
    }

    @Test
    void loginWithBlankIdThrowsMalformed() {
        CROBS crobs = new CROBS();

        assertThrows(CrobsException.MalformedCommandException.class, () -> crobs.login("  "));
        assertNull(crobs.getCurrentUser());
    }

    @Test
    void changeRoleNeedsALoggedInUser() {
        CROBS crobs = new CROBS();

        assertThrows(CrobsException.PermissionDeniedException.class,
                () -> crobs.changeRole(CROBS.SUPERADMIN_ID, "admin"));
    }

    @Test
    void logoutWithoutLoginIsRejected() {
        CROBS crobs = new CROBS();

        assertThrows(CrobsException.PermissionDeniedException.class, crobs::logout);
    }

    @Test
    void changeRoleRequiresSuperAdmin() {
        CROBS crobs = new CROBS();

        assertThrows(CrobsException.PermissionDeniedException.class,
                () -> crobs.changeRole("U000", "admin"));
    }

    @Test
    void changeRoleOnUnknownUserThrowsNotFound() {
        CROBS crobs = new CROBS();
        crobs.login("U000");

        assertThrows(CrobsException.NotFoundException.class,
                () -> crobs.changeRole("U123", "admin"));
    }

    @Test
    void changeRoleRejectsUnknownRoleLabel() {
        CROBS crobs = new CROBS();
        crobs.login("U000");

        assertThrows(CrobsException.MalformedCommandException.class,
                () -> crobs.changeRole("U000", "wizard"));
    }

    @Test
    void superadminRoleCannotBeChanged() {
        CROBS crobs = new CROBS();
        crobs.login("U000");

        // U000 is the only user so far, and its role is fixed.
        assertThrows(CrobsException.PermissionDeniedException.class,
                () -> crobs.changeRole("U000", "regular"));
    }
}
