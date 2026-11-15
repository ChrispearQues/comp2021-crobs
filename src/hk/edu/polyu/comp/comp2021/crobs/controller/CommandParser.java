package hk.edu.polyu.comp.comp2021.crobs.controller;

import hk.edu.polyu.comp.comp2021.crobs.controller.command.AddRoomCommand;
import hk.edu.polyu.comp.comp2021.crobs.controller.command.AddUserCommand;
import hk.edu.polyu.comp.comp2021.crobs.controller.command.BookCommand;
import hk.edu.polyu.comp.comp2021.crobs.controller.command.ChangeRoleCommand;
import hk.edu.polyu.comp.comp2021.crobs.controller.command.DeleteBookingCommand;
import hk.edu.polyu.comp.comp2021.crobs.controller.command.DeleteRoomCommand;
import hk.edu.polyu.comp.comp2021.crobs.controller.command.ListAllRoomsCommand;
import hk.edu.polyu.comp.comp2021.crobs.controller.command.ListBookingsCommand;
import hk.edu.polyu.comp.comp2021.crobs.controller.command.ListRoomCommand;
import hk.edu.polyu.comp.comp2021.crobs.controller.command.LoginCommand;
import hk.edu.polyu.comp.comp2021.crobs.controller.command.LogoutCommand;
import hk.edu.polyu.comp.comp2021.crobs.controller.command.ModifyBookingCommand;
import hk.edu.polyu.comp.comp2021.crobs.controller.command.ModifyRoomCommand;
import hk.edu.polyu.comp.comp2021.crobs.controller.command.QuitCommand;
import hk.edu.polyu.comp.comp2021.crobs.controller.command.RemoveUserCommand;
import hk.edu.polyu.comp.comp2021.crobs.controller.command.ScheduleCommand;
import hk.edu.polyu.comp.comp2021.crobs.controller.command.SearchCommand;
import hk.edu.polyu.comp.comp2021.crobs.model.CROBS;
import hk.edu.polyu.comp.comp2021.crobs.model.CrobsException;
import hk.edu.polyu.comp.comp2021.crobs.model.CrobsMessages;

/**
 * Turns a raw input line into a {@link Command}. Only syntax is checked here;
 * every value check (dates, capacities, permissions, ...) belongs to the model.
 */
public class CommandParser {

    private final CROBS crobs;

    public CommandParser(CROBS crobs) {
        this.crobs = crobs;
    }

    /** @throws CrobsException.MalformedCommandException on an unknown or incomplete command. */
    public Command parse(String line) {
        if (line == null || line.trim().isEmpty()) {
            throw new CrobsException.MalformedCommandException(CrobsMessages.EMPTY_COMMAND);
        }
        String text = line.trim();
        String[] parts = text.split("\\s+");
        String name = parts[0];

        if ("login".equals(name)) {
            require(parts, 2, name);
            return new LoginCommand(crobs, parts[1]);
        }
        if ("logout".equals(name)) {
            require(parts, 1, name);
            return new LogoutCommand(crobs);
        }
        if ("quit".equals(name)) {
            require(parts, 1, name);
            return new QuitCommand(crobs);
        }
        if ("addUser".equals(name)) {
            // The name may contain spaces, so only the command word is separated.
            String[] head = text.split("\\s+", 2);
            requireAtLeast(head, 2, name);
            return new AddUserCommand(crobs, head[1]);
        }
        if ("removeUser".equals(name)) {
            require(parts, 2, name);
            return new RemoveUserCommand(crobs, parts[1]);
        }
        if ("changeRole".equals(name)) {
            require(parts, 3, name);
            return new ChangeRoleCommand(crobs, parts[1], parts[2]);
        }
        if ("addRoom".equals(name)) {
            require(parts, 4, name);
            return new AddRoomCommand(crobs, parts[1], parts[2], parts[3]);
        }
        if ("modifyRoom".equals(name)) {
            String[] head = text.split("\\s+", 4);
            requireAtLeast(head, 4, name);
            return new ModifyRoomCommand(crobs, head[1], head[2], head[3]);
        }
        if ("deleteRoom".equals(name)) {
            require(parts, 2, name);
            return new DeleteRoomCommand(crobs, parts[1]);
        }
        if ("book".equals(name)) {
            // The purpose may contain spaces.
            String[] head = text.split("\\s+", 6);
            requireAtLeast(head, 6, name);
            return new BookCommand(crobs, head[1], head[2], head[3], head[4], head[5]);
        }
        if ("modifyBooking".equals(name)) {
            String[] head = text.split("\\s+", 7);
            requireAtLeast(head, 7, name);
            return new ModifyBookingCommand(crobs,
                    head[1], head[2], head[3], head[4], head[5], head[6]);
        }
        if ("deleteBooking".equals(name)) {
            require(parts, 2, name);
            return new DeleteBookingCommand(crobs, parts[1]);
        }
        if ("search".equals(name)) {
            require(parts, 6, name);
            return new SearchCommand(crobs, parts[1], parts[2], parts[3], parts[4], parts[5]);
        }
        if ("schedule".equals(name)) {
            require(parts, 3, name);
            return new ScheduleCommand(crobs, parts[1], parts[2]);
        }
        if ("listRoom".equals(name)) {
            require(parts, 2, name);
            return new ListRoomCommand(crobs, parts[1]);
        }
        if ("listAllRooms".equals(name)) {
            require(parts, 1, name);
            return new ListAllRoomsCommand(crobs);
        }
        if ("listBookings".equals(name)) {
            require(parts, 1, name);
            return new ListBookingsCommand(crobs);
        }
        throw new CrobsException.MalformedCommandException(
                String.format(CrobsMessages.UNKNOWN_COMMAND, name));
    }

    private static void require(String[] parts, int count, String name) {
        if (parts.length != count) {
            throw new CrobsException.MalformedCommandException(
                    String.format(CrobsMessages.WRONG_ARGS, name));
        }
    }

    private static void requireAtLeast(String[] parts, int count, String name) {
        if (parts.length < count) {
            throw new CrobsException.MalformedCommandException(
                    String.format(CrobsMessages.WRONG_ARGS, name));
        }
    }
}
