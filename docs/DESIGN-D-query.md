# 模块 D · 查询与视图 — 详细设计（REQ13 / REQ14 / REQ17）

> 负责人：nocketenyin。本文档是 D 模块的唯一实现依据；与 `docs/ARCHITECTURE.md` 冲突时以本文件为准，并同步提 PR 修改 ARCHITECTURE。

## 0. 官方规格原文（摘自 `project_description.pdf`）

**[REQ13] (3.5 points)** searching for available rooms at a specific timeslot with certain desired features.
```
Command: search date startTime endTime minCapacity features
Effect: Lists all rooms that have capacity greater than or equal to minCapacity, contain all
        required features, and are available during the specified time slot.
```

**[REQ14] (3.5 points)** viewing the weekly schedule for a specific room.
```
Command: schedule roomId weekStartDate
Effect: Lists all active bookings for the specified room in the 7-day period starting from
        weekStartDate, sorted by date and start time. Regular users can only see the userID
        corresponding to a booking if it is their own booking. Admin users can see the
        userIDs corresponding to all bookings.
```

**[REQ17] (1.5 points)** listing all bookings made by the current user.
```
Command: listBookings
Effect: Lists all bookings made by the current user. Bookings should be sorted by date and
        start time.
```

另有两处全局约束直接影响写法：

- **语言级别 = 8**（Section 12）。禁止 `var`、`switch` 表达式、text block、`List.of`、`Stream.toList()`。`java.time`（Java 8 引入）可用。
- **BON2 明确排除 `search` / `listBookings`**：这两条命令 `isUndoable()` 保持 `false`，不要为 undo 做额外工作。

## 1. 职责边界与文件归属

| 文件 | 是否 D 新建 | 说明 |
|---|---|---|
| `model/DateFormats.java` | 新建 | 日期时间解析与校验，全模块共用 |
| `model/RoomSearch.java` | 新建 | REQ13 过滤链 |
| `model/ScheduleView.java` | 新建 | REQ14 一周视图 + 脱敏 |
| `model/BookingListView.java` | 新建 | REQ17 |
| `model/CrobsMessages.java` | 新建 | 空结果/错误文案常量，CLI 与手册共用 |
| `controller/command/SearchCommand.java` | 已有，填 `execute()` | 只做 `String.join("\n", crobs.search(...))` |
| `controller/command/ScheduleCommand.java` | 已有，填 `execute()` | 同上 |
| `controller/command/ListBookingsCommand.java` | 已有，填 `execute()` | 同上 |
| `model/CROBS.java` | **不是 D 的文件** | 由 A 改成三行委托（见 §2.1） |
| `test/.../model/*Test.java` | 新建 | 4 个测试类 |

约束：`model` 不得 import `controller` / `view` / `io`。所有格式化留在 model，返回 `List<String>`。

## 2. 与队友的接口契约

### 2.1 A 需要改的 `CROBS` 委托（一次 PR 解决，D 不直接改）

```java
/** REQ13 */
public List<String> search(String date, String startTime, String endTime,
                           String minCapacity, String features) {
    Permission.requireLogin(currentUser);
    return new RoomSearch(rooms.values(), bookings.values())
            .search(date, startTime, endTime, minCapacity, features);
}

/** REQ14 */
public List<String> schedule(String roomId, String weekStartDate) {
    Permission.requireLogin(currentUser);
    return new ScheduleView(rooms, bookings).week(roomId, weekStartDate, currentUser);
}

/** REQ17 */
public List<String> listBookings() {
    Permission.requireLogin(currentUser);
    return new BookingListView(bookings.values()).of(currentUser);
}
```

D 需要 A 提供的东西（提 issue 给 A，不要自己动 `CROBS`）：

1. `currentUser` 字段（登录者，未登录为 `null`）。
2. `Map<String, Room> rooms` 与 `Map<String, Booking> bookings`（`Collection` 视图即可）。
3. 上述三个方法体的委托代码。
4. `CROBS` 的存储不可变假设：`search/schedule/listBookings` 只读，不修改状态。

### 2.2 依赖 C 的部分

- `TimeSlot.overlaps(TimeSlot)` 用于 REQ13 的“时段可用”判断。**在 C 合入前，`RoomSearchTest` 的可用性用例先跳过，其余用例照写。**
- `Booking` 的 `getId/getUserId/getRoomId/getTimeSlot/getPurpose` 已齐备，无需 C 新增。

### 2.3 依赖 B 的部分

- `Room.getId/getType/getCapacity/getFeatures` 已齐备。
- 建议 B 的 `listRoom` / `listAllRooms` 复用 D 的 `RoomSearch.roomLine(Room)`，保证同一个房间在任何命令下输出一致（REQ15/16 与 REQ13 格式统一）。这条要跟 B 约定，谁先合谁定义。

### 2.4 统一文案（全组共用，D 提出，A 落 `CrobsMessages`）

| key | 文案 |
|---|---|
| `NO_MATCHING_ROOM` | `No matching room.` |
| `NO_BOOKING_IN_WEEK` | `No booking in this week.` |
| `NO_BOOKING` | `No booking.` |
| `UNKNOWN_ROOM` | `Error: room <id> does not exist.` |
| `UNKNOWN_DATE` | `Error: invalid date "<v>", expected yyyy-MM-dd.` |
| `UNKNOWN_TIME` | `Error: invalid time "<v>", expected HH:mm.` |
| `BAD_INTERVAL` | `Error: start time must be earlier than end time.` |
| `BAD_CAPACITY` | `Error: minCapacity must be a non-negative integer.` |
| `NOT_LOGGED_IN` | `Error: please login first.` |

## 3. 类设计

### 3.1 `DateFormats`（final，私有构造，全静态）

```java
static LocalDate parseDate(String s)          // 严格 yyyy-MM-dd，失败抛 MalformedCommandException
static LocalTime parseTime(String s)          // 严格 HH:mm，失败抛 MalformedCommandException
static void requireOrdered(LocalTime start, LocalTime end)  // start >= end 抛 Malformed
static List<LocalDate> sevenDaysFrom(LocalDate start)       // 含 start 的连续 7 天
```

要点：用 `DateTimeFormatter.ofPattern("uuuu-MM-dd").withResolverStyle(ResolverStyle.STRICT)`（`yyyy` + STRICT 会因 era 字段报错，必须写 `uuuu`）。`HH:mm` 同理用 `HH:mm` + STRICT。

### 3.2 `RoomSearch`

```java
RoomSearch(Collection<Room> rooms, Collection<Booking> bookings)
List<String> search(String date, String start, String end, String minCapacity, String features)
static String roomLine(Room room)   // "R001 lectureTheatre 120 projector,whiteboard"
```

过滤链（顺序即实现顺序，短路可省开销）：

1. `LocalDate d = DateFormats.parseDate(date)`；`LocalTime s/e`；`requireOrdered(s, e)`。
2. `int min = parseCapacity(minCapacity)`：`Integer.parseInt` 失败或 `< 0` → `MalformedCommandException(BAD_CAPACITY)`。
3. `Set<String> wanted = parseFeatures(features)`：按 `,` 拆分，`trim` + `toLowerCase`；`-` 或空串表示“无特征要求”。
4. 对每个房间依次判断：`capacity >= min` → `room.getFeatures()` 全包含 `wanted`（同样 trim + 小写后比较）→ 该时段无冲突预约。
5. 冲突判断：构造 `new TimeSlot(date, start, end)`，遍历 `bookings`，`b.getRoomId().equals(room.getId()) && b.getTimeSlot().overlaps(slot)` 即判为不可用。**端点相接不算冲突**（由 C 的 `overlaps` 保证，D 不在这一层重复实现）。
6. 结果按 `roomId` 升序；为空返回 `["No matching room."]`。

### 3.3 `ScheduleView`

```java
ScheduleView(Map<String, Room> rooms, Collection<Booking> bookings)
List<String> week(String roomId, String weekStartDate, User viewer)
```

1. `rooms.get(roomId)` 为 `null` → `NotFoundException(UNKNOWN_ROOM)`（A 已 `requireLogin`，这里只管存在性）。
2. `LocalDate start = parseDate(weekStartDate)`，窗口 `[start, start.plusDays(6)]` 闭区间。
3. 过滤：`booking.roomId == roomId` 且 `date` 落在窗口内。**“active” 在本项目中等价于“未删除”**——删除即从集合移除，不需要额外状态位。
4. 排序：`date` → `startTime` → `bookingId`（三级，保证输出可复现、测试稳定）。
5. 脱敏：`isAdmin(viewer)` 时原样输出 `userId`；否则 `booking.getUserId().equals(viewer.getId())` ？`userId` : `***`。`isAdmin` 判定为 `role == SUPERADMIN || role == ADMIN`（superadmin 是 admin 的一种）。
6. 行格式：`B003 2026-11-16 09:00-11:00 U002 group meeting`。
7. 为空返回 `["No booking in this week."]`。

### 3.4 `BookingListView`

```java
BookingListView(Collection<Booking> bookings)
List<String> of(User owner)
```

1. 只保留 `booking.getUserId().equals(owner.getId())` 的预约（**REQ17 对 admin 也一样，只看自己**，与 REQ14 不同，别混）。
2. 排序同 §3.3 第 4 条。
3. 行格式与 REQ14 相同 → 直接复用 `ScheduleView.bookingLine(Booking, String userIdText)` 静态方法，避免两份格式代码漂移。
4. 为空返回 `["No booking."]`。

## 4. 输出格式规范（手册与代码必须一致）

```
> search 2026-11-16 09:00 11:00 30 projector
R002 computerLab 40 projector,whiteboard
R005 seminarRoom 30 projector

> search 2026-11-16 09:00 11:00 500 projector
No matching room.
```

```
> schedule R002 2026-11-16          # 以 regular U002 身份
B003 2026-11-16 09:00-11:00 U002 group meeting
B007 2026-11-18 14:00-16:00 ***  consultation

> schedule R002 2026-11-16          # 以 admin 身份
B003 2026-11-16 09:00-11:00 U002 group meeting
B007 2026-11-18 14:00-16:00 U007 consultation
```

```
> listBookings
B003 R002 2026-11-16 09:00-11:00 group meeting
B009 R005 2026-11-20 10:00-12:00 study group
```

说明：
- `listBookings` 行首没有 userId（都是自己）；`schedule` 行首**不打印 roomId**（命令里已指定），但打印 userId（脱敏后）。这一差异要写进用户手册。
- 不确定就用同一行字段顺序：`bookingId [roomId] date start-end [userId] purpose`。

## 5. 输入校验矩阵

| 输入 | 非法样例 | 异常 | 触发点 |
|---|---|---|---|
| date | `2026-13-01` / `26-11-16` / `abc` | `MalformedCommandException` | `DateFormats.parseDate` |
| time | `9:00` / `24:00` / `0900` | `MalformedCommandException` | `DateFormats.parseTime` |
| start >= end | `11:00 11:00` | `MalformedCommandException` | `requireOrdered` |
| minCapacity | `-5` / `abc` / `3.5` | `MalformedCommandException` | `RoomSearch` |
| features | `a,,b` | 忽略空 token | `parseFeatures` |
| roomId | `R999` | `NotFoundException` | `ScheduleView` |
| 未登录执行三条命令 | — | `PermissionDeniedException` | `CROBS`（A 的 `requireLogin`） |

注意：`CommandParser` 只做参数个数检查（5/2/0 个），**语义校验全部在 model**，否则覆盖率拿不到。

## 6. 测试用例清单（`test/.../model/`）

`DateFormatsTest`（8 例）：合法日期/时间；闰日 `2028-02-29` 通过、`2026-02-29` 抛；`2026-13-01` 抛；`24:00` 抛；`9:00` 抛；`start == end` 抛；`sevenDaysFrom` 跨月正确。

`RoomSearchTest`（10 例）：容量恰好等于 min；容量小于 min 被排除；多特征全包含才命中；`-` 无特征要求；非法日期/时间/容量各 1；排序为 roomId 升序；无结果文案；**时段被占用则排除（依赖 C 的 `overlaps`，C 未合入前先 `@Disabled` 注释说明）**。

`ScheduleViewTest`（9 例）：窗口首日、末日命中，第 8 天不命中；排序 date→start→id；regular 看他人的预约显示 `***`；regular 看自己显示真 ID；admin 看全部真 ID；superadmin 等同 admin；未知房间抛 `NotFoundException`；跨月窗口；空周文案。

`BookingListViewTest`（7 例）：只返回自己的；admin 也只返回自己的；排序正确；跨房间混合排序；空列表文案；purpose 含空格不截断；同一时段不同日期不混淆。

合计 34 例，全部直接 `new Room(...)` / `new Booking(...)` / `new TimeSlot(...)` 构造，**不依赖 `CROBS`，D 可以独立开工**。

## 7. 时间安排（对齐 README）

| 日期 | 动作 |
|---|---|
| 11/9–11/10 | 建 `DateFormats` + `CrobsMessages` + 测试；提 3 个 issue；给 A 提 `CROBS` 委托 issue |
| 11/11–11/12 | `RoomSearch` + 测试（可用性用例挂起） |
| 11/13 | 催 C 的 `overlaps`；催 A 的 `currentUser` / 集合 |
| 11/14–11/16 | `ScheduleView` + `BookingListView` + 测试；三个 Command 的 `execute()` |
| 11/14 并行 | 用户手册骨架（18 章标题 + 自己 3 章成文） |
| 11/17–11/19 | 覆盖率 ≥90%、inspection 清零、补异常分支 |

## 8. 风险

1. **仓库里没有 `COMP2021 PROJECT.xml`**，20 分 inspection 无法自检 → 催 A 从课程材料包补进来。
2. **PATH 里没有 `java` / `javac`**（本机未装或未配 JDK 25）→ 开工前先装 JDK 25，否则“本地编译通过”这条 DoD 无法验证。
3. `lib/` 缺 `junit-platform-launcher`，命令行跑不了 JUnit → 要么 A 补 jar，要么统一用 IDEA 跑并截图留证。
4. 日期格式官方未规定，全组必须统一为 `yyyy-MM-dd` / `HH:mm`，**要 A 在 `CommandParser` 的文档注释里写明**，避免 B/C 用另一种格式。

---

# 附：三份 Issue 文本（按 `.github/ISSUE_TEMPLATE/requirement.md`）

## [REQ13] search 可用房间查询

```markdown
**REQ id / points**: REQ13 / 3.5

**Command & effect**
```
search date startTime endTime minCapacity features
```
Lists all rooms whose capacity >= minCapacity, that contain all required features,
and that are free during [startTime, endTime) on date.

**Who can run it**
- [ ] superadmin
- [ ] admin
- [ ] regular user
- [x] any logged-in user

**Model methods touched**
`CROBS.search`（A 改为委托）、`model/RoomSearch`（D）、`model/DateFormats`（D）、`TimeSlot.overlaps`（C）

**Edge cases to handle**
- [ ] 未知/非法日期、`H:mm`、`24:00`
- [ ] `startTime >= endTime`
- [ ] `minCapacity` 非数字或负数
- [ ] features 为空或 `-`（视为无要求）
- [ ] 与已有预约冲突的房间必须排除（端点相接不算冲突）
- [ ] 结果按 roomId 升序；无结果输出 `No matching room.`
- [ ] 未登录 → PermissionDenied

**Owner**: nocketenyin

**Tests written** (file:line):
```

## [REQ14] schedule 房间周视图

```markdown
**REQ id / points**: REQ14 / 3.5

**Command & effect**
```
schedule roomId weekStartDate
```
Lists all active bookings of the room in the 7 days starting from weekStartDate,
sorted by date then start time. Regular users see a booking's userID only when it is
their own; admin (including superadmin) sees all userIDs.

**Who can run it**
- [ ] superadmin
- [ ] admin
- [ ] regular user
- [x] any logged-in user

**Model methods touched**
`CROBS.schedule`（A 改为委托）、`model/ScheduleView`（D）、`DateFormats`（D）

**Edge cases to handle**
- [ ] 未知 roomId → NotFound
- [ ] 非法 weekStartDate
- [ ] 窗口为 [start, start+6]，第 8 天不得出现
- [ ] 跨月窗口
- [ ] regular 看他人预约 → userID 显示 `***`；看自己 → 真 ID
- [ ] admin / superadmin → 全部真 ID
- [ ] 空周输出 `No booking in this week.`
- [ ] 未登录 → PermissionDenied

**Owner**: nocketenyin

**Tests written** (file:line):
```

## [REQ17] listBookings 我的预约

```markdown
**REQ id / points**: REQ17 / 1.5

**Command & effect**
```
listBookings
```
Lists all bookings made by the currently logged-in user, sorted by date then start time.
Even an admin sees only their own bookings here.

**Who can run it**
- [ ] superadmin
- [ ] admin
- [ ] regular user
- [x] any logged-in user

**Model methods touched**
`CROBS.listBookings`（A 改为委托）、`model/BookingListView`（D）

**Edge cases to handle**
- [ ] 只返回自己的预约（admin 同样）
- [ ] 排序 date → startTime → bookingId
- [ ] 无预约输出 `No booking.`
- [ ] 未登录 → PermissionDenied
- [ ] 不参与 undo/redo（BON2 明确排除）

**Owner**: nocketenyin

**Tests written** (file:line):
```

# 附：给 A 的协作 issue

```markdown
**标题**: [infra] CROBS 增加 currentUser + 查询三方法委托（D 模块依赖）

**背景**: D 模块（REQ13/14/17）的逻辑写在 `model/RoomSearch`、`ScheduleView`、
`BookingListView`，但 `CROBS` 是共用文件，按 README §6 只能由 A 修改。

**需要 A 做的**
1. `CROBS` 增加 `private User currentUser`，`login` / `logout` 维护它。
2. `CROBS` 增加 `rooms` / `bookings` 的只读访问（或直接改三个方法体）。
3. 按 `docs/DESIGN-D-query.md` §2.1 把 `search` / `schedule` / `listBookings`
   改为委托 + `Permission.requireLogin`。
4. 在 `CommandParser` 注释中固定日期格式 `yyyy-MM-dd`、时间格式 `HH:mm`，
   并把 `search` / `schedule` / `listBookings` 的参数个数检查写好。
5. 把 `COMP2021 PROJECT.xml` 放进仓库，并补 `junit-platform-launcher` jar。
```
