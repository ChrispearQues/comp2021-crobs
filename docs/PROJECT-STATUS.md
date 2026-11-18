# CROBS 项目现状与待办

最后更新：2026-11-18 19:00（香港时间）
代码基线：`main` @ `90c6da0`

---

## 1. 一句话结论

`main` 已经是一个可以交付的版本：**18 条命令全部实现、177 个单元测试全绿、model 包行覆盖 98.6%**。
剩下的是**非代码交付物**：用户手册、演示录影、小组 declaration，以及 11/28 的 design review 准备。

---

## 2. 代码与测试现状

| 项目 | 数值 |
| --- | --- |
| 交付提交 | `main` = `90c6da0`（2026-11-18 18:45） |
| 源文件 | 42 个 Java 文件 |
| 测试类 | 15 个，177 个测试用例，0 失败 |
| 编译 | `javac --release 25 -Xlint:all` 0 error / 0 warning |
| 行覆盖（全项目） | 1362 / 1598 = **85.2%** |
| 行覆盖（model 包） | 1064 / 1079 = **98.6%** |
| 分支覆盖（model 包） | 167 / 174 = **96.0%** |
| 命令覆盖 | 18 条命令全部实现，无 `UnsupportedOperationException` |
| 超级管理员 | 固定 ID `U000`，首个真实用户 `U001` |

**覆盖率低的包（供参考）**

- `controller.command`：21 / 107 = 19.6%（每个命令类只做转发，未单独测）
- `controller`：113 / 173 = 65.3%
- `Application`：49 / 113 = 43.4%

> 作业的测试覆盖率评分按 **model 包的行覆盖**计算，该口径下为 98.6%，已达到 ≥90% 的要求。


---

## 2.5 贡献率统计（2026-11-18 实测）

### 按当前代码归属（`git blame`，`src` 共 2843 行）

| 作者 | 行数 | 占比 |
| --- | --- | --- |
| YIN（D） | 1730 | 60.9% |
| Chrispear（A） | 1087 | 38.2% |
| LIUZIYI（B） | 26 | 0.9% |
| brianisbadass（C） | 0 | 0% |

### 按提交与改动量（全部历史，含未合并分支）

| 作者 | 提交数 | 新增行 | 删除行 | 文件改动次数 |
| --- | --- | --- | --- | --- |
| Chrispear（A） | 31 | 4307 | 364 | 117 |
| YIN（D） | 8 | 4904 | 777 | 115 |
| LIUZIYI（B） | 2 | 385 | 56 | 16 |
| brianisbadass（C） | 0 | 0 | 0 | 0 |

### 按模块看归属

| 模块 / 文件组 | 主要作者与占比 |
| --- | --- |
| A 基础设施（controller / io / Application / view） | Chrispear 57.0%（847 行）· YIN 43.0%（638 行） |
| B 用户与房间（User / Room / RoomType / IdGenerator） | YIN 57.1%（182 行）· Chrispear 34.8% · LIUZIYI 8.2% |
| C 预约与冲突（Booking / TimeSlot） | YIN 63.8%（97 行）· Chrispear 36.2% |
| D 查询与视图（RoomSearch / ScheduleView / BookingListView / DateFormats / CrobsMessages） | YIN 99.4%（492 行）· Chrispear 0.6% |
| 门面 CROBS.java（四方交叉） | YIN 81.9%（321 行）· Chrispear 18.1% |

**如何解读**：按提交数看 A 与 D 大致相当（31 vs 8 次、+4307 vs +4904 行）；按当前代码归属看 D 占 60.9%，原因是 11-15 那次重写覆盖了 A 与 B 的实现。互评建议按「最终代码 + 交付物（文档 / 测试 / 演示）」综合评定，并把 11-15 的返工计入。

---

## 3. 分支结构（远端 5 个）

| 分支 | 提交 | 日期 | 测试 | 用途 |
| --- | --- | --- | --- | --- |
| `main` | `90c6da0` | 11-18 18:45 | 177 | **交付版**，默认分支 |
| `deliver/frozen` | `90c6da0` | 11-18 18:45 | 177 | 与 main 同一提交，冻结标记 |
| `dev` | `5b3dd78` | 11-15 13:55 | 51 | A+B 整合快照，C/D 仍是 stub |
| `feat/design-d-query` | `05e2145` | 11-15 18:55 | 149 | YIN 的 REQ1–18 全量实现 |
| `feat/b-room-describe` | `24328ff` | 11-17 17:19 | 51 | B 的 `Room.describe` + features 小写化 |

**已删除并备份的分支（本地 tag `archive/2026-11-18/*`，共 8 个）**

`D`、`compat/bd-on-a`、`feat/d-followup`、`integrate/a-b`、`merge/d-into-dev`、`integrate/all`、`feat/module-a`、`feat/b-room-describe`（曾删已恢复）

---

## 4. Pull Request 记录

| PR | 标题 | 状态 | 处理 |
| --- | --- | --- | --- |
| #21 | A 模块：装配、Dispatcher、Parser、日志与登录链路 | MERGED | 11-08 合入 |
| #22 | docs: 门面签名表参数名对齐 | MERGED | 11-08 合入 |
| #24 | Feat/design d query | CLOSED | YIN 误开，1 分钟后自关 |
| #25 | feat(d): REQ13/REQ14/REQ17 query and view（+4352 行） | CLOSED | 11-18 评论说明后关闭，内容已并入 main |
| #26 | fix(d): 房号大小写 + 单一 Error 前缀 | CLOSED | 内容已 cherry-pick（`3b7f51d`） |
| #27 | feat(model): Room.describe + features 小写化 | CLOSED | 内容已并入（`8e3dc24`），**留言待补** |

---

## 5. 代码归属（`git blame` 统计，`src` 共 2843 行）

| 作者 | 行数 | 占比 |
| --- | --- | --- |
| YIN（nocketenyin） | 1730 | 60.9% |
| Chrispear | 1087 | 38.2% |
| LIUZIYI | 26 | 0.9% |

**事实说明**

- YIN 在 11-15 单日完成 REQ1–REQ18 端到端实现，覆盖了 B、C、D 三个模块的功能
- 模块 A 的 controller / io / Application 保留了分层结构，但实现被重写；`Application.java` 无原作者行
- 模块 B 的 `User` / `Room` / `RoomType` / `IdGenerator` 由 B 于 11-14 实现，被 YIN 版本大面积替换，仅余 26 行
- 模块 C（REQ10–12）从未有独立分支交付，实现在 YIN 的分支中

---

## 6. 与评分项的对照

| 评分项 | 分值 | 现状 | 缺口 |
| --- | --- | --- | --- |
| 需求完成度 | 40 | 18/18 命令实现 | 需按 REQ 清单逐条复核边界语义 |
| 代码质量（inspection） | 20 | `COMP2021_PROJECT.xml` 已入仓 | 需在 IDEA 中实跑一次 inspection |
| 测试覆盖 | 10 | model 包 98.6% | 已达到 |
| 报告 + 用户手册 | 15 | 设计文档齐（ARCHITECTURE / DESIGN-D / INTEGRATION） | **终端用户手册未定稿** |
| 演示 | 7 | 未开始 | **≤6 分钟录影、每人≥1 分钟、开场出示学生证** |
| 互评 | 8 | 未开始 | 贡献度需组内商定 |
| 加分：GUI | +6 | 未做 | 可选 |
| 加分：undo/redo | +3 | 未做（`Command` 有 undo 接口占位） | 可选 |

---

## 7. 待办清单

### 交付前必须完成（11/22 14:00）

1. **用户手册**（终端用户视角：安装、启动、18 条命令示例、错误信息说明）
2. **演示录影**：≤6 分钟，全员出镜，开场出示学生证，每人至少讲 1 分钟
3. **提交 ZIP**：源码 + 测试 + 手册 + 报告 + slides + 录影 + 小组 declaration
4. **declaration 写实**：按实际协作方式描述，不要照抄「各人独立完成各自模块」
5. **IDEA inspection 实跑**：确认 0 error（每个报错扣 1 分）


> **⚠ 给 C 的提醒（brianisbadass）**
>
> C 至今 **0 次提交、0 行代码**，REQ10 / REQ11 / REQ12（预约创建、修改、删除与冲突检测）由 D 的实现承担。请尽快完成以下任一项并留痕：
> 1. **复核** `CROBS.book / modifyBooking / deleteBooking` 三个方法，提交一份 review 记录（改注释或补测试都算）
> 2. **补测** C 模块边界：改期撞已占时段、删除房间只取消未来预约、跨天 / 零点时段
> 3. **负责** 演示录影中 C 模块的讲解段落（≥1 分钟）
>
> 建议 **11-20 之前** 完成第 1 或第 2 项，否则互评 8 分会很难看。

### 建议完成

6. **PR #27 留言**：向 B 说明其改动已并入 `8e3dc24`
7. **REQ 边界复核**：`REQ9` 删房间只取消未来预约、改期撞已占时段、跨月窗口
8. **`controller.command` 覆盖**：19.6%，若要冲全项目覆盖率可补（非评分口径）

### 11/28 design review

9. **每人能讲清自己名下那块**：接口、设计理由、为什么这样分层
10. 准备 class diagram 与 18 条命令的调用链说明

---

## 8. 风险

1. **归属集中**：60.9% 的 `src` 由一人完成，互评 8% 的贡献度分配需要组内明确并留痕
2. **诚信风险**：declaration 若声明与实际不符，属于学术诚信问题，务必写实
3. **历史与实现不一致**：`integrate/all` 被 rebase 过，`feat/design-d-query` 的 commit id 不在 `main` 血统中，查证时要按内容比对而非按提交比对
4. **加分项空缺**：GUI（+6）与 undo/redo（+3）未做，属于主动放弃的 9 分
5. **时间**：距 11/22 14:00 还有 3 天多，代码已冻结，剩余为非代码工作

---

## 9. 关键决策记录

| 日期 | 决策 | 原因 |
| --- | --- | --- |
| 11-18 | 超级管理员固定 `U000`，首个用户 `U001` | 作业要求超管随程序创建并固定 ID；原整合版生成为 `U001` |
| 11-18 | 主干 = YIN 的完整实现 + A/B 原层保留 | 保证能交（177 测试）同时不推翻各人模块 |
| 11-18 | 默认分支设为 `main`，快进至 `90c6da0` | 避免新 clone 看到 11-06 骨架 |
| 11-18 | 删除 8 个冗余分支，保留 3 条证据线 | 分支结构反映交付与归属 |
| 11-15 | REQ4/5/6 采用 YIN 的严格策略 | 其测试以此为准，且行为更可辩护 |

---

## 10. 常用命令

```bash
# 编译（JDK 25）
export JAVA_HOME=/opt/homebrew/opt/openjdk@25
export PATH="$JAVA_HOME/bin:$PATH"
javac --release 25 -Xlint:all -encoding UTF-8 -cp "lib/*" -d out $(find src test -name "*.java")

# 跑测试
java -jar junit-platform-console-standalone-1.14.0.jar execute \
     --class-path "lib/*:out" --scan-class-path out --details=summary

# 跑 CLI
printf 'login U000\naddUser Alice\naddRoom computerLab 20 wifi,projector\nlistAllRooms\nquit\n' \
  | java -cp out hk.edu.polyu.comp.comp2021.crobs.Application
```
