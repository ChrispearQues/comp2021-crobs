# Module D — Query and View (REQ13 / REQ14 / REQ17)

> Owner: nocketenyin. This document describes the code that is actually in the
> repository; if it ever disagrees with `docs/ARCHITECTURE.md`, the architecture
> document wins and this one is updated.

## 0. Specification

**[REQ13] (3.5 points)** searching for available rooms at a specific timeslot
with certain desired features.

```
Command: search date startTime endTime minCapacity features
Effect:  Lists all rooms that have capacity greater than or equal to minCapacity,
         contain all required features, and are available during the specified
         time slot.
```

**[REQ14] (3.5 points)** viewing the weekly schedule for a specific room.

```
Command: schedule roomId weekStartDate
Effect:  Lists all active bookings for the specified room in the 7-day period
         starting from weekStartDate, sorted by date and start time. Regular users
         can only see the userID corresponding to a booking if it is their own
         booking. Admin users can see the userIDs corresponding to all bookings.
```

**[REQ17] (1.5 points)** listing all bookings made by the current user.

```
Command: listBookings
Effect:  Lists all bookings made by the current user, sorted by date and start
         time.
```

Two global constraints shape the code:

- Language level 8 (Section 12), so no `var`, no `List.of`, no text blocks.
  `java.time` is available and is used for every date and time decision.
- BON2 explicitly excludes `search` and `listBookings`, so neither command
  implements undo.

## 1. Files

| File | Role |
|---|---|
| `model/DateFormats.java` | strict `yyyy-MM-dd` / `HH:mm` parsing and the seven-day window |
| `model/RoomSearch.java` | REQ13 filter chain |
| `model/ScheduleView.java` | REQ14 weekly view and user masking |
| `model/BookingListView.java` | REQ17 "my bookings" |
| `model/CrobsMessages.java` | every user-facing string, shared by the whole group |
| `model/Booking.java` | `describeWithUser`, `describeWithRoom`, `chronological()` — the two line formats and the sort order |
| `model/CROBS.java` | `search`, `schedule`, `listBookings` delegate after `Permission.requireLogin` |
| `controller/command/SearchCommand.java` | joins the result lines and returns them |
| `controller/command/ScheduleCommand.java` | same |
| `controller/command/ListBookingsCommand.java` | same |
| `test/.../model/DateFormatsTest.java` | 15 cases |
| `test/.../model/RoomSearchTest.java` | 15 cases |
| `test/.../model/ScheduleViewTest.java` | 13 cases |
| `test/.../model/BookingListViewTest.java` | 5 cases |

Module D does **not** own `Application`, `CommandParser`, `Dispatcher`, `Session`
or `Permission`; those belong to module A and are only read here.

The three command classes contain no logic beyond `String.join("\n", ...)`, so
the whole module is reachable from the model tests.

## 2. Design

### 2.1 `DateFormats`

```java
static LocalDate parseDate(String text)                    // throws Malformed
static LocalTime parseTime(String text)                    // throws Malformed
static void      requireOrdered(LocalTime start, LocalTime end)  // start >= end throws
static List<LocalDate> sevenDaysFrom(LocalDate start)      // start .. start + 6
static LocalDate weekEnd(LocalDate start)                  // the seventh day
```

`DateTimeFormatter.ofPattern("uuuu-MM-dd").withResolverStyle(STRICT)` is used on
purpose: `yyyy` with `STRICT` fails on the missing era field, and the non-strict
style would silently turn `2026-02-29` into `2026-02-28`. `HH:mm` with `STRICT`
is what rejects `24:00` and `9:00`.

The week length is derived from `DayOfWeek.values().length` instead of a literal,
because the course inspection rule treats an unexplained number as an error.

### 2.2 `RoomSearch` (REQ13)

```java
RoomSearch(Collection<Room> rooms, Collection<Booking> bookings)
List<String> search(String date, String start, String end, String minCapacity, String features)
```

Filter chain, in this order, so that a cheap test rejects a room before an
expensive one:

1. parse the date, both times, and require `start < end`;
2. `minCapacity` must parse as an integer and be `>= 0`;
3. the feature requirement is split on commas, trimmed and lower-cased; `-` or an
   empty text means "no requirement";
4. keep the room when `capacity >= minCapacity`, when it contains **every**
   requested feature, and when no booking of that room overlaps the slot;
5. sort the survivors by room ID;
6. return the lines, or the single line `No matching room.` when nothing is left.

Availability reuses `TimeSlot.overlaps`, which is owned by the booking module, so
the definition of a conflict exists exactly once in the code base. Touching
endpoints do not overlap: `09:00-11:00` and `11:00-13:00` can both exist.

### 2.3 `ScheduleView` (REQ14)

```java
ScheduleView(Map<String, Room> rooms, Collection<Booking> bookings)
List<String> week(String roomId, String weekStartDate, User viewer)
```

1. the room is looked up whatever the case of the ID, so `schedule r001` answers
   the same as `listRoom r001`; an unknown ID throws `NotFoundException`;
2. the window is `[weekStartDate, weekStartDate + 6]`, both ends included, so the
   eighth day never appears;
3. keep only the bookings of that room inside the window — "active" means "not
   deleted", and a deleted booking is removed from the map, so no extra state is
   needed;
4. sort by date, then start time, then booking ID, so two runs always print the
   same lines in the same order;
5. the owner is printed as it is for an admin, otherwise only for their own
   booking, otherwise as `***`. `Permission.isAdmin` counts the superadmin as an
   admin;
6. an empty week prints `No booking in this week.`

### 2.4 `BookingListView` (REQ17)

```java
BookingListView(Collection<Booking> bookings)
List<String> of(User owner)
```

Only the bookings whose `userId` equals the owner's ID are kept — **also for an
admin**, which is the one place where REQ17 differs from REQ14. The sort order is
shared with `ScheduleView`, and the line format puts the room ID second because
the command did not name a room. An empty result prints `No booking.`

### 2.5 One place per format

`Booking.describeWithUser` and `Booking.describeWithRoom` own the two line
formats, and `Booking.chronological()` owns the sort order. REQ14 and REQ17
therefore cannot drift apart, and moving the format into the model keeps it
reachable from the tests.

## 3. Output format

Real output of the program:

```
search 2026-11-16 09:00 11:00 30 projector
R001 computerLab 40 projector,whiteboard

search 2026-11-16 09:00 11:00 500 projector
No matching room.
```

```
schedule R001 2026-11-16            # as the regular user U002
B001 2026-11-16 09:00-11:00 U002 group meeting
B002 2026-11-18 14:00-16:00 *** consultation

schedule R001 2026-11-16            # as an admin
B001 2026-11-16 09:00-11:00 U002 group meeting
B002 2026-11-18 14:00-16:00 U007 consultation
```

```
listBookings
B001 R001 2026-11-16 09:00-11:00 group meeting
B002 R002 2026-11-20 10:00-12:00 study group
```

- room line: `roomId roomType capacity features`, where an empty feature set is
  printed as `-`;
- `schedule` line: `bookingId date startTime-endTime userId purpose` — no room ID,
  because the command already named the room;
- `listBookings` line: `bookingId roomId date startTime-endTime purpose` — no user
  ID, because every line belongs to the caller.

## 4. Input validation

| Input | Example | Result | Checked in |
|---|---|---|---|
| date | `2026-13-01`, `26-11-16`, `abc` | `MalformedCommandException` | `DateFormats.parseDate` |
| time | `9:00`, `24:00`, `0900` | `MalformedCommandException` | `DateFormats.parseTime` |
| somewhere `start >= end` | `11:00 11:00` | `MalformedCommandException` | `DateFormats.requireOrdered` |
| minCapacity | `-5`, `abc`, `3.5` | `MalformedCommandException` | `RoomSearch` |
| features | `a,,b` | empty tokens ignored | `Room.parseFeatures` |
| unknown room | `R999` | `NotFoundException` | `ScheduleView.week` |
| no user logged in | — | `PermissionDeniedException` | `CROBS` via `Permission.requireLogin` |
| wrong argument count | `search 2026-11-16 09:00` | `MalformedCommandException` | `CommandParser` |

Semantic validation stays in the model: `CommandParser` only splits the line and
compares the word count with the usage template, so every value check is covered
by a unit test.

## 5. Design decisions worth defending

1. **Reusing `TimeSlot.overlaps` instead of comparing intervals again** — one
   definition of "conflict" for booking and searching; the two can never disagree.
2. **Formatting in `model`, not in the command** — the CLI and a future GUI print
   the same text, and the format is unit-tested rather than eyeballed.
3. **Three-level sort with the booking ID as tie-breaker** — without it, two
   bookings of the same room at the same time could swap between runs and make the
   output untestable. (They cannot exist with the same room and slot, but they can
   exist in different rooms, which matters for REQ17.)
4. **`***` instead of hiding the line** — REQ14 says a regular user cannot see the
   *userID*, not that the booking is secret, so the booking stays visible.
5. **`LocalDate` / `LocalTime` internally** — leap years and month lengths are
   handled by the JDK; a hand-written validator would need its own tests.
6. **Empty results return one line of text instead of an empty list** — the user
   gets feedback, and the command stays a one-liner.
7. **`CrobsMessages` carries no `Error: ` prefix** — the `Dispatcher` adds it once
   for every exception, whoever threw it, so the prefix cannot double up.
8. **Room IDs are matched case-insensitively in REQ14** — the facade already
   upper-cases IDs before a lookup, and a view that compared exactly would make
   `schedule r001` fail while `listRoom r001` succeeded.

## 6. Tests

```bash
# from the project root, JUnit 5 jars are in lib/
javac -d out $(find src -name "*.java")
javac -d out-test -cp "out:lib/*" $(find test -name "*.java")
java -jar junit-platform-console-standalone.jar execute \
     --class-path "out:out-test" --scan-class-path
```

| Test class | Cases | Covers |
|---|---|---|
| `DateFormatsTest` | 15 | leap day, month 13, two-digit year, `24:00`, `9:00`, reversed interval, cross-month week |
| `RoomSearchTest` | 15 | capacity equal/below the minimum, all features required, dash, busy room, touching endpoints, other day, sorting, empty result, every malformed input |
| `ScheduleViewTest` | 13 | first and last day of the window, eighth day excluded, before the window, cross-month window, case of the room ID, masking, admin, superadmin, unknown room, invalid date, empty week |
| `BookingListViewTest` | 5 | own bookings only, admin sees only their own, sorting, purpose with spaces, empty result |

48 dedicated cases; REQ13, REQ14 and REQ17 are additionally exercised end to end
through `CROBS` in `CROBSTest`. Together they leave the `model` package at 100 %
line coverage.

## 7. Boundaries with the other modules

- `CROBS` owns the storage and the logged-in user; module D only reads them and
  never changes state.
- `Permission` decides who may run the commands, so no role check appears in the
  controller.
- `CrobsMessages` is the single source of every message; the user manual quotes it
  verbatim.
- `model` imports nothing from `view`, `controller` or `io`.

## 8. Known limitations

- **REQ13 depends on REQ10.** `RoomSearch.isBusy` asks
  `TimeSlot.overlaps(TimeSlot)`, which module C owns. Until that method is
  implemented, a search works over an empty booking list and fails loudly as soon
  as one booking exists. The dependency is marked in the `isBusy` javadoc; D does
  not guess a conflict rule that C may contradict.
- `search` treats a room as busy if any booking overlaps the slot, including a
  booking that belongs to the searching user. The specification asks for
  "available rooms", so this is the conservative reading.
- `schedule` does not mark the caller's own bookings in any special way beyond
  showing the real user ID; there is no visual difference from an admin's view of
  their own booking.

## 9. Follow-up from the 15 November review

| Item in `docs/COMPAT-BD.md` | State |
|---|---|
| §4.3 case of IDs | fixed: `ScheduleView.findRoom` matches the ID case-insensitively, covered by `findsTheRoomWhateverTheCaseOfTheId` and `scheduleAcceptsALowerCaseRoomId` |
| §4.4 `Error: Error:` | fixed: every error text in `CrobsMessages` lost its prefix, covered by `errorMessagesLeaveThePrefixToTheDispatcher` |
| §4.5 ownership of `DateFormats` / `CrobsMessages` | kept: both files are edited only on `feat/design-d-query` |
| §4.2 controller files not taken | confirmed: `Application`, `CommandParser`, `Dispatcher`, `Permission`, `Session` stay module A's |
| §4.1 REQ13 blocked on REQ10 | unchanged, documented in §8 above |

Answers to the questions of §6 that touch module D:

1. Permission checks belong in the facade (`CROBS`), next to `changeRole`; D's
   views take an already-authenticated `User` and never check a role themselves.
2. Validate before minting an ID, so a rejected command does not burn a `U00x`.
3. Removing a user or a room also drops the matching bookings; whether a cancelled
   booking should leave a trace is module C's decision, REQ17 does not need one.
4. The room and booking line formats are D's (`id type capacity features`, then
   `bookingId ...`); REQ15 and REQ16 reuse the same format so the output of a room
   never changes between commands.
5. A cancelled booking is simply forgotten; REQ1's command log is the only trace.
