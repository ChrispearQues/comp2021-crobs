package hk.edu.polyu.comp.comp2021.crobs.model;

/** Central place for role checks. Keep permission logic here, not in the controller. */
public final class Permission {

    private Permission() { }

    /** @throws CrobsException.PermissionDeniedException when nobody is logged in. */
    public static void requireLogin(User user) {
        if (user == null) {
            throw new CrobsException.PermissionDeniedException(CrobsMessages.NOT_LOGGED_IN);
        }
    }

    /** @throws CrobsException.PermissionDeniedException unless the user is an admin or the superadmin. */
    public static void requireAdmin(User user) {
        requireLogin(user);
        if (!isAdmin(user)) {
            throw new CrobsException.PermissionDeniedException(CrobsMessages.ADMIN_ONLY);
        }
    }

    /** @throws CrobsException.PermissionDeniedException unless the user is the superadmin. */
    public static void requireSuperAdmin(User user) {
        requireLogin(user);
        if (user.getRole() != Role.SUPERADMIN) {
            throw new CrobsException.PermissionDeniedException(CrobsMessages.SUPERADMIN_ONLY);
        }
    }

    /** The superadmin is an admin too. */
    public static boolean isAdmin(User user) {
        return user != null && (user.getRole() == Role.ADMIN || user.getRole() == Role.SUPERADMIN);
    }
}
