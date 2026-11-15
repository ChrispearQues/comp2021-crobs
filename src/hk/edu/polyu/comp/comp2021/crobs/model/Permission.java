package hk.edu.polyu.comp.comp2021.crobs.model;

/** Central place for role checks. Keep permission logic here, not in the controller. */
public final class Permission {

    private Permission() { }

    /**
     * Requires somebody to be logged in.
     *
     * @param user the current user, possibly null
     * @throws CrobsException.PermissionDeniedException when nobody is logged in
     */
    public static void requireLogin(User user) {
        if (user == null) {
            throw new CrobsException.PermissionDeniedException(CrobsMessages.NOT_LOGGED_IN);
        }
    }

    /**
     * Requires an admin or the superadmin.
     *
     * @param user the current user, possibly null
     * @throws CrobsException.PermissionDeniedException when the user is a guest or a regular user
     */
    public static void requireAdmin(User user) {
        requireLogin(user);
        if (!isAdmin(user)) {
            throw new CrobsException.PermissionDeniedException(CrobsMessages.ADMIN_ONLY);
        }
    }

    /**
     * Requires the superadmin.
     *
     * @param user the current user, possibly null
     * @throws CrobsException.PermissionDeniedException when the user is a guest, an admin or a regular user
     */
    public static void requireSuperAdmin(User user) {
        requireLogin(user);
        if (user.getRole() != Role.SUPERADMIN) {
            throw new CrobsException.PermissionDeniedException(CrobsMessages.SUPERADMIN_ONLY);
        }
    }

    /**
     * Tells whether a user may use the admin commands. The superadmin is an
     * admin too.
     *
     * @param user the user to test, possibly null
     * @return true for an admin or the superadmin
     */
    public static boolean isAdmin(User user) {
        return user != null && (user.getRole() == Role.ADMIN || user.getRole() == Role.SUPERADMIN);
    }
}
