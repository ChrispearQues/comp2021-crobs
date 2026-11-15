# Compatibility layer: modules B and D on top of A

Branch: `compat/bd-on-a`, based on `origin/integrate/a-b` (`5b3dd78`).

Goal: keep module A's controller / logging / login chain as it is, and add the
glue that turns module B's `model` classes (REQ2/3/7/8/9) and module D's query
views (REQ13-REQ17) into working facade methods. **No B or D class body was
rewritten**; module C (REQ10-REQ12) is still a stub.

Status: `javac -Xlint:all` on `src` gives 0 errors / 0 warnings, all 51 tests of
`integrate/a-b` still pass. The CLI smoke run below shows what works and what
still answers "not implemented".

---

## 1. What changed

### Added files (new)

| File | Origin | What it is |
| --- | --- | --- |
| `model/CrobsMessages.java` | module D, verbatim | The shared user-facing texts used by D's views. |
| `model/DateFormats.java` | module D, verbatim | Strict `yyyy-MM-dd` / `HH:mm` parsing and the seven-day window of REQ14. |
| `model/RoomSearch.java` | module D, compile-adapted | REQ13 room search. |
| `model/ScheduleView.java` | module D, compile-adapted | REQ14 weekly schedule with owner masking. |
| `model/BookingListView.java` | module D, compile-adapted | REQ17 "my bookings". |
| `model/CompatViews.java` | new (this branch) | Temporary seam, see §2. |

### Thin delegation (module B, in `model/CROBS.java`)

| Facade method | Delegates to | Kept in the facade |
| --- | --- | --- |
| `addUser(name)` | `new User(id, name, Role.REGULAR)`, `IdGenerator.next()` | `Permission.requireAdmin`, name pre-check, `users.put`, returns the ID |
| `removeUser(userId)` | `findUser` (A), `User.getRole` | admin right, regular-user-only rule, `users.remove` |
| `addRoom(type, cap, feat)` | `RoomType.fromLabel`, `Room.parseCapacity`, `Room.parseFeatures`, `new Room(...)`, `IdGenerator.next()` | admin right, `rooms.put`, returns the ID |
| `modifyRoom(roomId, field, value)` | `Room.modify(field, value)` | admin right, `findRoom` |
| `deleteRoom(roomId)` | `Room.getId` | admin right, `findRoom`, `rooms.remove` |
| `search(...)` | `new RoomSearch(rooms.values(), bookings.values()).search(...)` | `Permission.requireLogin` |
| `schedule(...)` | `new ScheduleView(rooms, bookings.values()).week(roomId, weekStart, currentUser)` | `Permission.requireLogin` |
| `listRoom(roomId)` | `CompatViews.describe(findRoom(roomId))` | `Permission.requireLogin` |
| `listAllRooms()` | `CompatViews.describe` per room | login right, ID sort (REQ16) |
| `listBookings()` | `new BookingListView(bookings.values()).of(currentUser)` | `Permission.requireLogin` |

Two helpers were added to `CROBS`: `findRoom(String)` (mirror of A's
`findUser`, case-insensitive, `NotFoundException` when missing) and the imports
`ArrayList`, `Collections`, `Set`.

### Compile-level adaptations only (module D files)

| Place | Was (D) | Now | Why |
| --- | --- | --- | --- |
| `RoomSearch.search` | `room.describe()` | `CompatViews.describe(room)` | `Room` belongs to B; its body must not be edited. |
| `ScheduleView.week` | `Booking.chronological()` | `CompatViews.chronological()` | same reason, `Booking` belongs to C. |
| `ScheduleView.week` | `booking.describeWithUser(...)` | `CompatViews.describeWithUser(booking, ...)` | same reason. |
| `ScheduleView.week` | `Permission.isAdmin(viewer)` | `viewer != null && viewer.isAdmin()` | module A's `Permission` has no `isAdmin`; B's `User.isAdmin()` already means ADMIN **or** SUPERADMIN. |
| `BookingListView.of` | `booking.describeWithRoom()`, `Booking.chronological()` | `CompatViews.*` | same reason. |

Nothing else in D's views was touched: the filtering, sorting, masking and
message logic is byte-for-byte D's.

## 2. `CompatViews` — the temporary seam

D's views render text through methods that live on B's `Room` (`describe()`),
C's `Booking` (`describeWithUser`, `describeWithRoom`, `chronological`) and C's
`TimeSlot` (`toString`). Those bodies are off limits for this branch, so the
same text is produced by `model/CompatViews` (package-private, 5 static
helpers):

- `describe(Room)` -> `roomId roomType capacity features` (`-` when no feature)
- `slotText(TimeSlot)` -> `date startTime-endTime`
- `describeWithUser(Booking, String)` -> `bookingId date start-end owner purpose`
- `describeWithRoom(Booking)` -> `bookingId roomId date start-end purpose`
- `chronological()` -> date, then start time, then booking ID

`CompatViews` should be deleted as soon as B adds `Room.describe()` and C adds
the three `Booking` helpers plus `TimeSlot.toString()`; then the views call the
real methods again and the facade calls `findRoom(id).describe()`.

## 3. What module B must still do

1. **Decide where the admin check lives.** This branch puts
   `Permission.requireAdmin` / `requireLogin` inside `CROBS`, the same way A's
   `changeRole` does and the way D's `CROBS` does. If B expected the controller
   to check rights instead, the two lines per method move up a layer.
2. **`Room.describe()`** — add it to `Room` (format:
   `id type.getLabel() capacity features|-`) and delete `CompatViews.describe`.
3. **Feature normalisation** — B's `Room.parseFeatures` keeps the case as typed
   and treats `-` as "none"; D's version lower-cases every token. REQ13
   features are therefore compared case-sensitively today. Pick one rule.
4. **`Room.modify` field names** — B accepts exactly `roomType`, `capacity`,
   `features`. The user manual / D's parser wording should match.
5. **Message style** — B/A throw inline English sentences without an `Error: `
   prefix; the `Dispatcher` adds it. See §6 for the one place where this
   currently doubles.
6. **REQ3 / REQ9 booking cleanup** — `removeUser` and `deleteRoom` already drop
   the matching entries of the `bookings` map, but the map is empty until C
   lands, and REQ9 actually says "cancel the **future** bookings". Follow up:
   `// TODO(REQ3/REQ10)` and `// TODO(REQ9)` in `CROBS`.

## 4. What module D must still do

1. **REQ13 is blocked on REQ10.** `RoomSearch.isBusy` calls
   `TimeSlot.overlaps(...)`, still a C stub. Today a search works over an empty
   booking list and fails loudly (`UnsupportedOperationException: TODO:
   TimeSlot.overlaps`) as soon as one booking exists. Documented in
   `RoomSearch` and in the `CROBS.search` javadoc.
2. **`Application` / `CommandParser` / `Dispatcher` / `Permission` / `Role` /
   `Session` changes from `feat/design-d-query` were deliberately not taken** —
   A owns the controller. D's parser argument orders were checked against A's:
   `search <date> <start> <end> <minCapacity> <features>` and
   `schedule <roomId> <weekStartDate>` match.
3. **Case handling of IDs** — A's `findUser`/`findRoom` upper-case the ID
   before lookup; D's `ScheduleView` looks up `rooms.get(roomId)` exactly.
   `schedule r001` therefore reports "room does not exist" while
   `listRoom r001` works. Unify in one place when A and D meet.
4. **Error text prefix** — D's `CrobsMessages` error patterns already start with
   `Error: `, and A's `Dispatcher` prints `Error: <message>`; a bad date shows
   `Error: Error: invalid date ...`. Resolve by dropping one of the two.
5. **`DateFormats` / `CrobsMessages`** are byte-for-byte D's files, so D keeps
   ownership; any wording change should be made on `feat/design-d-query` and
   merged here.

## 5. CLI smoke run

Script (one command per line, piped into the REPL), run twice: once exactly as
requested, once with a legal room type because `LAB` is not a valid label.

Run A — the requested sequence:

| # | Command | Actual output |
| --- | --- | --- |
| 1 | `login U000` | `Logged in as U000` |
| 2 | `addUser Alice` | `Added user U001` |
| 3 | `addRoom LAB 20 wifi,projector` | `Error: Unsupported room type: LAB` (see note) |
| 4 | `listAllRooms` | `No rooms found.` |
| 5 | `addRoom computerLab 20 wifi,projector` | `Added room R001` |
| 6 | `listAllRooms` | `R001 computerLab 20 wifi,projector` |
| 7 | `listRoom R001` | `R001 computerLab 20 wifi,projector` |
| 8 | `search 2026-11-16 09:00 11:00 10 wifi` | `R001 computerLab 20 wifi,projector` |
| 9 | `schedule R001 2026-11-16` | `No booking in this week.` |
| 10 | `listBookings` | `No booking.` |
| 11 | `logout` | `Logged out` |
| 12 | `quit` | `Bye.` |

Note on step 3: `RoomType` (module B) only knows `lectureTheatre`,
`computerLab`, `seminarRoom`, `meetingRoom`, so `LAB` is rejected — that is B's
validation working, not a wiring gap. Step 5 is the same command with a legal
label and it succeeds.

Run B — error paths of the same wiring:

| Command | Actual output |
| --- | --- |
| `addUser Alice` before login | `Error: You must log in first` |
| `removeUser U001` | `Removed user U001` |
| `removeUser U000` | `Error: Only a regular user can be removed; U000 is SUPERADMIN` |
| `addRoom meetingRoom x -` | `Error: Invalid capacity number: x` |
| `modifyRoom R001 capacity 30` | `Modified room R001`, then `listRoom R001` -> `R001 seminarRoom 30 -` |
| `modifyRoom R001 roomType nonsense` | `Error: Unsupported room type: nonsense` |
| `listRoom R999` | `Error: No such room: R999` |
| `search 2026-02-29 09:00 11:00 10 -` | `Error: Error: invalid date "2026-02-29", expected yyyy-MM-dd.` (double prefix, §4.4) |
| `schedule R001 2026-13-01` | `Error: Error: invalid date "2026-13-01", expected yyyy-MM-dd.` (double prefix, §4.4) |
| `deleteRoom R001` then `listAllRooms` | `Deleted room R001`, then `No rooms found.` |
| `book` / `modifyBooking` / `deleteBooking` | still `Not implemented yet: TODO: ...` (module C) |

`TimeSlot.overlaps` probed directly: `UnsupportedOperationException: TODO:
TimeSlot.overlaps`.

## 6. Behaviour that is a guess and needs the owners' confirmation

1. **Permission checks inside `CROBS`** (§3.1) — REQ2/3/7/8/9 are admin-only and
   REQ13-REQ17 need a session; this branch enforces both in the facade, because
   A's `changeRole` already does it there. If the team wants it in the
   controller, move it (12 lines).
2. **`addUser` validates the name before minting the ID** so that a rejected
   command does not consume `U00x`. D's code did the same; B's `User` would
   otherwise validate after `next()` had already run.
3. **Removing a user / deleting a room also drops the matching bookings.** The
   task said "minimal safe handling"; the exact REQ3 / REQ9 semantics (delete
   vs. keep as history, "future only") are module C's call.
4. **`findRoom` upper-cases the ID** (like A's `findUser`), while D's
   `ScheduleView` does not (§4.3).
5. **Room/booking line formats** are D's (`id type capacity features`,
   `bookingId date start-end ...`), not B's `toString()`
   (`id | type | capacity=.. | features=..`). REQ15/REQ16 now print D's format
   because REQ13/14/17 must print it for consistency; if the team prefers B's
   `toString`, change `CompatViews.describe` in one place.
6. **`Cancelled` bookings are simply forgotten** — no tombstone, no log line
   beyond REQ1's command log.
