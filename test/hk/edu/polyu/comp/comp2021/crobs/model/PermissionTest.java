package hk.edu.polyu.comp.comp2021.crobs.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PermissionTest {

    private static final User SUPER = new User("U000", "superadmin", Role.SUPERADMIN);
    private static final User ADMIN = new User("U001", "alice", Role.ADMIN);
    private static final User REGULAR = new User("U002", "bob", Role.REGULAR);

    @Test
    void requireLoginRejectsNull() {
        assertThrows(CrobsException.PermissionDeniedException.class,
                () -> Permission.requireLogin(null));
    }

    @Test
    void requireLoginAcceptsAnyUser() {
        assertDoesNotThrow(() -> Permission.requireLogin(REGULAR));
    }

    @Test
    void requireAdminAcceptsAdminAndSuperAdmin() {
        assertDoesNotThrow(() -> Permission.requireAdmin(ADMIN));
        assertDoesNotThrow(() -> Permission.requireAdmin(SUPER));
    }

    @Test
    void requireAdminRejectsRegularAndAnonymous() {
        assertThrows(CrobsException.PermissionDeniedException.class,
                () -> Permission.requireAdmin(REGULAR));
        assertThrows(CrobsException.PermissionDeniedException.class,
                () -> Permission.requireAdmin(null));
    }

    @Test
    void requireSuperAdminRejectsAdminAndRegular() {
        assertDoesNotThrow(() -> Permission.requireSuperAdmin(SUPER));
        assertThrows(CrobsException.PermissionDeniedException.class,
                () -> Permission.requireSuperAdmin(ADMIN));
        assertThrows(CrobsException.PermissionDeniedException.class,
                () -> Permission.requireSuperAdmin(REGULAR));
    }
}
