# Integrating modules A, B, C and D

> This note records *how* `integrate/all` was built and *who contributed what*.
> It is the input to the design review and to the peer-evaluation statement, so
> every claim below is backed by a commit; nothing is inferred from memory.

## 1. Provenance

`integrate/all` is cut from `origin/merge/d-into-dev`
(`8dcc41d`, "merge: integrate feat/design-d-query (REQ1-REQ18) into dev").
`dev` and `main` were not touched and no branch was force-pushed.

| Member | Branch | Commits that carry their work | Declared scope (README §4) |
|---|---|---|---|
| **A** ChrispearQues | `feat/module-a` | `5dd07bf` io/, `abafff2` controller/, `0188767` CROBS login+REQ4-6, `0de56a1` docs, `5f40e35`, `62855ac`, `c6d2e2c`, `1604d7c`, `b29bf2c` (CLI robustness + tests) | `Application`, `controller`, `io`, REQ1/4/5/6/18 |
| **B** LeoLiuZY | `dev` (`a06951d`) | `a06951d` "feat(partB)" — 7 command classes, `User`, `Room`, `RoomType`, `IdGenerator`, 3 tests | `model` users + rooms, REQ2/3/7/8/9/15/16 |
| **C** brianisbadness | *(none)* | **no commits on any branch** | `model` Booking / TimeSlot, REQ10/11/12 |
| **D** nocketenyin | `feat/design-d-query` | `08b2e11` (REQ1-REQ18 end to end), `e1e5d7a` (inspection rules), `ccbd594` (design note) | search / schedule / listBookings, output format, manual, REQ13/14/17 |

Facts that follow from this table:

* A's and D's implementations are two **independent** implementations of the
  same skeleton: D's `08b2e11` rewrote `Application`, `CommandParser`,
  `Dispatcher`, all 18 command classes and the four `io` classes, and deleted
  A's `controller/Session`. A's commits never reached `dev`, so this is a
  semantic merge, not a fast-forward.
* C never delivered: the REQ10-REQ12 code in this branch (booking rules in
  `CROBS`, `Booking`, `TimeSlot`) is D's implementation. It must not be
  presented as C's work at the design review.
* B's `User` work survives verbatim — see §3.

## 2. Now same package, one author each

| File | Primary author (current content) | Notes |
|---|---|---|
| `Application.java` | A + D, merged here | D's wiring (`CROBS` + `CommandParser` + `Dispatcher` + `ConsoleView`); A's command-line validation, exit status and logger lifecycle |
| `controller/CommandParser.java` | A + D, merged here | D's usage templates + tail arguments; A's tokenizer and case-insensitive command words |
| `controller/Dispatcher.java` | A (flow) / D (final text) | `log -> parse -> execute -> print` is A's design; D added `CrobsMessages` wording and the `getCounter()` accessor |
| `controller/command/*.java` (18) | A scaffold, B 7 classes, D final wiring | All 18 now call one `CROBS` method and return its text |
| `io/*.java` (4) | A + D, merged here | See §3 |
| `view/View.java`, `view/ConsoleView.java` | A | D added Javadoc only |
| `model/CROBS.java` | A (REQ4/5/6 + `Permission`), D (REQ1-REQ18 facade) | One facade; `currentUser` is the only login state |
| `model/User.java` | **B** | constructor validation, `isAdmin()`, `toString()` are B's (`a06951d`), unchanged; D added Javadoc (`e1e5d7a`) |
| `model/Room.java`, `RoomType.java`, `IdGenerator.java` | B first, D rewrote/extended | B's feature parsing and ID sequence survive; D added the inspection rules (strict parsing, constants) |
| `model/Permission.java`, `Role.java`, `CrobsException.java`, `CrobsMessages.java` | A first (permission model, exceptions), D extended | `CrobsMessages` (every user-facing string) is D's |
| `model/Booking.java`, `TimeSlot.java` | A scaffold, **D** implementation | C's declared scope, never delivered |
| `model/DateFormats.java`, `RoomSearch.java`, `ScheduleView.java`, `BookingListView.java` | D | REQ13/14/17 |
| `test/.../model/*` (12 classes) | D, except B's `UserTest` | `UserTest` is B's own suite (`a06951d`), kept as is |
| `test/.../ApplicationOptionsTest.java`, `controller/CommandParserTest.java`, `io/LoggerTest.java` | **A**, ported here | See §4 |
| `README.md` | A | ownership table, timeline, definition of done |
| `docs/ARCHITECTURE.md`, `docs/DESIGN-D-query.md` | D | A wrote the first ARCHITECTURE; D rewrote both to match the code |

## 3. Duplicate capabilities: what was merged and whose behaviour won

| Capability | Module A | Module D | Adopted |
|---|---|---|---|
| **Command-line parsing** | `parseOptions` loop with arity check, `Options` record, testable | `for (i += 2)` pair loop, `log.txt`/`log.html` defaults, silently ignores anything else | **A's validation** on **D's wiring**: missing name, unknown option, same normalised path for both logs are errors; only the files named on the command line are written |
| **Exit status** | 0 normal, 1 for usage/log-file errors, no stack trace | always 0; `UncheckedIOException` printed as a stack trace | **A's** |
| **Logger lifecycle** | `OperationLogger.close()`, `HtmlLogger.close()` writes the footer, half-open file closed when the second logger fails | no `close()` at all | **A's** contract (`default close()`), **D's** implementations |
| **TXT log shape** | buffered writer, `index + " " + command`, truncate on open | whole file rewritten per line, **no index**, truncate on open | **D's** rewrite model (no handle held, file always complete) + **A's** numbering |
| **HTML log shape** | streamed header at open, footer at close, `escape` of `& < > "` | full document rewritten per line, `escape` of `& < >` only | **D's** document (DOCTYPE, charset, `border`) and rewrite model + **A's** quote escaping |
| **Tokenizer** | static `tokenize`, double quotes keep a span together, unterminated quote rejected | `String.split("\\s+")` only; `addUser`/`book`/`modifyBooking` glue the rest of the line into the last argument | **A's tokenizer** + **D's** usage-driven arity and tail arguments, so both `book ... "Group meeting"` and `book ... Group meeting` work |
| **Command words** | case-insensitive (`LOGIN`) | case-sensitive | **A's** (`Locale.ROOT`) |
| **Error wording** | ad-hoc strings in the parser | every string in `CrobsMessages` | **D's** |
| **Login state** | `controller/Session` wrapper over `CROBS` | `CROBS.currentUser` only (`Session` deleted) | **D's** — one source of truth; A's wrapper is not resurrected |
| **REQ4/5/6 rules** | stricter: login is refused while somebody is logged in, `logout` without login is an error, "cannot promote to superadmin" | permissive: a `login` may switch user, `logout` without login is a no-op, `parseRole` only accepts `admin`/`regular` | **D's**, because its tests state these as intended behaviour; A's variant is kept on `feat/module-a` (§6, item 1) |

## 4. Defects that module A's tests found in module D's code

Before this branch, the baseline (`8dcc41d`) was measured by running it, not by
reading it. Each row is a defect in D's code, the observable baseline behaviour,
the ported A test that catches it, and the fix.

| # | File | Defect / baseline behaviour | Test (from A) | Fix |
|---|---|---|---|---|
| 1 | `io/TxtLogger` | the log line is the bare command — `quit` instead of `1 quit`; REQ1 numbering lost | `txtLoggerWritesOneNumberedLinePerCommand` | `72f4c71` |
| 2 | `io/HtmlLogger` | `escape` leaves `"` raw, so `addUser "A & B"` puts an unescaped quote inside `<td>` | `htmlLoggerEscapesHtmlMetacharacters` | `72f4c71` |
| 3 | `io/OperationLogger` | no `close()`; loggers can never be flushed or closed, and a failing close cannot be reported | `compositeLoggerFansOutToEveryLogger`, `htmlLoggerCloseIsIdempotent` | `72f4c71` |
| 4 | `Application` | `-txt` with no file name is silently ignored: exit **0**, and the run still writes the default `log.txt` + `log.html` | `rejectsAMissingFileName` | `62ef941` |
| 5 | `Application` | unknown option (`-foo bar`) is silently ignored: exit **0** | `rejectsAnUnknownOption` | `62ef941` |
| 6 | `Application` | `-txt <directory>` dumps `UncheckedIOException` + stack trace on stderr | CLI check (§5) | `62ef941` |
| 7 | `Application` | `-txt x -html ./x` is accepted (exit 0) and the two writers destroy the file — the result was plain text with the HTML table gone | `rejectsTheSameFileForBothLogs`, `rejectsTheSameFileWrittenInTwoSpellings` | `62ef941` |
| 8 | `Application` | `-txt only.txt` also creates an unsolicited `log.html` | CLI check (§5) | `62ef941` |
| 9 | `CommandParser` | no tokenizer, so a quoted purpose (`book ... "Group meeting"`) is split and rejected as 7 arguments | `tokenizerKeepsQuotedArgumentTogether`, `quotedPurposeCountsAsOneArgument` | `9e15fbc` |
| 10 | `CommandParser` | an unterminated quote is not detected (no tokenizer existed) | `tokenizerRejectsAnUnterminatedQuote` | `9e15fbc` |
| 11 | `CommandParser` | command words are case-sensitive (`LOGIN U000` → unknown command) | `commandNamesAreCaseInsensitive` | `9e15fbc` |

Three A assertions were **not** forced onto D's design; the tests were adapted
and the reason is a comment in the test:

* error texts now come from `CrobsMessages` ("Error: unknown command \"fly\"."
  instead of A's "Unknown command: fly") — D owns the wording;
* the HTML assertion checks D's full document (`<!DOCTYPE html>`, `<table>`,
  `Index` column) instead of A's one-line `<table>` header;
* `List.of` was replaced with `Arrays.asList` because the project compiles at
  language level 8 (see §6, item 4).

Two A tests were extended (a dangling `-html` after a complete `-txt`; the same
file spelled `logs/../out.log` vs `out.log`) and one test was added
(`unquotedTailStillCountsAsOneArgument`) so that D's tail semantics cannot
regress while A's tokenizer is in place.

## 5. Test suite and verification

| | Tests | Result |
|---|---|---|
| Baseline `8dcc41d` | 149 | 149 green |
| `integrate/all` | **174** (+25: Application 7, CommandParser 11, Logger 7) | 174 green |

```bash
export JAVA_HOME=/opt/homebrew/opt/openjdk@25
export PATH="$JAVA_HOME/bin:$PATH"

javac -Xlint:all -d out -cp "lib/*" $(find src -name '*.java')      # 0 error, 0 warning
javac -Xlint:all --release 8 -d /tmp/r8 -cp "lib/*" $(find src -name '*.java')
                                                                    # language level 8 holds
javac -Xlint:all -d tout -cp "out:lib/*" $(find test -name '*.java')
java -jar junit-platform-console-standalone-1.14.0.jar execute \
     -cp "out:tout:lib/*" --scan-classpath=tout --details=summary   # 174/174
```

CLI smoke run on the integrated build (`U001` is the generated superadmin ID,
see §6 item 1):

| Command | Expected | Observed |
|---|---|---|
| `login U001` / `addUser Alice Smith` / `changeRole U002 admin` / `logout` / `login U002` / `quit` with `-txt` + `-html` | both logs, numbered 1..6 | both logs, `1 login U001` … `6 quit`, HTML rows match |
| same run without options | no log files created | `ls -A` empty, exit 0 |
| `-txt` (no file name) | usage, exit 1 | `Missing file name after -txt` + usage, exit 1, no `log.txt`/`log.html` |
| `-foo bar` | usage, exit 1 | `Unknown option: -foo` + usage, exit 1 |
| `-txt <directory>` | message, exit 1, no stack trace | `Error: Cannot write the TXT log /tmp`, exit 1, 0 stack frames |
| `-txt same.log -html ./same.log` | rejected, exit 1, file not created | rejected, exit 1, no file |
| `addUser "A & B <x>"` with `-html` | escaped cell | `&quot;A &amp; B &lt;x&gt;&quot;` |
| second run on the same paths | old content replaced | TXT `1 quit`, one HTML row |

## 6. Open questions (need a human decision before submission)

1. **Superadmin ID.** D generates the superadmin as `U001` (first ID of
   `IdGenerator("U")`); A hard-codes `U000` with the comment "its ID stays U000
   on every run". The assignment text is not in this repository, so the merge
   keeps D's behaviour; the tests, `ARCHITECTURE.md` ("User U002 created.") and
   the CLI smoke runs all assume `U001`. If the spec says `U000`, change
   `CROBS()` plus the fixtures in `CROBSTest` — one place, no controller change.
2. **Default log files.** The baseline always wrote `log.txt`/`log.html`; the
   integrated branch writes only what `-txt`/`-html` name, and no option means
   no log. If the grader starts the program without options and expects logs,
   restore two defaults in `main()`; A's tests deliberately assert that an
   absent option writes nothing.
3. **REQ4/5/6 policy.** A refuses `login` while somebody is logged in and treats
   `logout` without login as an error; D allows switching user by `login` and a
   no-op `logout`. D's behaviour is what its tests assert, so it was kept.
   Decide once, then make both implementations and the user manual agree.
4. **Language level.** `ARCHITECTURE.md` §7 requires level 8, but A's original
   `Application` used a record, arrow switches and `Path.of` (Java 11+), which
   would not compile in an IDE set to level 8. The merged `Application` uses a
   static nested class and `Paths.get`; `src/` now compiles with `--release 8`.
   `crobs.iml` sets no language level — set it explicitly to avoid surprises.
5. **Module C's contribution.** C has no commits; REQ10-REQ12 are D's code.
   The peer-evaluation statement (8%) and the design-review division of work
   must reflect that.
6. **Log cosmetics.** A's table header was `No.`, D's is `Index`; D's document
   was kept. Neither logger records timestamps — REQ1 does not ask for them,
   but say so in the user manual if a reviewer asks.
