# Crobs — Command-Line Campus Room Booking System

COMP2021 Object-Oriented Programming · Group Project · Fall 2026 (PolyU)

| Item | Value |
|---|---|
| Submission (ZIP) | **14:00, 22 Nov 2026** |
| Design review | **14:00, 28 Nov 2026** |
| Late penalty | 25% per day |
| JDK | 25 |
| IDE | IntelliJ IDEA 2026.2 |

## Layout

```
src/hk/edu/polyu/comp/comp2021/crobs/
├── Application.java            # main; reads -html / -txt log paths
└── model/
    └── CROBS.java              # business entry point
test/hk/edu/polyu/comp/comp2021/crobs/model/
└── CROBSTest.java              # JUnit 5
lib/                            # JUnit 5.14 jars (do not delete)
crobs.iml                       # module: src + test source roots
```

Two hard constraints from the guideline:

1. **All business logic goes in `model`** — line coverage is measured only on
   `hk.edu.polyu.comp.comp2021.crobs.model`, and the score is
   `base points x requirement coverage`.
2. **Run `COMP2021 PROJECT.xml` inspections early** — one point is deducted per
   reported error.

## Team

| Member | Module | Requirements |
|---|---|---|
| A | Infrastructure, logging, permissions | REQ1, REQ4, REQ5, REQ6, REQ18 |
| B | Users and rooms | REQ2, REQ3, REQ7, REQ8, REQ9, REQ15, REQ16 |
| C | Bookings and conflict detection | REQ10, REQ11, REQ12, REQ17 |
| D | Search and schedules | REQ13, REQ14 |

## Workflow

- One branch per member; merge into `dev` at least once a day.
- Only the infrastructure owner edits `Application`, `CROBS`, `Session`.
- Model API signatures are frozen on 8 Nov 2026.
