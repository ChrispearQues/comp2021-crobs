package hk.edu.polyu.comp.comp2021.crobs.controller;

import hk.edu.polyu.comp.comp2021.crobs.model.CROBS;
import hk.edu.polyu.comp.comp2021.crobs.model.User;

/**
 * Thin view of the login state. The user object itself lives in {@link CROBS}
 * (single source of truth); this class must never keep a second copy.
 */
public class Session {

    private final CROBS crobs;

    public Session(CROBS crobs) {
        this.crobs = crobs;
    }

    public User getCurrentUser() { return crobs.getCurrentUser(); }

    public boolean isLoggedIn() { return crobs.getCurrentUser() != null; }
}
