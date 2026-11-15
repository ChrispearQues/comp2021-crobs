package hk.edu.polyu.comp.comp2021.crobs.controller;

import hk.edu.polyu.comp.comp2021.crobs.controller.command.BookCommand;
import hk.edu.polyu.comp.comp2021.crobs.controller.command.LoginCommand;
import hk.edu.polyu.comp.comp2021.crobs.controller.command.LogoutCommand;
import hk.edu.polyu.comp.comp2021.crobs.controller.command.QuitCommand;
import hk.edu.polyu.comp.comp2021.crobs.model.CROBS;
import hk.edu.polyu.comp.comp2021.crobs.model.CrobsException;
import org.junit.jupiter.api.Test;

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
        assertEquals(List.of("login", "U000"), CommandParser.tokenize("  login    U000  "));
    }

    @Test
    void tokenizerAcceptsTabs() {
        assertEquals(List.of("login", "U000"), CommandParser.tokenize("\tlogin\tU000"));
    }

    @Test
    void tokenizerKeepsAnEmptyQuotedArgument() {
        assertEquals(List.of("addUser", ""), CommandParser.tokenize("addUser \"\""));
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
    void unknownCommandIsRejected() {
        CrobsException.MalformedCommandException e = assertThrows(
                CrobsException.MalformedCommandException.class, () -> parser.parse("fly to Mars"));
        assertEquals("Unknown command: fly", e.getMessage());
    }

    @Test
    void wrongNumberOfArgumentsIsRejected() {
        assertThrows(CrobsException.MalformedCommandException.class, () -> parser.parse("login"));
        assertThrows(CrobsException.MalformedCommandException.class, () -> parser.parse("login U000 extra"));
        assertThrows(CrobsException.MalformedCommandException.class, () -> parser.parse("quit now"));
        assertThrows(CrobsException.MalformedCommandException.class, () -> parser.parse("addRoom R1 10"));
    }
}
