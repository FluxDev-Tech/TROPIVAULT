# TropiVault Update Guide

> Guide for contributors: **before** making changes, check what's already been added or modified.  
> After completing work, **update this file** so other developers know what changed.

---

## How to Use This Guide

1. **Before starting new work** — Follow the **Pre-Update Checklist** to avoid duplicating effort or conflicting with ongoing changes.
2. **After completing work** — Add an entry to the **Update Log** at the bottom of this file (most recent first).

---

## Pre-Update Checklist

Before implementing any new feature or modification, run through this checklist:

### 1. Check the Update Log
Scroll to the bottom of this file and read the **Update Log** section. Verify that the function or change you are about to make hasn't already been implemented by another contributor.

### 2. Search the Codebase
Use grep or your IDE's search to confirm the function/class doesn't already exist:

```bash
# Search for a specific function or class name
grep -r "functionName" app/src/
grep -r "ClassName" app/src/
grep -r "functionName" mobile/lib/

# Search across all source files
grep -ri "keyword" --include=*.kt --include=*.dart --include=*.toml .
```

### 3. Check the ViewModel
All business logic functions live in `TropiVaultViewModel.kt`. Review its function list before adding new ones:
- **File**: `app/src/main/java/com/example/tropivault/ui/TropiVaultViewModel.kt`

### 4. Check the Repository
All data-layer operations live in `TropiVaultRepository.kt`:
- **File**: `app/src/main/java/com/example/tropivault/data/repository/TropiVaultRepository.kt`

### 5. Check the DAO
All database queries live in `TropiVaultDao.kt`:
- **File**: `app/src/main/java/com/example/tropivault/data/local/TropiVaultDao.kt`

### 6. Check the Entities
All data models/entities are in the `data/local/` directory:
- `UserEntity.kt` — User accounts (CLIENT, FARMER, RIDER, ADMIN)
- `ProductEntity.kt` — Crop/listing data
- `CartItemEntity.kt` — Shopping cart items
- `OrderEntity.kt` — Order records
- `OrderItemEntity.kt` — Line items within orders
- `NotificationEntity.kt` — Push/in-app notifications

### 7. Check Existing Tests
Ensure you don't duplicate or break existing test coverage:
- `app/src/test/java/com/example/ExampleUnitTest.kt` — Business logic tests
- `app/src/test/java/com/example/ExampleRobolectricTest.kt` — Resource tests

### 8. Run Tests Before & After
```bash
./gradlew :app:testDebugUnitTest
```
All tests must pass before committing changes.

### 9. Check the Environment Files
- `.env` — Local secrets (not committed). Uses `GEMINI_API_KEY` for AI features.
- `.env.example` — Template for required secrets. **Update this if you add new environment variables.**
- `metadata.json` — Project metadata. **Update this if you add new major capabilities.**

---

## Update Log

> Add new entries below in this format (most recent first):

```
### [YYYY-MM-DD] Brief Description of Change
- **What changed**: (summary of files modified)
- **New functions added**: (list any new functions/methods/classes)
- **Modified functions**: (list any modified functions)
- **New dependencies**: (if any were added to `libs.versions.toml` or `build.gradle.kts`)
- **Breaking changes**: (if any)
- **Contributor**: (your name or handle)
- **Files affected**: (list of file paths)

---
```

### [2026-09-29] Initial Documentation & System Overview
- **What changed**: Created `overview.md`, `update.md`, and `technology-stack.md` to document the existing TropiVault codebase.
- **New functions added**: None (first snapshot of existing code).
- **Modified functions**: None.
- **New dependencies**: None.
- **Breaking changes**: None.
- **Contributor**: System initialization
- **Files affected**: `overview.md`, `update.md`, `technology-stack.md`

---

## Area-by-Area Update Guidance

### Adding a New ViewModel Function
1. Add the function to `TropiVaultViewModel.kt`.
2. Expose required data as a `StateFlow` if it needs to drive UI.
3. Use `viewModelScope.launch { }` for all database operations.
4. Update `update.md` log after completion.

### Adding a New DAO Query
1. Add the query to `TropiVaultDao.kt` in `TropiVaultRepository.kt` interface.
2. Call it from `TropiVaultRepository.kt` as a `Flow` or `suspend` function.
3. Expose from the ViewModel.
4. Add a database migration entry or bump database version if entities changed.
5. Update `update.md` log after completion.

### Adding a New Entity
1. Create the entity file in `app/src/main/java/com/example/tropivault/data/local/`.
2. Register it in `AppDatabase.kt` `entities` array.
3. Add DAO methods in `TropiVaultDao.kt`.
4. Add repository methods in `TropiVaultRepository.kt`.
5. If schema changes, bump `AppDatabase` version and handle migration or use `fallbackToDestructiveMigration()`.
6. Update `update.md` log after completion.

### Adding a New Screen
1. Create a new file in `app/src/main/java/com/example/tropivault/ui/screens/`.
2. Add a new entry to the `Screen` enum in `TropiVaultViewModel.kt`.
3. Add the screen to the `when (currentScreen)` block in `MainActivity.kt`.
4. Add to bottom/top bar navigation in `TropiVaultComponents.kt` if applicable.
5. Update `update.md` log after completion.

### Adding a New Dependency
1. Add the version to `gradle/libs.versions.toml` under `[versions]`.
2. Add the library entry under `[libraries]`.
3. Add the implementation to `app/build.gradle.kts` `dependencies` block.
4. Update `.env.example` if the dependency requires secrets configuration.
5. Update `update.md` log after completion.

### Adding a New Flutter Reference File
1. Create `.dart` file under `mobile/lib/`.
2. Ensure it follows the existing Riverpod `StateNotifier` pattern in `session_provider.dart`.
3. Keep parity with the Kotlin model where appropriate (see `user_model.dart`).
4. Update `update.md` log after completion.

### Updating Build Configuration
1. For version bumps, modify `gradle/libs.versions.toml` (the single source of truth).
2. For Gradle settings, modify `gradle.properties` or `settings.gradle.kts`.
3. For app-level config, modify `app/build.gradle.kts`.
4. Update `update.md` log after completion.
