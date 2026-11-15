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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

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
     * Parses one input line. Command words are case-insensitive.
     *
     * @param line the raw line typed by the user
     * @return the command that corresponds to the line
     * @throws CrobsException.MalformedCommandException on an unknown command, a wrong
     *         argument count or an unterminated quote
     */
    public Command parse(String line) {
        if (line == null || line.trim().isEmpty()) {
            throw new CrobsException.MalformedCommandException(CrobsMessages.EMPTY_COMMAND);
        }
        List<String> tokens = tokenize(line.trim());
        String name = tokens.get(0).toLowerCase(Locale.ROOT);

        if ("login".equals(name)) {
            return new LoginCommand(crobs, arguments(tokens, LOGIN_USAGE, name).next());
        }
        if ("logout".equals(name)) {
            requireArity(tokens, LOGOUT_USAGE, name);
            return new LogoutCommand(crobs);
        }
        if ("quit".equals(name)) {
            requireArity(tokens, QUIT_USAGE, name);
            return new QuitCommand(crobs);
        }
        if ("adduser".equals(name)) {
            return new AddUserCommand(crobs, argumentsWithTail(tokens, ADD_USER_USAGE, name).next());
        }
        if ("removeuser".equals(name)) {
            return new RemoveUserCommand(crobs, arguments(tokens, REMOVE_USER_USAGE, name).next());
        }
        if ("changerole".equals(name)) {
            Iterator<String> args = arguments(tokens, CHANGE_ROLE_USAGE, name);
            return new ChangeRoleCommand(crobs, args.next(), args.next());
        }
        if ("addroom".equals(name)) {
            Iterator<String> args = arguments(tokens, ADD_ROOM_USAGE, name);
            return new AddRoomCommand(crobs, args.next(), args.next(), args.next());
        }
        if ("modifyroom".equals(name)) {
            Iterator<String> args = arguments(tokens, MODIFY_ROOM_USAGE, name);
            return new ModifyRoomCommand(crobs, args.next(), args.next(), args.next());
        }
        if ("deleteroom".equals(name)) {
            return new DeleteRoomCommand(crobs, arguments(tokens, DELETE_ROOM_USAGE, name).next());
        }
        if ("book".equals(name)) {
            Iterator<String> args = argumentsWithTail(tokens, BOOK_USAGE, name);
            String roomId = args.next();
            String date = args.next();
            String startTime = args.next();
            String endTime = args.next();
            String purpose = args.next();
            return new BookCommand(crobs, roomId, date, startTime, endTime, purpose);
        }
        if ("modifybooking".equals(name)) {
            Iterator<String> args = argumentsWithTail(tokens, MODIFY_BOOKING_USAGE, name);
            String bookingId = args.next();
            String roomId = args.next();
            String date = args.next();
            String startTime = args.next();
            String endTime = args.next();
            String purpose = args.next();
            return new ModifyBookingCommand(crobs, bookingId, roomId, date, startTime, endTime, purpose);
        }
        if ("deletebooking".equals(name)) {
            return new DeleteBookingCommand(crobs,
                    arguments(tokens, DELETE_BOOKING_USAGE, name).next());
        }
        if ("search".equals(name)) {
            Iterator<String> args = arguments(tokens, SEARCH_USAGE, name);
            String date = args.next();
            String startTime = args.next();
            String endTime = args.next();
            String minCapacity = args.next();
            String features = args.next();
            return new SearchCommand(crobs, date, startTime, endTime, minCapacity, features);
        }
        if ("schedule".equals(name)) {
            Iterator<String> args = arguments(tokens, SCHEDULE_USAGE, name);
            return new ScheduleCommand(crobs, args.next(), args.next());
        }
        if ("listroom".equals(name)) {
            return new ListRoomCommand(crobs, arguments(tokens, LIST_ROOM_USAGE, name).next());
        }
        if ("listallrooms".equals(name)) {
            requireArity(tokens, LIST_ALL_ROOMS_USAGE, name);
            return new ListAllRoomsCommand(crobs);
        }
        if ("listbookings".equals(name)) {
            requireArity(tokens, LIST_BOOKINGS_USAGE, name);
            return new ListBookingsCommand(crobs);
        }
        throw new CrobsException.MalformedCommandException(
                String.format(CrobsMessages.UNKNOWN_COMMAND, tokens.get(0)));
    }

    /**
     * Splits on whitespace, but keeps a double-quoted span together, so that a
     * booking purpose such as {@code "Group meeting"} stays one argument.
     *
     * <p>Package-private so that the tokenizer can be tested without faking a
     * whole REPL.</p>
     *
     * @param line one input line, already trimmed
     * @return the words of the line, in order; never empty for a non-blank line
     * @throws CrobsException.MalformedCommandException when a quote is left open
     */
    static List<String> tokenize(String line) {
        List<String> tokens = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        boolean inQuotes = false;
        boolean started = false;

        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            if (c == '"') {
                inQuotes = !inQuotes;
                started = true;
            } else if (Character.isWhitespace(c) && !inQuotes) {
                if (started) {
                    tokens.add(current.toString());
                    current.setLength(0);
                    started = false;
                }
            } else {
                current.append(c);
                started = true;
            }
        }
        if (inQuotes) {
            throw new CrobsException.MalformedCommandException(CrobsMessages.UNTERMINATED_QUOTE);
        }
        if (started) {
            tokens.add(current.toString());
        }
        return tokens;
    }

    /** The arguments of a line whose words must match the usage exactly. */
    private static Iterator<String> arguments(List<String> tokens, String usage, String name) {
        requireArity(tokens, usage, name);
        return tokens.subList(1, tokens.size()).iterator();
    }

    /**
     * The arguments of a line whose last argument may contain spaces, such as a
     * user name or the purpose of a booking: the words after the fixed ones are
     * joined back into one argument.
     */
    private static Iterator<String> argumentsWithTail(List<String> tokens, String usage, String name) {
        int count = wordCount(usage);
        if (tokens.size() < count) {
            throw wrongArgs(name);
        }
        List<String> args = new ArrayList<>(tokens.subList(1, count - 1));
        args.add(String.join(" ", tokens.subList(count - 1, tokens.size())));
        return args.iterator();
    }

    private static void requireArity(List<String> tokens, String usage, String name) {
        if (tokens.size() != wordCount(usage)) {
            throw wrongArgs(name);
        }
    }

    private static CrobsException.MalformedCommandException wrongArgs(String name) {
        return new CrobsException.MalformedCommandException(
                String.format(CrobsMessages.WRONG_ARGS, name));
    }

    private static int wordCount(String usage) {
        return usage.split(" ").length;
    }
}
