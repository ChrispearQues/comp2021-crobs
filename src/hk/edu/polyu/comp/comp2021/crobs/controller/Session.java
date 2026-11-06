package hk.edu.polyu.comp.comp2021.crobs.controller;

import hk.edu.polyu.comp.comp2021.crobs.model.User;

/** Holds the currently logged-in user. No user is logged in when a session starts. */
public class Session {

    private User currentUser;

    public User getCurrentUser() { return currentUser; }

    public void setCurrentUser(User currentUser) { this.currentUser = currentUser; }

    public boolean isLoggedIn() { return currentUser != null; }
}
