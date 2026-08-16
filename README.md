# Singleton in Java — Software License Manager

## Purpose

This project demonstrates the **Singleton** design pattern in Java through a
realistic scenario: a **Software License Manager** that must exist as a single
instance across the entire application so that every component sees the same
license state, unlocked features, and usage counters.

## Chosen Scenario

Imagine a desktop application that ships with different license tiers
(BASIC, PRO, ENTERPRISE). On startup the application activates a license key,
which determines the tier and the set of unlocked features. Various subsystems
(feature checker, telemetry reporter) need to query or mutate license state.
Because there must be exactly one source of truth, the `LicenseManager` is
implemented as a Singleton using the **Bill Pugh holder idiom** — lazy, thread-safe,
and lock-free.

## Project Structure

```
singleton/example/
├── README.md
└── src/main/java/com/blog/example/
    ├── Main.java                       Entry point; orchestrates the demo
    ├── license/
    │   └── LicenseManager.java         Singleton holding all license state
    └── client/
        ├── FeatureChecker.java          Queries the singleton for feature access
        └── UsageReporter.java           Records module hits via the singleton
```

### Layer / Package Roles

| Package | Responsibility |
|---|---|
| `com.blog.example` | Application entry point (`Main`) |
| `com.blog.example.license` | The Singleton class that owns license state |
| `com.blog.example.client` | Consumer classes that interact with the Singleton |

## Prerequisites

- **Java 17** or later (uses `String.isBlank()`, `LocalDate`, text blocks are not required)

## How to Compile and Run

From the project root (`singleton/example/`):

```bash
# Compile
mkdir -p out
javac -d out \
  src/main/java/com/blog/example/license/LicenseManager.java \
  src/main/java/com/blog/example/client/FeatureChecker.java \
  src/main/java/com/blog/example/client/UsageReporter.java \
  src/main/java/com/blog/example/Main.java

# Run
java -cp out com.blog.example.Main
```

## Expected Output (example)

```
=== Software License Manager — Singleton Demo ===

first  instance hash: 12345678
second instance hash: 12345678
Same object? true

License activated  -> key=ACME-PRO-2024-XK9R  tier=PRO  features=[REPORTING, ADVANCED_ANALYTICS, REAL_TIME_SYNC]

[FeatureChecker] REPORTING -> GRANTED  (tier=PRO)
[FeatureChecker] ADVANCED_ANALYTICS -> GRANTED  (tier=PRO)
[FeatureChecker] REAL_TIME_SYNC -> GRANTED  (tier=PRO)
[FeatureChecker] AI_PREDICTIONS -> DENIED  (tier=PRO)

[UsageReporter] Recorded hit for REPORTING
[UsageReporter] Recorded hit for REPORTING
[UsageReporter] Recorded hit for ADVANCED_ANALYTICS
[UsageReporter] Skipped REAL_TIME_SYNC (not licensed)  <-- only if feature were locked

--- License Summary ---
Key             : ACME-PRO-2024-XK9R
Tier            : PRO
Activated on    : 2025-01-15
Features        : [REPORTING, ADVANCED_ANALYTICS, REAL_TIME_SYNC]
Module hit map  : {REPORTING=2, ADVANCED_ANALYTICS=1, REAL_TIME_SYNC=1}
-----------------------
```

## Key Singleton Details

1. **Private constructor** — prevents `new LicenseManager()`.
2. **Static inner `Holder` class** — the JVM guarantees that `Holder` is loaded
   (and `INSTANCE` created) only when `getInstance()` is first invoked, giving
   lazy initialisation without `synchronized`.
3. **Single shared state** — `FeatureChecker` and `UsageReporter` both call
   `getInstance()` and provably receive the same object (hash codes match).
