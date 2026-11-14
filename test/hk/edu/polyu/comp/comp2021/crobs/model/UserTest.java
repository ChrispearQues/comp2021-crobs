package hk.edu.polyu.comp.comp2021.crobs.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class UserTest {
    @Test
    void testValidUserCreationAndGetters() {
        User user = new User("U001", "Alice", Role.REGULAR);
        assertEquals("U001", user.getId());
        assertEquals("Alice", user.getName());
        assertEquals(Role.REGULAR, user.getRole());
        assertFalse(user.isAdmin());
        assertEquals("U001 | Alice | REGULAR", user.toString());
    }

    @Test
    void testRoleChangeAndAdminCheckBoundary() {
        User user = new User("U002", "Bob", Role.REGULAR);
        assertFalse(user.isAdmin());

        user.setRole(Role.ADMIN);
        assertEquals(Role.ADMIN, user.getRole());
        assertTrue(user.isAdmin());

        user.setRole(Role.SUPERADMIN);
        assertEquals(Role.SUPERADMIN, user.getRole());
        assertTrue(user.isAdmin());
    }

    @Test
    void testInvalidUserArgumentsThrowException() {
        assertThrows(CrobsException.MalformedCommandException.class,
                () -> new User(null, "Alice", Role.REGULAR));
        assertThrows(CrobsException.MalformedCommandException.class,
                () -> new User("   ", "Alice", Role.REGULAR));
        assertThrows(CrobsException.MalformedCommandException.class,
                () -> new User("U001", null, Role.REGULAR));
        assertThrows(CrobsException.MalformedCommandException.class,
                () -> new User("U001", "   ", Role.REGULAR));
        assertThrows(CrobsException.MalformedCommandException.class,
                () -> new User("U001", "Alice", null));

        User validUser = new User("U001", "Alice", Role.REGULAR);
        assertThrows(CrobsException.MalformedCommandException.class,
                () -> validUser.setRole(null));
    }
}
