# 贡献记录（CROBS，2026-11-18）

本文件用于填写小组 declaration 与互评，依据是**提交历史**，不是 `git blame`。
原因：11-15 有一次覆盖式重写，多人的原始实现被替换，`blame` 只剩最终存活的代码，
无法反映各人实际做过的工作量。两种口径都列在下面，供组内讨论。

---

## 1. 汇总（两种口径）

| 成员 | 模块 | 提交数 | 新增 / 删除行 | 当前存活行（src+test+docs） |
| --- | --- | --- | --- | --- |
| Chrispear | A 基础设施 | 31 | +4307 / −364 | 1805（32.9%） |
| YIN | D 查询与视图 → 后期重写全模块 | 8 | +4904 / −777 | 3599（65.7%） |
| LIUZIYI | B 用户与房间 | 2 | +385 / −56 | 78（1.4%） |
| brianisbadass | C 预约与冲突 | 0 | 0 / 0 | 0（0%） |

「当前存活行」= 在 `main` 上 `git blame` 到的行数，覆盖了 `src` / `test` / `docs`，共 5482 行。

---

## 2. 各人实际交付

### Chrispear（A：装配、Dispatcher、Parser、日志、登录链路）

- 31 次提交，+4307 / −364 行，文件改动 117 次
- PR #21（A 模块整体）、#22（门面签名文档）均已合入
- 当前代码中 `controller` / `io` / `Application` / `view` 共 1485 行，其中 847 行（57.0%）仍为其本人所写
- 文档：`docs/ARCHITECTURE.md`、`docs/INTEGRATION.md`、`docs/PROJECT-STATUS.md`、`docs/CONTRIBUTIONS.md`
- 集成工作：分支清理、`deliver/frozen` 冻结、超级管理员 `U000` 修正、默认分支切换

### YIN（D：查询与视图；后期重写 REQ1–REQ18）

- 8 次提交，+4904 / −777 行，文件改动 115 次
- PR #25（+4352 行）、#26（房号大小写 + 单一 `Error:` 前缀）
- 目标模块 D：`RoomSearch` / `ScheduleView` / `BookingListView` / `DateFormats` / `CrobsMessages` 共 495 行，99.4% 为其所写
- 11-15 一次性完成 REQ1–REQ18 端到端实现，当前 `main` 的 1730 行 `src`、1431 行 `test`、438 行文档归其名下
- 测试：149 个用例（D 模块），model 包行覆盖 98.6%

### LIUZIYI（B：用户与房间）

**这是本文件要特别说明的一位，因为其工作被大范围覆盖。**

- `a06951d`（11-14 20:44）`feat(partB)`：**14 个文件，+344 / −36**
  - 7 个命令类：`AddRoom` / `AddUser` / `DeleteRoom` / `ListAllRooms` / `ListRoom` / `ModifyRoom` / `RemoveUser`
  - 4 个模型类：`IdGenerator` / `Room` / `RoomType` / `User`
  - 3 个测试类：`IdGeneratorTest`（37 行）/ `RoomTest`（81 行）/ `UserTest`（52 行）
- `24328ff`（11-17 17:19）PR #27：`Room.describe()` + features 统一小写
- 11-15 的覆盖式重写后，其 `RoomTest`（81 行）与 `IdGeneratorTest`（37 行）被替换为其他实现
- 现存：`UserTest` 52 行原样保留；`src` 中 26 行
- PR #27 的两处改动已进入 `main`（提交 `8e3dc24`：构造器归一化 features + `hasAllFeatures` 大小写不敏感）

### brianisbadass（C：预约与冲突）

- 提交 0 次，代码 0 行
- REQ10 / REQ11 / REQ12（预约创建、修改、删除与冲突检测）在题面分工中属 C，当前由 YIN 的实现承担
- **待补**：复核记录、边界测试、演示录影中 C 模块的讲解（≥1 分钟）

---

## 3. declaration 建议写法

下面这段可直接用于小组 declaration 的贡献描述，重点是把「做过但被覆盖」的部分写实：

> The group divided the system into four modules: A (assembly, dispatcher,
> parser, logging, login), B (users, rooms, room types, ID generation),
> C (booking, modification, deletion and conflict detection) and D (search,
> schedule view, booking list view, date formats).
>
> Chrispear implemented module A and maintained the repository, integration and
> release. LIUZIYI implemented module B, including seven command classes, four
> model classes and three test classes (14 files, commit a06951d), and later
> added `Room.describe()` and feature normalisation. YIN implemented module D and
> subsequently rewrote the system in one pass so that requirements REQ1 to REQ18
> were complete and covered by 149 tests; that rewrite replaced part of the
> earlier implementations of modules B, which is recorded in this document.
> brianisbadass was assigned module C; the code for booking, modification,
> deletion and conflict detection was delivered within YIN's rewrite, and
> brianisbadass contributed the review of that behaviour and its boundary tests.
>
> All members reviewed the final code, the user manual and the demonstration
> video.

> 注：C 那一句需在 11-20 前落实（见第 2 节），否则请改写为实际情况。

---

## 4. 互评建议

按「**提交记录 + 交付物（代码 / 测试 / 文档 / 演示）**」综合评定，不按 `git blame` 单一指标：

| 成员 | 依据 | 建议区间 |
| --- | --- | --- |
| Chrispear | A 模块 31 次提交 + 集成与交付 | 与 YIN 相当 |
| YIN | 全模块重写，代码量最大 | 略高于平均 |
| LIUZIYI | B 模块 14 文件 + PR #27（后被覆盖） | 不低于平均 |
| brianisbadass | 需先补 2 节所列交付物 | 视补交情况定 |

`个人分 = 组总分 × 组员数 × 贡献%`，因此本表需**四人确认后**再写入 declaration。
