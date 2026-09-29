# TropiVault - Technology Stack Reference

> Complete inventory of all technologies, languages, frameworks, firmware, and tools used across every area of the TropiVault system.

---

## 1. Firmware / Embedded Systems

| Component | Technology | Status | Notes |
|-----------|-----------|--------|-------|
| Cold Storage Vault Controller (simulated) | Firmware abstraction in app | Conceptual | Vault temperature setpoint slider and nitrogen purge toggle simulate IoT sensor control from `FarmerDashboardScreen.kt`. Real implementation would use ESP32/Arduino-based controllers with Modbus/RS485 or MQTT. |
| Insulated Transit Box Sensor | Simulated telemetry | Conceptual | Rider dashboard displays `11.8°C (Optimal Produce Safe)` from `RiderDashboardScreen.kt`. Would integrate with ESP32 + DS18B20 temperature sensor + Bluetooth LE. |
| Solar Power Monitoring | Simulated | Conceptual | `FarmerDashboardScreen.kt` shows `98% (4.4 kW)` solar battery status. Real integration would use MPPT solar charge controller with Modbus RTU. |
| Ozone Sanitizer | Simulated | Conceptual | Referenced in product preservation notes (`ProductEntity.kt`). Would use commercial ozone generator with timer control. |

### Planned Firmware Integrations
| Target Device | Planned Tech | Integration Method |
|---------------|-------------|-------------------|
| Vault Temperature Sensor | ESP32 + DS18B20 | MQTT over WiFi |
| Cold-Box Tracker | ESP32 + BLE | Bluetooth Low Energy to companion app |
| Solar Charge Controller | Victron SmartSolar + VE.Direct | Modbus RTU via USB/Bluetooth |
| Humidity Sensor | SHT31-DIS | I2C to ESP32 gateway |

---

## 2. Frontend (Mobile App) — Primary: Android

### Languages
| Language | Version | Usage |
|----------|---------|-------|
| **Kotlin** | 2.2.10 | All Android app source code (UI, ViewModel, Repository, Data layer) |
| **Kotlin DSL** | Gradle Kotlin DSL | Build scripts (`build.gradle.kts`) |

### UI Framework
| Framework | Version | File |
|-----------|---------|------|
| **Jetpack Compose** | 2024.09.00 (BOM) | `app/src/main/...` (all `.kt` UI files) |
| **Material 3** | Androidx | `ui/theme/` directories |
| **Accompanist Permissions** | 0.37.3 (dependency declared, commented out) | `libs.versions.toml` |

### State Management
| Library | Usage | File |
|---------|-------|------|
| **StateFlow** | Reactive state in ViewModel | `TropiVaultViewModel.kt` |
| **State Management** | `collectAsStateWithLifecycle` for Compose | All screen files |
| **Datastore Preferences** | 1.1.7 | `UserPreferences.kt` |

### Navigation
| Library | Usage |
|---------|-------|
| **Navigation Compose** | 2.8.9 | Custom `Screen` enum + `navigateTo()`/`navigateBack()` |
| **HorizontalPager** | `androidx.compose.foundation.pager` | Onboarding PageView |

### Image Loading
| Library | Version | Usage |
|---------|---------|-------|
| **Coil** | 2.7.0 | Image loading (configured; uses `painterResource` for local drawables currently) |

### Android SDK
| Component | Version |
|-----------|---------|
| **compileSdk** | 36 (release, min 24) |
| **minSdk** | 24 (Android 7.0) |
| **targetSdk** | 36 |
| **AGP** | 9.1.1 |

### Android Permissions
| Permission | Usage |
|-----------|-------|
| `INTERNET` | Network access (future API/backend calls) |
| `VIBRATE` | Haptic feedback |
| `android.permission` | Declared in `AndroidManifest.xml` |

---

## 3. Frontend (Mobile App) — Cross-Platform Reference: Flutter

### Languages
| Language | Version | Usage |
|----------|---------|-------|
| **Dart** | (unspecified, compatible with stable channel) | Cross-platform reference implementation |

### Framework & State Management
| Library | Usage |
|---------|-------|
| **Flutter** | Cross-platform UI framework |
| **Riverpod** | `flutter_riverpod` for state management |
| **StateNotifier** | `SessionNotifier` in `session_provider.dart` |

### Files
| File | Description |
|------|-------------|
| `mobile/lib/models/user_model.dart` | `UserModel` with `UserRole` and `UserStatus` enums, JSON serialization |
| `mobile/lib/providers/session_provider.dart` | `SessionNotifier`, 9 Riverpod providers for auth/role/session |

---

## 4. Backend / Data Layer

### Local Database
| Technology | Version | Usage |
|-----------|---------|-------|
| **Room** | 2.7.0 | SQLite abstraction for all persistent data |
| **Room KTX** | 2.7.0 | Kotlin extensions and Flow support |
| **Room Compiler** | 2.7.0 (KSP) | Annotation processor |
| **SQLite** | Embedded | Underlying database engine (`tropivault_database.db`) |

### Data Storage
| Mechanism | Version | Usage |
|-----------|---------|-------|
| **SharedPreferences** | Android SDK | `UserPreferences.kt` — onboarding status, active user ID, remember-me |

### Entities (Room Tables)
| Entity | Table Name |
|--------|-----------|
| `UserEntity` | `users` |
| `ProductEntity` | `products` |
| `CartItemEntity` | `cart_items` |
| `OrderEntity` | `orders` |
| `OrderItemEntity` | `order_items` |
| `NotificationEntity` | `notifications` |

### Database Schema
| Version | Migration Strategy |
|---------|-------------------|
| 1 | `fallbackToDestructiveMigration()` (no custom migrations yet) |

---

## 5. API & Networking

### HTTP Client
| Library | Version | Usage |
|---------|---------|-------|
| **Retrofit** | 2.12.0 | Type-safe HTTP client (declared, not yet wired) |
| **Moshi** | 1.15.2 | JSON serialization for Retrofit responses |
| **OkHttp** | 4.10.0 | HTTP engine + logging interceptor |

### AI Services
| Service | Version | Usage |
|---------|---------|-------|
| **Firebase AI (Gemini)** | 34.17.0 (BOM) | AI/ML features (configured via `.env` secret) |

### Firebase
| Product | Version | Usage |
|---------|---------|-------|
| **Firebase BOM** | 34.17.0 | Platform BOM |
| **Firebase AI** | (BOM-managed) | Gemini model access |
| **Firebase AppCheck** | (BOM-managed) | reCaptcha + debug providers (security) |
| **Firebase Auth** | (declared, commented out) | Planned Google Sign-In via Credential Manager |
| **Firebase Firestore** | (declared, commented out) | Planned cloud database |
| **Google Services Auth** | (declared, commented out) | Planned for Google Sign-In |
| **Google ID (One Tap)** | (declared, commented out) | Planned for Google Sign-In |

### Secrets Management
| Tool | Version | Usage |
|------|---------|-------|
| **Secrets Gradle Plugin** | 2.0.1 | Injects `.env` values into BuildConfig |
| **Google Services Gradle** | 4.5.0 | Firebase configuration |

---

## 6. Testing

| Framework | Version | Usage |
|-----------|---------|-------|
| **JUnit 4** | 4.13.2 | Unit tests |
| **AndroidX JUnit** | 1.3.0 | Android test runner |
| **Espresso** | 3.7.0 | UI instrumentation tests |
| **Robolectric** | 4.16.1 | Local unit tests with Android framework simulation |
| **Kotlin Coroutines Test** | 1.10.2 | Async/coroutine testing |
| **Compose UI Test JUnit4** | (BOM) | Compose UI testing |
| **AndroidX Test Core** | 1.6.1 | Test utilities |
| **AndroidX Test Runner** | 1.6.2 | Instrumentation runner |

### Test Files
| File | Type | Description |
|------|------|-------------|
| `ExampleUnitTest.kt` | Unit | Order subtotal & delivery fee calculations |
| `ExampleRobolectricTest.kt` | Robolectric | String resource retrieval (app name) |
| `ExampleInstrumentedTest.kt` | Instrumented | Scaffold (not yet implemented) |

---

## 7. Build Tools & Infrastructure

| Tool | Version | Usage |
|------|---------|-------|
| **Gradle** | 9.3.1 | Build system |
| **Gradle Wrapper** | 9.3.1 | Ensures consistent Gradle version |
| **Kotlin Android Extensions (KSP)** | 2.3.5 | Symbol processing for Room & Moshi |
| **Android Gradle Plugin (AGP)** | 9.1.1 | Android project build system |
| **Java JDK** | 11 (compileOptions) / 17+ (recommended runtime) | Java source compatibility |

### Build Configuration Files
| File | Purpose |
|------|---------|
| `build.gradle.kts` (root) | Project-level plugins |
| `app/build.gradle.kts` | App-level dependencies, build types, signing configs |
| `gradle/libs.versions.toml` | Version catalog (single source of truth) |
| `gradle/wrapper/gradle-wrapper.properties` | Gradle distribution URL |
| `gradle.properties` | JVM args, Kotlin code style, caching |
| `settings.gradle.kts` | Repository config, root project name, module includes |

### Build Variants
| Variant | Signing Config | Minify | Notes |
|---------|---------------|--------|-------|
| `debug` | `debugConfig` | Disabled | Uses `debug.keystore` (android/android) |
| `release` | `release` | Disabled | Uses environment variable keystore path |

---

## 8. Development Environment

| Requirement | Details |
|-------------|---------|
| **IDE** | VS Code (with Kotlin, Gradle, Java extensions) |
| **JDK** | JDK 17 or 21 |
| **Android SDK** | Command-line tools or Android Studio |
| **Environment Variables** | `ANDROID_HOME`, `PATH` for emulator/platform-tools |
| **Signing (Release)** | `KEYSTORE_PATH`, `STORE_PASSWORD`, `KEY_PASSWORD` env vars |
| **Secrets** | `.env` file (copy from `.env.example`) |

### VS Code Extensions
| Extension | ID |
|-----------|-----|
| Kotlin | `fwcd.kotlin` |
| Gradle for Java | `vscjava.vscode-gradle` |
| Extension Pack for Java | `vscjava.vscode-java-pack` |
| Android iOS Emulator | `Diatemb.vs-code-android-ios-emulator` (optional) |

---

## 9. Design System

### Colors
| Token | Hex | Role |
|-------|-----|------|
| ForestGreen | `#075B45` | Primary brand |
| DeepGreen | `#064E3B` | High-contrast text, dark surfaces |
| FreshGreen | `#219653` | Secondary CTAs, approval |
| GoldenYellow | `#F5B700` | Accent badges, highlights |
| TropicalOrange | `#F59E0B` | Ratings, warnings, telemetry |
| TropiCream | `#FFFDF5` | Background |
| DarkForest | `#0A1B14` | Dark mode primary |
| LeafMint | `#E8F5E9` | Surface accent |
| GoldLight | `#FEF3C7` | Light accent |

### Typography
| Style | Font | Weight | Size |
|-------|------|--------|------|
| displayLarge | SansSerif | Black | 36sp |
| displayMedium | SansSerif | ExtraBold | 30sp |
| headlineLarge | SansSerif | ExtraBold | 24sp |
| headlineMedium | SansSerif | Bold | 20sp |
| titleLarge | SansSerif | Bold | 18sp |
| bodyLarge | SansSerif | Normal | 15sp |
| bodyMedium | SansSerif | Normal | 13sp |
| labelSmall | SansSerif | Bold | 10sp |

### Material 3
| Feature | Implementation |
|---------|---------------|
| ColorScheme | Light + Dark (custom tropical palette) |
| Dynamic Color | Disabled (preserves brand colors) |
| Shapes | RoundedCornerShape (4-20dp) |

---

## 10. Resources & Assets

### Drawables
| Resource | Type |
|----------|------|
| `tropivault_hero_harvest_1790637412571.jpg` | AI-generated harvest hero image |
| `tropivault_icon_1790637372989.jpg` | App icon reference |
| `tropivault_preservation_1790637424122.jpg` | Vault preservation imagery |
| `ic_launcher_foreground.xml` | Adaptive icon foreground |
| `ic_launcher_background.xml` | Adaptive icon background |

### Mipmap Directories
| Density | Directories |
|---------|-------------|
| ldpi/mdpi | `mipmap-mdpi` |
| hdpi | `mipmap-hdpi` |
| xhdpi | `mipmap-xhdpi` |
| xxhdpi | `mipmap-xxhdpi` |
| xxxhdpi | `mipmap-xxxhdpi` |
| anydpi-v26 | `mipmap-anydpi-v26` (adaptive icons) |

### XML Configs
| File | Purpose |
|------|---------|
| `AndroidManifest.xml` | App permissions, activities, application config |
| `data_extraction_rules.xml` | Auto-backup rules |
| `backup_rules.xml` | Full backup rules |
| `themes.xml` | App theme definition |
| `strings.xml` | String resources |
| `colors.xml` | Color resources |

---

## 11. Environment Variables & Secrets

| Variable | Source | Usage |
|----------|--------|-------|
| `GEMINI_API_KEY` | `.env` | Google Gemini AI API calls (currently commented out) |
| `KEYSTORE_PATH` | Environment | Release keystore file path |
| `STORE_PASSWORD` | Environment | Release keystore password |
| `KEY_PASSWORD` | Environment | Release key password |

---

## 12. Project Metadata

| Field | Value |
|-------|-------|
| **Project Name** | TropiVault |
| **Application ID** | `com.aistudio.tropivault.kvzpqa` |
| **Namespace** | `com.example` |
| **Version Code** | 1 |
| **Version Name** | 1.0 |
| **Major Capabilities** | Server-side Gemini API |
| **Master Admin Key** | `TROPI2026` |

---

## 13. Supported Crops & Origins

### Crop Categories
| Category | Examples |
|----------|----------|
| Mangoes | Carabao Mangoes |
| Bananas & Plantains | Lacatan Bananas |
| Citrus & Melons | Pineapple, Pomelo |
| Exotic & Rare | Mangosteen, Dragon Fruit |
| Preserved & Dehydrated | Freeze-dried Crisps, Jackfruit Chews |
| Farm Bundles | Tropical Immunity Vault Box |

### Regional Origins
| Origin | Province |
|--------|----------|
| Guimaras | Guimaras Island |
| Davao | Davao City / Davao del Sur |
| Bukidnon | Bukidnon Highlands |
| Batangas | Lipa, Batangas |
| Metro Manila | Taguig, Pasig, Mandaluyong |

---

## 14. Payment Methods

| Method | Key | Notes |
|--------|-----|-------|
| GCash | `GCASH` | Account: 0917-888-VAULT, reference tracking |
| Bank Transfer | `BANK_TRANSFER` | BDO, BPI, Metrobank, UnionBank, LandBank |
| Cash on Delivery | `COD` | Rider collects cash, records POD |
| Cash on Pickup | `COP` | Pay at regional depot |

---

## 15. User Roles & Permissions

| Role | Key | Permissions |
|------|-----|-------------|
| Client / Buyer | `CLIENT` | Browse, cart, checkout, track orders, manage address, support tickets |
| Farmer Partner | `FARMER` | Manage products, vault telemetry, fulfill orders, view revenue |
| Rider Partner | `RIDER` | Accept deliveries, transit tracking, POD recording, request payout |
| Administrator | `ADMIN` | Approve partners, verify payments, assign riders, moderate catalog, manage users |

---

## 16. Order Lifecycle

| Status | Description |
|--------|-------------|
| `PLACED` | Order created, awaiting processing |
| `PROCESSING` | Farmer packing produce in climate vault |
| `READY_FOR_DISPATCH` | Packed, awaiting rider assignment |
| `IN_TRANSIT` | Rider started delivery |
| `DELIVERED` | Customer received order |
| `COMPLETED` | Order fully settled |
| `CANCELLED` | Order cancelled |
| `PAID` | Payment fully processed |
| `PENDING_VERIFICATION` | Payment awaiting admin verification |
| `PENDING_COLLECTION` | COD/COP awaiting cash collection |
| `VERIFIED` | Payment verified by admin |
| `COLLECTED_BY_RIDER` | COD cash collected by rider |
