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

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/** Turns a raw input line into a Command object. Only syntax checks belong here. */
public class CommandParser {

    private final CROBS crobs;

    public CommandParser(CROBS crobs) {
        this.crobs = crobs;
    }

    /** @throws CrobsException.MalformedCommandException on unknown commands or a wrong argument count. */
    public Command parse(String line) {
        List<String> tokens = tokenize(line);
        if (tokens.isEmpty()) {
            throw new CrobsException.MalformedCommandException("Empty command");
        }

        String name = tokens.get(0).toLowerCase(Locale.ROOT);
        String[] args = tokens.subList(1, tokens.size()).toArray(new String[0]);

        return switch (name) {
            case "login" -> {
                expect(args, 1, "login <userId>");
                yield new LoginCommand(crobs, args[0]);
            }
            case "logout" -> {
                expect(args, 0, "logout");
                yield new LogoutCommand(crobs);
            }
            case "quit" -> {
                expect(args, 0, "quit");
                yield new QuitCommand();
            }
            case "adduser" -> {
                expect(args, 1, "addUser <name>");
                yield new AddUserCommand(crobs, args[0]);
            }
            case "removeuser" -> {
                expect(args, 1, "removeUser <userId>");
                yield new RemoveUserCommand(crobs, args[0]);
            }
            case "changerole" -> {
                expect(args, 2, "changeRole <userId> <role>");
                yield new ChangeRoleCommand(crobs, args[0], args[1]);
            }
            case "addroom" -> {
                expect(args, 3, "addRoom <type> <capacity> <features>");
                yield new AddRoomCommand(crobs, args[0], args[1], args[2]);
            }
            case "modifyroom" -> {
                expect(args, 3, "modifyRoom <roomId> <field> <value>");
                yield new ModifyRoomCommand(crobs, args[0], args[1], args[2]);
            }
            case "deleteroom" -> {
                expect(args, 1, "deleteRoom <roomId>");
                yield new DeleteRoomCommand(crobs, args[0]);
            }
            case "book" -> {
                expect(args, 5, "book <roomId> <date> <startTime> <endTime> <purpose>");
                yield new BookCommand(crobs, args[0], args[1], args[2], args[3], args[4]);
            }
            case "modifybooking" -> {
                expect(args, 6, "modifyBooking <bookingId> <roomId> <date> <startTime> <endTime> <purpose>");
                yield new ModifyBookingCommand(crobs, args[0], args[1], args[2], args[3], args[4], args[5]);
            }
            case "deletebooking" -> {
                expect(args, 1, "deleteBooking <bookingId>");
                yield new DeleteBookingCommand(crobs, args[0]);
            }
            case "search" -> {
                expect(args, 5, "search <date> <startTime> <endTime> <minCapacity> <features>");
                yield new SearchCommand(crobs, args[0], args[1], args[2], args[3], args[4]);
            }
            case "schedule" -> {
                expect(args, 2, "schedule <roomId> <weekStartDate>");
                yield new ScheduleCommand(crobs, args[0], args[1]);
            }
            case "listroom" -> {
                expect(args, 1, "listRoom <roomId>");
                yield new ListRoomCommand(crobs, args[0]);
            }
            case "listallrooms" -> {
                expect(args, 0, "listAllRooms");
                yield new ListAllRoomsCommand(crobs);
            }
            case "listbookings" -> {
                expect(args, 0, "listBookings");
                yield new ListBookingsCommand(crobs);
            }
            default -> throw new CrobsException.MalformedCommandException(
                    "Unknown command: " + tokens.get(0));
        };
    }

    private static void expect(String[] args, int count, String usage) {
        if (args.length != count) {
            throw new CrobsException.MalformedCommandException(
                    "Wrong number of arguments. Usage: " + usage);
        }
    }

    /**
     * Splits on whitespace, but keeps double-quoted arguments together so that a
     * booking purpose such as {@code "Group meeting"} stays one argument.
     */
    private static List<String> tokenize(String line) {
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
            throw new CrobsException.MalformedCommandException("Unterminated quoted argument");
        }
        if (started) {
            tokens.add(current.toString());
        }
        return tokens;
    }
}
