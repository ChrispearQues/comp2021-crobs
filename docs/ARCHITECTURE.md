# Crobs — Code Structure

> Goal: four people can work in parallel without breaking each other. If an
> implementation contradicts this document, this document wins; change it with a
> pull request first.

## 1. Directory layout

```
src/hk/edu/polyu/comp/comp2021/crobs/
├── Application.java                   entry point: -html/-txt arguments, wiring, command loop
├── view/
│   ├── View.java                      output interface (print / printError)
│   └── ConsoleView.java               System.out only
├── controller/
│   ├── Command.java                   interface: execute()
│   ├── CommandParser.java             String -> Command; syntax only
│   ├── Dispatcher.java                single entry point: log -> parse -> execute -> print
│   └── command/                       one class per REQ (18 classes)
├── io/
│   ├── OperationLogger.java           interface: log(int index, String command)
│   ├── HtmlLogger.java                REQ1: two-column HTML table
│   ├── TxtLogger.java                 REQ1: one command per line
│   └── CompositeLogger.java           writes both files
└── model/                             ★ line coverage is measured on this package only
    ├── CROBS.java                     facade: all business methods + the logged-in user
    ├── User.java                      id, name, role
    ├── Role.java                      enum SUPERADMIN / ADMIN / REGULAR
    ├── Room.java                      id, type, capacity, features + feature parsing
    ├── RoomType.java                  enum lectureTheatre / computerLab / seminarRoom / meetingRoom
    ├── Booking.java                   id, userId, roomId, timeSlot, purpose + ordering
    ├── TimeSlot.java                  date + start/end; overlaps(TimeSlot)
    ├── IdGenerator.java               unique IDs (U / R / B prefixes)
    ├── Permission.java                every role check
    ├── DateFormats.java               strict yyyy-MM-dd and HH:mm parsing
    ├── RoomSearch.java                REQ13 filter chain
    ├── ScheduleView.java              REQ14 weekly view + user masking
    ├── BookingListView.java           REQ17 "my bookings"
    ├── CrobsMessages.java             every user-facing string
    └── CrobsException.java            base class + PermissionDenied / NotFound /
                                       Conflict / MalformedCommand

test/hk/edu/polyu/comp/comp2021/crobs/model/
├── CROBSTest.java                     every REQ through the facade
├── RoomSearchTest.java / ScheduleViewTest.java / BookingListViewTest.java
├── DateFormatsTest.java               leap day, 24:00, 9:00 ...
├── RoomTest.java / RoomTypeTest.java / BookingTest.java / TimeSlotTest.java
├── IdGeneratorTest.java / PermissionTest.java
```

## 2. Dependency direction (one way only)

```
view  ←  controller  ←  Application
             ↓
           model
```

- `model` never imports `view`, `controller` or `io`.
- `controller` calls `model` public methods only; it never touches collections.
- `view` receives ready-to-print strings and prints them; it makes no decisions.
- The only exception is `Application`, which assembles all layers.

## 3. Login state

There is exactly one place that knows who is logged in: `CROBS.currentUser`
(`null` = nobody). Permissions are checked inside the `model` so that they are
covered by the unit tests. The controller never inspects a role.

## 4. The `CROBS` facade

| Method | REQ | Returns | Throws |
|---|---|---|---|
| `login(String userId)` | 5 | — | NotFound, Malformed |
| `logout()` | 6 | — | — |
| `addUser(String name)` | 2 | `User U001 created.` | PermissionDenied, Malformed |
| `removeUser(String userId)` | 3 | — | PermissionDenied, NotFound |
| `changeRole(String userId, String role)` | 4 | — | PermissionDenied, NotFound, Malformed |
| `addRoom(String type, String capacity, String features)` | 7 | `Room R001 created.` | PermissionDenied, Malformed |
| `modifyRoom(String roomId, String field, String value)` | 8 | — | PermissionDenied, NotFound, Malformed |
| `deleteRoom(String roomId)` | 9 | — | PermissionDenied, NotFound |
| `book(String roomId, String date, String start, String end, String purpose)` | 10 | `Booking B001 created.` | PermissionDenied, Conflict, NotFound, Malformed |
| `modifyBooking(String bookingId, ...)` | 11 | — | PermissionDenied, Conflict, NotFound, Malformed |
| `deleteBooking(String bookingId)` | 12 | — | PermissionDenied, NotFound |
| `search(String date, String start, String end, String minCapacity, String features)` | 13 | `List<String>` | PermissionDenied, Malformed |
| `schedule(String roomId, String weekStartDate)` | 14 | `List<String>` | PermissionDenied, NotFound, Malformed |
| `listRoom(String roomId)` | 15 | one line | PermissionDenied, NotFound |
| `listAllRooms()` | 16 | `List<String>` | PermissionDenied |
| `listBookings()` | 17 | `List<String>` | PermissionDenied |

All formatting lives in the model, so the CLI and a future GUI print the same
text. Methods that return `void` are silent on success; the command object adds
a fixed confirmation line from `CrobsMessages`.

## 5. Output format

```
search 2026-11-16 09:00 11:00 30 projector
R002 computerLab 40 projector,whiteboard

schedule R002 2026-11-16            (regular user)
B003 2026-11-16 09:00-11:00 *** group meeting

schedule R002 2026-11-16            (admin)
B003 2026-11-16 09:00-11:00 U001 group meeting

listBookings
B003 R002 2026-11-16 09:00-11:00 group meeting
```

- `bookingId [roomId] date startTime-endTime [userId] purpose`
- `roomId` appears in `listBookings` but not in `schedule` (the command already
  names the room).
- `***` replaces a user ID that a regular user is not allowed to see.

## 6. Three rules

1. **Business decisions live in `model`.** An `if (user.getRole() == ...)` inside
   `controller` is in the wrong place: it cannot be tested and it costs coverage.
2. **Every command goes through `Dispatcher`.** REQ1 logging is written there and
   nowhere else.
3. **Exceptions are not swallowed.** The model throws `CrobsException`
   subclasses; `Dispatcher` turns them into a printable message.

## 7. Build and test

```bash
# compile
javac -d out $(find src -name "*.java")

# tests (JUnit 5 in lib/)
java -jar junit-platform-console-standalone.jar \
     execute --class-path "out:out-test" --scan-class-path

# run
java -cp out hk.edu.polyu.comp.comp2021.crobs.Application -html log.html -txt log.txt
```

SDK: JDK 25, language level 8 (no `var`, no `List.of`, no text blocks).

## 8. Code inspection (20 points)

The official ruleset is committed as `.idea/inspectionProfiles/COMP2021_PROJECT.xml`
and `profiles_settings.xml` selects it, so a clone of this repository opens in
IntelliJ IDEA with the course profile already active. To check it manually:

```
Settings | Editor | Inspections | gear | Import Profile... | COMP2021_PROJECT.xml
Analyze | Inspect Code | Whole project
```

Only the rule's **errors** cost points (1 each), never the warnings. The rules
that the code is written to satisfy:

| Rule | What it means here |
|---|---|
| `JavaDoc` | every class, field and method visible outside the class needs Javadoc, and methods need `@param` / `@return` |
| `MagicNumber` | no literal above 2 outside a constant; `IdGenerator` and `DateFormats` derive theirs (`DayOfWeek.values().length`) |
| `UnusedImport`, `MissingOverrideAnnotation`, `PackageVisibleField`, `PublicField` | no unused import, `@Override` everywhere, no non-private field without a modifier, only `static final` constants are public |
| `EmptyCatchBlock`, `EmptyClass`, `ReturnFromFinallyBlock`, ... | no empty or dead control structures |

Unit tests are not inspected, so the test sources may use literal dates freely.

## 9. Ownership

| Member | Package | REQ |
|---|---|---|
| A · ChrispearQues | `Application`, `controller`, `io` | REQ1, 4, 5, 6, 18 |
| B · LeoLiuZY | `model`: User / Room / IdGenerator | REQ2, 3, 7, 8, 9, 15, 16 |
| C · brianisbadass | `model`: Booking / TimeSlot + booking rules | REQ10, 11, 12 |
| D · nocketenyin | `model`: search / schedule / listBookings, output format, user manual | REQ13, 14, 17 |
