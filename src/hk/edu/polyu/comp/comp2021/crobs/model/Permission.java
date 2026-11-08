package hk.edu.polyu.comp.comp2021.crobs.model;

/** Central place for role checks. Keep permission logic here, not in the controller. */
public final class Permission {

    private Permission() { }

    /** Every command except login and quit needs somebody to be logged in. */
    public static void requireLogin(User user) {
        if (user == null) {
            throw new CrobsException.PermissionDeniedException("You must log in first");
        }
    }

    /** Admin actions may be run by an admin or by the superadmin. */
    public static void requireAdmin(User user) {
        requireLogin(user);
        if (user.getRole() == Role.REGULAR) {
            throw new CrobsException.PermissionDeniedException(
                    "This operation requires admin rights; " + user.getId() + " is a regular user");
        }
    }

    /** Only the single superadmin may do this (REQ4). */
    public static void requireSuperAdmin(User user) {
        requireLogin(user);
        if (user.getRole() != Role.SUPERADMIN) {
            throw new CrobsException.PermissionDeniedException(
                    "This operation requires the superadmin; " + user.getId() + " is not the superadmin");
        }
    }
}
