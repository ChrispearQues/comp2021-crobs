package hk.edu.polyu.comp.comp2021.crobs.controller;

import hk.edu.polyu.comp.comp2021.crobs.controller.command.AddUserCommand;
import hk.edu.polyu.comp.comp2021.crobs.controller.command.BookCommand;
import hk.edu.polyu.comp.comp2021.crobs.controller.command.LoginCommand;
import hk.edu.polyu.comp.comp2021.crobs.controller.command.LogoutCommand;
import hk.edu.polyu.comp.comp2021.crobs.controller.command.QuitCommand;
import hk.edu.polyu.comp.comp2021.crobs.model.CROBS;
import hk.edu.polyu.comp.comp2021.crobs.model.CrobsException;
import hk.edu.polyu.comp.comp2021.crobs.model.CrobsMessages;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

/** Syntax level checks only: the parser must never touch the model. */
class CommandParserTest {

    private final CROBS crobs = new CROBS();
    private final CommandParser parser = new CommandParser(crobs);

    // --- tokenizer -------------------------------------------------------

    @Test
    void tokenizerKeepsQuotedArgumentTogether() {
        List<String> tokens = CommandParser.tokenize("book R1 2026-11-20 09:00 10:00 \"Group meeting\"");

        assertEquals(6, tokens.size());
        assertEquals("Group meeting", tokens.get(5));
    }

    @Test
    void tokenizerSqueezesRunsOfWhitespace() {
        assertEquals(Arrays.asList("login", "U000"), CommandParser.tokenize("  login    U000  "));
    }

    @Test
    void tokenizerAcceptsTabs() {
        assertEquals(Arrays.asList("login", "U000"), CommandParser.tokenize("\tlogin\tU000"));
    }

    @Test
    void tokenizerKeepsAnEmptyQuotedArgument() {
        assertEquals(Arrays.asList("addUser", ""), CommandParser.tokenize("addUser \"\""));
    }

    @Test
    void tokenizerRejectsAnUnterminatedQuote() {
        assertThrows(CrobsException.MalformedCommandException.class,
                () -> CommandParser.tokenize("book R1 x \"not closed"));
    }

    // --- parse -----------------------------------------------------------

    @Test
    void emptyLineIsMalformed() {
        assertThrows(CrobsException.MalformedCommandException.class, () -> parser.parse(""));
        assertThrows(CrobsException.MalformedCommandException.class, () -> parser.parse("   "));
        assertThrows(CrobsException.MalformedCommandException.class, () -> parser.parse(null));
    }

    @Test
    void commandNamesAreCaseInsensitive() {
        assertInstanceOf(LoginCommand.class, parser.parse("LOGIN U000"));
        assertInstanceOf(LogoutCommand.class, parser.parse("LogOut"));
        assertInstanceOf(QuitCommand.class, parser.parse("QUIT"));
    }

    @Test
    void quotedPurposeCountsAsOneArgument() {
        assertInstanceOf(BookCommand.class,
                parser.parse("book R1 2026-11-20 09:00 10:00 \"Group meeting\""));
    }

    @Test
    void unquotedTailStillCountsAsOneArgument() {
        // The parser joins everything after the fixed arguments back together, so a
        // name or purpose may be typed without quotes (the design this branch keeps).
        assertInstanceOf(AddUserCommand.class, parser.parse("addUser John Smith"));
        assertInstanceOf(BookCommand.class,
                parser.parse("book R1 2026-11-20 09:00 10:00 Group meeting"));
    }

    @Test
    void unknownCommandIsRejected() {
        // Module D puts every message in CrobsMessages, so the wording differs from
        // the one module A's parser used ("Unknown command: fly").
        CrobsException.MalformedCommandException e = assertThrows(
                CrobsException.MalformedCommandException.class, () -> parser.parse("fly to Mars"));
        assertEquals(String.format(CrobsMessages.UNKNOWN_COMMAND, "fly"), e.getMessage());
    }

    @Test
    void wrongNumberOfArgumentsIsRejected() {
        assertThrows(CrobsException.MalformedCommandException.class, () -> parser.parse("login"));
        assertThrows(CrobsException.MalformedCommandException.class, () -> parser.parse("login U000 extra"));
        assertThrows(CrobsException.MalformedCommandException.class, () -> parser.parse("quit now"));
        assertThrows(CrobsException.MalformedCommandException.class, () -> parser.parse("addRoom R1 10"));
        assertThrows(CrobsException.MalformedCommandException.class, () -> parser.parse("addUser"));
    }
}
