# Crobs — 代码结构约定

> 目标：四个人并行开发不打架。任何与本文件冲突的实现，以本文件为准；要改先提 PR 改本文件。

## 1. 目录结构

```
src/hk/edu/polyu/comp/comp2021/crobs/
├── Application.java                  程序入口：读 -html/-txt 参数、装配、主循环
├── view/
│   ├── View.java                     输出接口（print / printError）
│   └── ConsoleView.java              只做 System.out / System.in
├── controller/
│   ├── CommandParser.java            String → Command；只做切分与格式校验
│   ├── Command.java                  接口：execute() / undo() / isUndoable()
│   ├── Dispatcher.java               统一入口：记日志 → parse → execute → 打印（REQ1）
│   ├── Session.java                  登录状态的薄委托（真正的 currentUser 在 CROBS 里）
│   └── command/                      每条命令一个类
│       ├── LoginCommand.java          REQ5
│       ├── LogoutCommand.java         REQ6
│       ├── QuitCommand.java           REQ18
│       ├── AddUserCommand.java        REQ2
│       ├── RemoveUserCommand.java     REQ3
│       ├── ChangeRoleCommand.java     REQ4
│       ├── AddRoomCommand.java        REQ7
│       ├── ModifyRoomCommand.java     REQ8
│       ├── DeleteRoomCommand.java     REQ9
│       ├── ListRoomCommand.java       REQ15
│       ├── ListAllRoomsCommand.java   REQ16
│       ├── BookCommand.java           REQ10
│       ├── ModifyBookingCommand.java  REQ11
│       ├── DeleteBookingCommand.java  REQ12
│       ├── ListBookingsCommand.java   REQ17
│       ├── SearchCommand.java         REQ13
│       └── ScheduleCommand.java       REQ14
├── io/
│   ├── OperationLogger.java          接口：log(int index, String command)
│   ├── HtmlLogger.java               REQ1：HTML 表格
│   ├── TxtLogger.java                REQ1：逐行文本
│   └── CompositeLogger.java          同时写两份
└── model/                            ★ coverage 只统计这个包
    ├── CROBS.java                    门面：全部业务方法 + 权限判定
    ├── User.java                     id, name, role
    ├── Role.java                     enum SUPERADMIN / ADMIN / REGULAR
    ├── Room.java                     id, type, capacity, features
    ├── RoomType.java                 enum lectureTheatre / computerLab / seminarRoom / meetingRoom
    ├── Booking.java                  id, userId, roomId, date, start, end, purpose
    ├── TimeSlot.java                 date + start/end；overlaps(TimeSlot)
    ├── IdGenerator.java              唯一 ID（用户 / 房间 / 预约各一套前缀）
    ├── Permission.java               权限判定集中处（谁能执行哪类操作）
    └── CrobsException.java           基类 + 四个子类：
                                      PermissionDeniedException
                                      NotFoundException
                                      ConflictException
                                      MalformedCommandException

test/hk/edu/polyu/comp/comp2021/crobs/model/
├── CROBSTest.java                    按命令逐一测
├── UserTest.java / RoomTest.java / BookingTest.java
├── TimeSlotTest.java                 重叠边界（端点相接不算冲突）
├── IdGeneratorTest.java
└── PermissionTest.java
```

约 35 个源文件。`controller/command` 里每个类 30–60 行，不要写业务判断。

## 2. 依赖方向（单向，不许反向）

```
view  ←  controller  ←  Application
             ↓
           model
```

- `model` **不得** import `view` / `controller` / `io`（model 是纯业务）
- `controller` 只调 `model` 的公开方法，不直接操作集合
- `view` 只接收要打印的字符串，不做判断
- 唯一例外：`Application` 可以 import 所有层（它是装配器）

## 3. CROBS 门面方法（11/8 冻结，改动需通知全员）

| 方法 | 对应 REQ | 抛出的异常 |
|---|---|---|
| `login(String userId)` | REQ5 | NotFound, Malformed (already logged in) |
| `logout()` | REQ6 | — |
| `addUser(String name)` | REQ2 | PermissionDenied |
| `removeUser(String userId)` | REQ3 | PermissionDenied, NotFound |
| `changeRole(String userId, String newRole)` | REQ4 | PermissionDenied, NotFound, Malformed |
| `addRoom(String roomType, String capacity, String features)` | REQ7 | PermissionDenied, Malformed |
| `modifyRoom(String roomId, String field, String newValue)` | REQ8 | PermissionDenied, NotFound, Malformed |
| `deleteRoom(String roomId)` | REQ9 | PermissionDenied, NotFound |
| `book(String roomId, String date, String startTime, String endTime, String purpose)` | REQ10 | Conflict, NotFound, Malformed |
| `modifyBooking(String bookingId, ...)` | REQ11 | PermissionDenied, Conflict, NotFound |
| `deleteBooking(String bookingId)` | REQ12 | PermissionDenied, NotFound |
| `search(String date, String startTime, String endTime, String minCapacity, String features)` | REQ13 | Malformed |
| `schedule(String roomId, String weekStartDate)` | REQ14 | NotFound, Malformed |
| `listRoom(String roomId)` / `listAllRooms()` | REQ15 / REQ16 | NotFound |
| `listBookings()` | REQ17 | — |
| `getCurrentUser()` | 装配用 | — |

返回值统一是**可直接打印的字符串**（或字符串列表），格式化留在 model，保证 CLI 与未来的 GUI 输出一致。

> **参数一律传原始字符串**。`CommandParser` 只做「切分 + 参数个数校验」，不做类型转换：
> `addRoom` 的 capacity 和 `search` 的 minCapacity 都是 `String`，由 model 负责解析成 `int`
> 并抛出 `MalformedCommandException`。理由：把「用户输入是否合法」这件事留在 model 里，
> 才能被 `model` 包的单元测试覆盖到（覆盖率只算这个包），也才不会出现
> 「parser 报一种错、model 报另一种错」的双重校验。

## 4. 装配与状态归属

谁 new 谁、谁存什么，说清楚才不会出现「两份登录状态」。

```
Application
  ├─ new CROBS()                    ← 唯一持有业务数据 + currentUser
  ├─ new Session(crobs)             ← 只转调 crobs.getCurrentUser()，不存用户对象
  ├─ new CommandParser(crobs)       ← 只依赖门面，构造 Command 对象
  ├─ new TxtLogger / HtmlLogger → CompositeLogger
  ├─ new ConsoleView()
  └─ new Dispatcher(parser, logger, view)
```

- **`CROBS.currentUser` 是登录状态的单一真相源**。`getCurrentUser()` 返回 `null` 表示未登录。
- **`Session` 只做薄委托**：`isLoggedIn()` → `crobs.getCurrentUser() != null`。
  它**不缓存** `User`，否则 `logout()` 之后就会出现「Session 说还登录着、CROBS 说没有」的双真相源。
- `User` 是可变对象（`setRole`）。因为 `currentUser` 指向的就是 `users` 表里的同一个实例，
  `changeRole` 不需要「同步更新两处」，改一处就等于改全部。
- `Dispatcher` 不知道也不问「谁登录了」：权限判断在 `model/Permission`，controller 只搬字符串。

## 5. 三条铁律

1. **业务判断一律在 model**。`controller` 里出现 `if (user.getRole() == ADMIN)` 就是错的位置——权限判定放 `Permission` / `CROBS`，否则测不到、coverage 掉分。
2. **命令一律走 `Dispatcher`**，日志（REQ1）只在这一处写，不要每条命令自己写。
3. **异常不吞**：model 抛 `CrobsException` 子类，`Dispatcher` 统一捕获并转成用户可读信息。返回 `String` 表示错误会破坏可测性。

## 6. 分工映射

| 成员 | 包 | REQ |
|---|---|---|
| A · ChrispearQues | `Application`、`controller`、`io` | REQ1, 4, 5, 6, 18 |
| B · LeoLiuZY | `model`: User/Room/IdGenerator | REQ2, 3, 7, 8, 9, 15, 16 |
| C · brianisbadass | `model`: Booking/TimeSlot + 预约核心命令 | REQ10, 11, 12 |
| D · nocketenyin | `model`: 查询 + `ConsoleView` 输出格式 + 用户手册 | REQ13, 14, 17 |
