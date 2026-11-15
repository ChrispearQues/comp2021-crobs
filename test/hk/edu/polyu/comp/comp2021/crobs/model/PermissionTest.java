package hk.edu.polyu.comp.comp2021.crobs.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PermissionTest {

    private static final Class<CrobsException.PermissionDeniedException> DENIED =
            CrobsException.PermissionDeniedException.class;

    private static User user(Role role) {
        return new User("U002", "someone", role);
    }

    @Test
    void anyLoggedInUserPassesTheLoginCheck() {
        Permission.requireLogin(user(Role.REGULAR));
    }

    @Test
    void nobodyLoggedInIsRejected() {
        assertThrows(DENIED, () -> Permission.requireLogin(null));
    }

    @Test
    void adminsAndTheSuperAdminPassTheAdminCheck() {
        Permission.requireAdmin(user(Role.ADMIN));
        Permission.requireAdmin(user(Role.SUPERADMIN));
    }

    @Test
    void regularUsersAndGuestsFailTheAdminCheck() {
        assertThrows(DENIED, () -> Permission.requireAdmin(user(Role.REGULAR)));
        assertThrows(DENIED, () -> Permission.requireAdmin(null));
    }

    @Test
    void onlyTheSuperAdminPassesTheSuperAdminCheck() {
        Permission.requireSuperAdmin(user(Role.SUPERADMIN));
    }

    @Test
    void adminsAndRegularUsersFailTheSuperAdminCheck() {
        assertThrows(DENIED, () -> Permission.requireSuperAdmin(user(Role.ADMIN)));
        assertThrows(DENIED, () -> Permission.requireSuperAdmin(user(Role.REGULAR)));
        assertThrows(DENIED, () -> Permission.requireSuperAdmin(null));
    }

    @Test
    void isAdminCoversTheSuperAdmin() {
        assertTrue(Permission.isAdmin(user(Role.SUPERADMIN)));
        assertTrue(Permission.isAdmin(user(Role.ADMIN)));
        assertFalse(Permission.isAdmin(user(Role.REGULAR)));
        assertFalse(Permission.isAdmin(null));
    }
}
