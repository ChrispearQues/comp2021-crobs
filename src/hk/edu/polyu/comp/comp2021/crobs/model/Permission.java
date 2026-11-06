package hk.edu.polyu.comp.comp2021.crobs.model;

/** Central place for role checks. Keep permission logic here, not in the controller. */
public final class Permission {

    private Permission() { }

    public static void requireLogin(User user) {
        // TODO: throw PermissionDeniedException when user == null
        throw new UnsupportedOperationException("TODO: Permission.requireLogin");
    }

    public static void requireAdmin(User user) {
        // TODO: requireLogin, then SUPERADMIN or ADMIN
        throw new UnsupportedOperationException("TODO: Permission.requireAdmin");
    }

    public static void requireSuperAdmin(User user) {
        // TODO: requireLogin, then SUPERADMIN only
        throw new UnsupportedOperationException("TODO: Permission.requireSuperAdmin");
    }
}
