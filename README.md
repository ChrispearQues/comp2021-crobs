# Crobs — Command-Line Campus Room Booking System

COMP2021 Object-Oriented Programming · Group Project · Fall 2026 (PolyU)

| 项目 | 内容 |
|---|---|
| 提交（ZIP） | **2026-11-22 14:00** |
| Design review | **2026-11-28 14:00** |
| 迟交 | 每天扣 25% |
| JDK | 25 |
| IDE | IntelliJ IDEA 2026.2 |

---

## 1. 我们要做什么

一个命令行校园场地预约系统（Crobs）。两类用户（admin / regular）+ 一个唯一的 superadmin；所有房间和用户都有唯一 ID。共 **18 条命令**（REQ1–REQ18），另有 GUI 与 undo/redo 两个加分项。

每条需求都在 Issues 里开成了独立任务，**第一眼看 Issues 页就知道还剩什么**。

## 2. 评分怎么来（决定我们怎么花时间）

| 部分 | 分值 | 关键点 |
|---|---|---|
| 需求实现 | 40 | 18 条命令 + 报告/手册 |
| 代码质量 | 20 | 跑 `COMP2021 PROJECT.xml` inspection，**每个报错扣 1 分** |
| 测试覆盖 | 10 | 只统计 `hk.edu.polyu.comp.comp2021.crobs.model` 包的行覆盖率；<30% 得 0 分，≥90% 满分，再乘需求完成率 |
| 报告 + 用户手册 | 15 | 见 guideline Section A |
| 演示 | 7 | ≤6 分钟录影，开场出示学生证，每人 ≥1 分钟 |
| 互评 | 8 | design review |
| 加分 | +9 | GUI 6 分 / undo-redo 3 分 |

**两条由此推出的铁律**

1. **业务逻辑必须写在 `model` 包**。写进 CLI 或 controller 的代码测不到，覆盖率就是白丢分。
2. **inspection 从第一周就开始跑**，不要留到最后修。

## 3. 目录与文档

```
src/hk/edu/polyu/comp/comp2021/crobs/
├── Application.java        入口：读 -html / -txt 参数、装配、主循环
├── view/                   ConsoleView（只做输入输出）
├── controller/             CommandParser / Dispatcher / Session / command/
├── io/                     HtmlLogger / TxtLogger（REQ1 日志）
└── model/                  ★ 全部业务逻辑，coverage 统计范围
test/hk/edu/polyu/comp/comp2021/crobs/model/
```

- **`docs/ARCHITECTURE.md`** — 完整类清单、依赖方向、`CROBS` 门面方法签名表（11/8 冻结）。**动手前先读这个。**
- Issues — 每条 REQ 一个，带分值、负责模块、验收清单。

## 4. 时间线

| 日期 | 里程碑 |
|---|---|
| 11/8（日） | 认领模块；`CROBS` 方法签名冻结；每人都能跑通骨架 |
| 11/13（四） | REQ1/2/3/5/6/7/9/15/16 完成 + 登录流程打通 |
| 11/17（一） | REQ10/11/12/13/14/17 完成，18 条命令全部可用 |
| 11/19（三） | 覆盖率 ≥90%、inspection 清零、异常与权限补全 |
| 11/20（四） | 用户手册 + 报告 + slides + 录影（≤6 分钟） |
| 11/22（六）14:00 | **交 ZIP**（提前一天交，当天不碰新东西） |

> 11/22 当天还有人要交 DSAI2201 A2（23:55）与 AF2108 A2，所以**目标是在 11/20 收尾**，不要压到最后一天。

## 5. 模块划分（待认领）

| 模块 | 内容 | 需求 |
|---|---|---|
| **A** 基础设施 | `Application`、`Dispatcher`、`CommandParser`、`Session`、`io/`；日志、登录登出、权限、quit — **ChrispearQues** | REQ1, 4, 5, 6, 18 |
| **B** 用户与房间 | `model/User`、`Room`、`RoomType`、`IdGenerator`；增删改查 | REQ2, 3, 7, 8, 9, 15, 16 |
| **C** 预约与冲突 | `model/Booking`、`TimeSlot`；创建/修改/删除预约与冲突检测 | REQ10, 11, 12 |
| **D** 查询与视图 | 搜索、周课表、列出我的预约、输出格式 + 用户手册主笔 | REQ13, 14, 17 |

认领后在 Issues 里把自己指派上，并在本表对应行写上名字。A 已由 **ChrispearQues** 认领。

## 6. 工作流程（每个人都照这个做）

```bash
# 第一次
git clone https://github.com/ChrispearQues/comp2021-crobs.git
cd comp2021-crobs

# 每次开工
git checkout dev && git pull
git checkout -b feat/book-conflict     # 分支名：feat/短描述 或 fix/短描述

# 做完
git add -A
git commit -m "feat(book): detect overlapping bookings"
git push -u origin feat/book-conflict
```

规矩：

1. **`main` 只由 A 合并**，其他人不要直接推 `main`（免费账号的私有仓库开不了分支保护，只能靠纪律）。
2. **每天至少合一次 `dev`**。攒三天的冲突够吵一晚上。
3. **共用文件只有 A 能改**：`Application`、`Dispatcher`、`CROBS`、`Session`。需要新增方法就提 issue 或告诉 A。
4. **提交信息**用 `feat|fix|docs|test(scope): 描述`。
5. **不引入没学过的库**；只用 JDK 标准库 + `lib/` 里的 JUnit。

## 7. 什么算「做完」（Definition of Done）

一个 issue 只有下面全部打勾才能关：

- [ ] `model` 里有对应方法，逻辑完整（不是 `TODO`）
- [ ] 异常情形都有处理：未知 ID、权限不足、参数格式错误、冲突
- [ ] 至少 3 个 JUnit 测试（正常 + 边界 + 异常），放 `test/.../model/`
- [ ] 本地 `javac` 通过、测试全绿
- [ ] IntelliJ inspection 无报错
- [ ] 分支合并进 `dev`，issue 关闭

## 8. 三条代码规矩

1. **业务判断一律在 `model`**。`controller` 里出现 `if (user.getRole() == ADMIN)` 就是放错位置（测不到、覆盖率丢分）。
2. **命令一律走 `Dispatcher`**，REQ1 日志只在这一处写，不要每条命令各自写文件。
3. **异常不吞**：`model` 抛 `CrobsException` 的子类，`Dispatcher` 统一捕获转成用户可读信息。不要用返回 `String` 表示错误。

## 9. 提交物清单（11/22 的 ZIP 里要有）

- [ ] 完整 `src` + `test`（含 `COMP2021 PROJECT.xml` 检查通过）
- [ ] 用户手册
- [ ] 项目报告（标题下方写明各成员贡献百分比）
- [ ] 演示 slides
- [ ] 演示录影（≤6 分钟，开场出示学生证，每人 ≥1 分钟）
- [ ] 小组 Honour Declaration（每人一份，缺/假声明最高扣 40%）
