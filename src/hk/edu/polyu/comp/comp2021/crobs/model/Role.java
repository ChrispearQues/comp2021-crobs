package hk.edu.polyu.comp.comp2021.crobs.model;

/** User roles. The superadmin is the only user that can change roles. */
public enum Role {

    /** The single user created when the system starts; an admin with extra powers. */
    SUPERADMIN,

    /** Can manage rooms and regular users. */
    ADMIN,

    /** Can book rooms, search and view schedules. */
    REGULAR
}
