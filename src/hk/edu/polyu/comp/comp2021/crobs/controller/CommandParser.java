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
import java.util.Arrays;
import java.util.Iterator;

/**
 * Turns a raw input line into a {@link Command}. Only syntax is checked here;
 * every value check (dates, capacities, permissions, ...) belongs to the model.
 *
 * <p>The usage templates below give both the arity check and the text of the
 * error message, so a command can never accept one argument more or less than
 * the specification allows.</p>
 */
public class CommandParser {

    private static final String LOGIN_USAGE = "login userID";
    private static final String LOGOUT_USAGE = "logout";
    private static final String QUIT_USAGE = "quit";
    private static final String ADD_USER_USAGE = "addUser name";
    private static final String REMOVE_USER_USAGE = "removeUser targetUserID";
    private static final String CHANGE_ROLE_USAGE = "changeRole targetUserId newRole";
    private static final String ADD_ROOM_USAGE = "addRoom roomType capacity features";
    private static final String MODIFY_ROOM_USAGE = "modifyRoom roomID field newValue";
    private static final String DELETE_ROOM_USAGE = "deleteRoom roomID";
    private static final String BOOK_USAGE = "book roomId date startTime endTime purpose";
    private static final String MODIFY_BOOKING_USAGE =
            "modifyBooking bookingID roomId date startTime endTime purpose";
    private static final String DELETE_BOOKING_USAGE = "deleteBooking bookingID";
    private static final String SEARCH_USAGE = "search date startTime endTime minCapacity features";
    private static final String SCHEDULE_USAGE = "schedule roomId weekStartDate";
    private static final String LIST_ROOM_USAGE = "listRoom roomID";
    private static final String LIST_ALL_ROOMS_USAGE = "listAllRooms";
    private static final String LIST_BOOKINGS_USAGE = "listBookings";

    private final CROBS crobs;

    /**
     * Creates a parser that builds commands for one system.
     *
     * @param crobs the system the commands work on
     */
    public CommandParser(CROBS crobs) {
        this.crobs = crobs;
    }

    /**
     * Parses one input line.
     *
     * @param line the raw line typed by the user
     * @return the command that corresponds to the line
     * @throws CrobsException.MalformedCommandException on an unknown command or a wrong argument count
     */
    public Command parse(String line) {
        if (line == null || line.trim().isEmpty()) {
            throw new CrobsException.MalformedCommandException(CrobsMessages.EMPTY_COMMAND);
        }
        String text = line.trim();
        String name = text.split("\\s+")[0];

        if ("login".equals(name)) {
            return new LoginCommand(crobs, arguments(text, LOGIN_USAGE, name).next());
        }
        if ("logout".equals(name)) {
            requireArity(text, LOGOUT_USAGE, name);
            return new LogoutCommand(crobs);
        }
        if ("quit".equals(name)) {
            requireArity(text, QUIT_USAGE, name);
            return new QuitCommand(crobs);
        }
        if ("addUser".equals(name)) {
            return new AddUserCommand(crobs, argumentsWithTail(text, ADD_USER_USAGE, name).next());
        }
        if ("removeUser".equals(name)) {
            return new RemoveUserCommand(crobs, arguments(text, REMOVE_USER_USAGE, name).next());
        }
        if ("changeRole".equals(name)) {
            Iterator<String> args = arguments(text, CHANGE_ROLE_USAGE, name);
            return new ChangeRoleCommand(crobs, args.next(), args.next());
        }
        if ("addRoom".equals(name)) {
            Iterator<String> args = arguments(text, ADD_ROOM_USAGE, name);
            return new AddRoomCommand(crobs, args.next(), args.next(), args.next());
        }
        if ("modifyRoom".equals(name)) {
            Iterator<String> args = arguments(text, MODIFY_ROOM_USAGE, name);
            return new ModifyRoomCommand(crobs, args.next(), args.next(), args.next());
        }
        if ("deleteRoom".equals(name)) {
            return new DeleteRoomCommand(crobs, arguments(text, DELETE_ROOM_USAGE, name).next());
        }
        if ("book".equals(name)) {
            Iterator<String> args = argumentsWithTail(text, BOOK_USAGE, name);
            String roomId = args.next();
            String date = args.next();
            String startTime = args.next();
            String endTime = args.next();
            String purpose = args.next();
            return new BookCommand(crobs, roomId, date, startTime, endTime, purpose);
        }
        if ("modifyBooking".equals(name)) {
            Iterator<String> args = argumentsWithTail(text, MODIFY_BOOKING_USAGE, name);
            String bookingId = args.next();
            String roomId = args.next();
            String date = args.next();
            String startTime = args.next();
            String endTime = args.next();
            String purpose = args.next();
            return new ModifyBookingCommand(crobs, bookingId, roomId, date, startTime, endTime, purpose);
        }
        if ("deleteBooking".equals(name)) {
            return new DeleteBookingCommand(crobs, arguments(text, DELETE_BOOKING_USAGE, name).next());
        }
        if ("search".equals(name)) {
            Iterator<String> args = arguments(text, SEARCH_USAGE, name);
            String date = args.next();
            String startTime = args.next();
            String endTime = args.next();
            String minCapacity = args.next();
            String features = args.next();
            return new SearchCommand(crobs, date, startTime, endTime, minCapacity, features);
        }
        if ("schedule".equals(name)) {
            Iterator<String> args = arguments(text, SCHEDULE_USAGE, name);
            return new ScheduleCommand(crobs, args.next(), args.next());
        }
        if ("listRoom".equals(name)) {
            return new ListRoomCommand(crobs, arguments(text, LIST_ROOM_USAGE, name).next());
        }
        if ("listAllRooms".equals(name)) {
            requireArity(text, LIST_ALL_ROOMS_USAGE, name);
            return new ListAllRoomsCommand(crobs);
        }
        if ("listBookings".equals(name)) {
            requireArity(text, LIST_BOOKINGS_USAGE, name);
            return new ListBookingsCommand(crobs);
        }
        throw new CrobsException.MalformedCommandException(
                String.format(CrobsMessages.UNKNOWN_COMMAND, name));
    }

    /** The arguments of a line whose words must match the usage exactly. */
    private static Iterator<String> arguments(String text, String usage, String name) {
        requireArity(text, usage, name);
        return tail(text.split("\\s+"));
    }

    /**
     * The arguments of a line whose last argument may contain spaces, such as
     * a user name or the purpose of a booking.
     */
    private static Iterator<String> argumentsWithTail(String text, String usage, String name) {
        int count = wordCount(usage);
        String[] words = text.split("\\s+", count);
        if (words.length != count) {
            throw new CrobsException.MalformedCommandException(
                    String.format(CrobsMessages.WRONG_ARGS, name));
        }
        return tail(words);
    }

    private static void requireArity(String text, String usage, String name) {
        if (text.split("\\s+").length != wordCount(usage)) {
            throw new CrobsException.MalformedCommandException(
                    String.format(CrobsMessages.WRONG_ARGS, name));
        }
    }

    /** Drops the command word and hands out the remaining words. */
    private static Iterator<String> tail(String[] words) {
        return Arrays.asList(words).subList(1, words.length).iterator();
    }

    private static int wordCount(String usage) {
        return usage.split(" ").length;
    }
}
