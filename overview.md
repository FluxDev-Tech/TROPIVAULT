# TropiVault - System Overview

> **Good Fruits. Longer Tomorrows.**
> *Tropical Agriculture Marketplace & Climate-Controlled Food Preservation Management System*

---

## Architecture Summary

| Layer | Technology | File Path |
|-------|-----------|-----------|
| Mobile App (Primary) | Android Jetpack Compose + Kotlin + Room | `app/` |
| Cross-Platform Reference | Flutter + Riverpod + Dart | `mobile/` |
| Build System | Gradle (Kotlin DSL) | `build.gradle.kts`, `settings.gradle.kts` |
| Database | SQLite via Room (embedded) | `AppDatabase.kt` |
| Dependency Injection | Manual (ViewModel-scoped) | `TropiVaultViewModel.kt` |
| State Management | Kotlin StateFlow | `TropiVaultViewModel.kt` |
| API Client | Retrofit + Moshi + OkHttp | `build.gradle.kts` (configured) |
| AI Integration | Firebase AI (Gemini) | `libs.versions.toml` |
| Testing | JUnit + Robolectric | `app/src/test/` |

---

## Directory Structure

```text
tropivault/
├── app/                          # Android application (primary)
│   ├── build.gradle.kts          # App-level build config
│   └── src/
│       ├── main/
│       │   ├── AndroidManifest.xml
│       │   ├── java/com/example/
│       │   │   ├── MainActivity.kt
│       │   │   ├── ui/
│       │   │   │   ├── TropiVaultViewModel.kt       # Central state & navigation
│       │   │   │   ├── Screen.kt                    # (defined in ViewModel)
│       │   │   │   ├── components/
│       │   │   │   │   └── TropiVaultComponents.kt    # TopBar, BottomBar, ProductCard, StatusBadge
│       │   │   │   ├── screens/
│       │   │   │   │   ├── OnboardingScreens.kt
│       │   │   │   │   ├── MarketplaceScreen.kt
│       │   │   │   │   ├── ProductDetailScreen.kt
│       │   │   │   │   ├── CartAndCheckoutScreen.kt
│       │   │   │   │   ├── AuthScreens.kt
│       │   │   │   │   ├── FarmerDashboardScreen.kt
│       │   │   │   │   ├── RiderDashboardScreen.kt
│       │   │   │   │   ├── AdminDashboardScreen.kt
│       │   │   │   │   ├── ClientDashboardScreen.kt
│       │   │   │   │   └── ProfileAndOrdersScreen.kt
│       │   │   │   └── theme/                         # Color, Theme, Type
│       │   │   └── data/
│       │   │       ├── local/                         # Room entities, DAO, AppDatabase, DatabaseInitializer
│       │   │       └── repository/                    # TropiVaultRepository, UserPreferences
│       │   ├── res/                                    # Drawables, mipmaps, XML configs
│       │   └── test/                                  # JUnit + Robolectric tests
│       └── androidTest/
├── mobile/                       # Flutter reference implementation (cross-platform)
│   └── lib/
│       ├── models/
│       │   └── user_model.dart                      # UserModel with role & status enums
│       └── providers/
│           └── session_provider.dart               # Riverpod StateNotifier for session
├── gradle/                       # Gradle wrapper + version catalog
│   ├── libs.versions.toml
│   └── wrapper/gradle-wrapper.properties
├── .env.example                  # Secrets template (Gemini API key)
├── metadata.json
└── README.md
```

---

## Implemented Functions & Features

### 1. Onboarding & Navigation
- **`OnboardingScreen`** (`OnboardingScreens.kt`) — HorizontalPager/PageView with 2 pages
  - Page 0: *Get Started* — hero harvest visual, slogan, primary CTA, login link, skip-to-market
  - Page 1: *How It Works* — 4 feature cards, explore marketplace, create account
  - Persistence: onboarding completion saved in `UserPreferences` (`key_onboarding_done`)
- **`Screen` enum** (`TropiVaultViewModel.kt:12`) — 14 screens: ONBOARDING_START, ONBOARDING_HOW_IT_WORKS, PUBLIC_MARKETPLACE, PRODUCT_DETAIL, CART, CHECKOUT, LOGIN, REGISTER, CLIENT_DASHBOARD, FARMER_DASHBOARD, RIDER_DASHBOARD, ADMIN_DASHBOARD, ORDER_HISTORY, USER_PROFILE, NOTIFICATIONS
- **`navigateTo(screen)`**, **`navigateBack()`** — stack-based navigation with back handling in `MainActivity.kt`
- **`completeOnboarding()`** — marks onboarding done, redirects to marketplace

### 2. User Authentication & Session Management
- **`login(email, pass)`** — authenticates against Room DB, sets active user, handles PENDING/REJECTED states
- **`registerClient(fullName, email, phone, password, address)`** — instant approval, enters CLIENT_DASHBOARD
- **`registerFarmer(fullName, farmName, email, phone, farmLocation, password, autoApprove)`** — optional auto-approval
- **`registerRider(fullName, email, phone, address, vehicleType, licenseNumber, password, autoApprove)`** — optional auto-approval
- **`registerAdmin(fullName, email, phone, password, adminKey)`** — requires master key `TROPI2026`, enters ADMIN_DASHBOARD
- **`logout()`** — clears session via `UserPreferences.clearSession()`
- **`quickLoginAs(role)`** — instant role switcher for testing (finds existing user by role)
- **`updateAddress(newAddress)`** — updates the active client's delivery address
- **`sessionState`** (StateFlow) — reactive sealed interface: Unauthenticated, Loading, Authenticated, PendingApproval, Error
- **`isClient`, `isFarmer`, `isRider`, `isAdmin`** — role-based boolean StateFlows

### 3. Marketplace & Product Discovery
- **`MarketplaceScreen`** — search bar, category filter chips, location filter, sort dropdown, featured products grid, product cards
- **`filteredProducts`** (StateFlow) — live filtering by search query, category, location, and sort (Featured, PriceLow, PriceHigh, ShelfLife)
- **`updateSearchQuery(query)`, `selectCategory(category)`, `selectLocation(location)`, `selectSortBy(sort)`**
- **`ProductCard`** component — displays product image/icon, category, shelf-life badge, storage temp, rating, price, add-to-cart button
- **`selectProduct(product)`** — navigates to PRODUCT_DETAIL

### 4. Product Detail
- **`ProductDetailScreen`** — hero fruit display, preservation metrics vault card, farm provenance card, description, quantity stepper, Add to Cart, Buy Now
- **`VaultMetric`** component — displays shelf-life days, vault temperature, stock in vault

### 5. Cart & Checkout
- **`CartScreen`** — cart item list with increment/decrement/remove, subtotal, clear-all, preservation packaging assurance card
- **`addToCart(product, quantity)`**, **`updateCartItemQuantity(cartItem, delta)`**, **`removeCartItem(cartItemId)`**, **`clearCart()`**
- **`CheckoutScreen`** — fulfillment selection (Thermal Delivery ₱75 / Depot Pickup Free), contact info, payment method (GCash/Bank Transfer/COD/COP), order summary with cost breakdown
- **`placeOrder(...)`** — creates order + order items, generates order number, clears cart, sends notifications

### 6. Role-Based Dashboards

#### Client Dashboard
- **`ClientDashboardScreen`** — identity & wallet banner, food-waste impact metrics, active order tracking with progress indicator, saved address card, recommended produce carousel, support ticket dialog
- **`walletCredits`** — store credit system with top-up dialog + GCash reference
- **`ActiveClientOrderCard`** — order timeline progress (PLACED → PROCESSING → READY_FOR_DISPATCH → IN_TRANSIT → DELIVERED)

#### Farmer Dashboard
- **`FarmerDashboardScreen`** — farm header with stats (gross farmgate, vault stock, target temp), interactive vault telemetry controls, produce catalog management
- **`addProduct(name, category, description, price, unit, stockKg, storageTemp, shelfLifeDays, preservationNotes)`**
- **`editProduct(productId, name, price, stockKg, storageTemp, shelfLifeDays, preservationNotes)`**
- **`updateProductStock(productId, newStock)`** — +10kg/-10kg adjustments
- **`deleteProduct(product)`**
- **`updateOrderStatus(orderId, status)`** — farmer marks orders as PROCESSING or READY_FOR_DISPATCH

#### Rider Dashboard
- **`RiderDashboardScreen`** — rider profile card with stats (active trips, completed, earnings), insulated box thermal sensor display, active deliveries with transit milestones, available order queue, completed trip history
- **`assignRider(orderId, rider)`** — from admin or accepting available orders
- **`completeDelivery(orderId, codCollected, proofNotes)`** — records POD notes and COD collection
- **`requestRiderPayout(amount)`** — submits payout request notification to admin (₱75 per completed trip)

#### Admin Dashboard
- **`AdminDashboardScreen`** — control center overview (GMV, orders, pending approvals, catalog count), 6 sub-panel tabs
- **`approveUser(userId)`**, **`rejectUser(userId)`** — approve/reject farmer & rider applications
- **`toggleProductApproval(productId, currentApproved)`** — toggle product active/delisted
- **`verifyPayment(orderId)`** — transitions payment from PENDING_VERIFICATION to VERIFIED + order to PROCESSING
- **User Management** — search/filter users, suspend/activate accounts
- **Dispatch** — assign/reassign verified riders to unassigned deliveries

### 7. Orders & Notifications
- **Order lifecycle statuses**: PLACED → PROCESSING → READY_FOR_DISPATCH → IN_TRANSIT → DELIVERED/COMPLETED/CANCELLED
- **Payment statuses**: PENDING_VERIFICATION → VERIFIED → PAID / PENDING_COLLECTION → COLLECTED_BY_RIDER
- **Notification system** (`NotificationEntity`) — 5 types: ORDER, HARVEST, PAYMENT, STORAGE_ALERT, APPROVAL; target roles: ALL, CLIENT, FARMER, RIDER, ADMIN
  - **`getNotifications(userId, role)`** — targeted notifications
  - **`markNotificationRead(id)`**

### 8. Data Layer (Room Database)
- **`AppDatabase`** — singleton SQLite database (`tropivault_database.db`), 6 entities, `fallbackToDestructiveMigration()`
- **`TropiVaultDao`** — 30+ DAO methods covering all CRUD operations across users, products, cart, orders, order items, notifications
- **Entities**: `UserEntity`, `ProductEntity`, `CartItemEntity`, `OrderEntity`, `OrderItemEntity`, `NotificationEntity`
- **`DatabaseInitializer`** — seeds 7 users, 8 products, 2 sample orders, 2 notifications on first launch

### 9. Data Persistence
- **`UserPreferences`** — SharedPreferences wrapper for: onboarding completion, active user ID, remember-me flag

### 10. UI Components
- **`TropiVaultTopBar`** — branded top app bar with back button, cart badge, role switcher
- **`TropiVaultBottomBar`** — 4-item navigation: Market, Cart, Orders, Role Dashboard
- **`ProductCard`** — reusable marketplace product card
- **`StatusBadge`** — colored status indicator for order/payment/user states
- **`TropicalPagerIndicator`** — animated dot indicator for onboarding

### 11. Design System
- **Material 3** with custom tropical palette
- **Colors**: ForestGreen (#075B45), DeepGreen (#064E3B), FreshGreen (#219653), GoldenYellow (#F5B700), TropicalOrange (#F59E0B), TropiCream (#FFFDF5)
- **Light & Dark** color schemes with semantic tokens
- **Typography** — Material 3 type scale with DeepGreen primary color

### 12. Flutter Reference Implementation (`mobile/`)
- **`UserModel`** — Dart model class with `UserRole` (client/farmer/rider/admin) and `UserStatus` (approved/pending/rejected) enums, serialization to/from JSON
- **`SessionNotifier`** — Riverpod `StateNotifier` with login, Google sign-in, register (client/farmer/rider), quick role switcher, logout
- **Providers**: `sessionProvider`, `currentUserProvider`, `userRoleProvider`, `isAuthenticatedProvider`, `isClientProvider`, `isFarmerProvider`, `isRiderProvider`, `isAdminProvider`, `isPendingApprovalProvider`

### 13. Testing
- **`ExampleUnitTest`** — JUnit tests for order subtotal calculation and delivery fee logic
- **`ExampleRobolectricTest`** — Robolectric test for string resource retrieval (app name)

### 14. Build Configuration
- **Gradle 9.3.1** with Kotlin 2.2.10 DSL
- **AGP 9.1.1**, **compileSdk 36**, **minSdk 24**, **targetSdk 36**
- **KSP** for Room and Moshi code generation
- **Secrets Gradle Plugin** for `.env` management (Gemini API key)
- **Firebase** — BOM, AI (Gemini), AppCheck (recaptcha + debug)
- **Release signing** with environment variable keystore path

---

## System Progress (Feature Completion)

| Feature | Status | Notes |
|---------|--------|-------|
| Onboarding Flow (2-page PageView) | Complete | Skippable, persisted |
| Public Marketplace (search, filter, sort) | Complete | Category & location chips, featured section |
| Product Detail with Vault Metrics | Complete | Preservation protocol, provenance, quantity |
| Shopping Cart (add, update, remove) | Complete | Live subtotal, clear all, cold-chain assurance |
| Checkout Flow | Complete | Delivery/pickup, 4 payment methods, order summary |
| User Registration (4 roles) | Complete | Instant approval for most; admin key required |
| Login/Logout | Complete | Email+password auth, session persistence |
| Quick Role Switcher | Complete | Instant test-account switching |
| Client Dashboard | Complete | Wallet, order tracking, recommendations, support |
| Farmer Dashboard | Complete | Vault telemetry, product CRUD, order fulfillment |
| Rider Dashboard | Complete | Active trips, POD, available queue, payout request |
| Admin Dashboard | Complete | Overview metrics, approvals, payments, dispatch, catalog, users |
| Order Management | Complete | Full lifecycle from PLACED to DELIVERED |
| Payment Processing | Complete | GCash ref, bank transfer, COD, COP |
| Notification System | Complete | Role-based, 5 types, read tracking |
| Preservation Intelligence | Complete | Storage temps, shelf-life, preservation grades |
| Database Seeding | Complete | 7 users, 8 products, 2 orders, 2 notifications |
| Design System (Material 3) | Complete | Tropical color palette, typography, dark mode |
| Flutter Reference | Complete | UserModel + SessionProvider (Riverpod) |
| Unit Tests | Complete | Order calculations, resource retrieval |
| Instrumented Tests | Complete | Scaffold configured |

---

## Future Suggestions for Improvement

### High Priority

1. **Payment Gateway Integration** — Currently mock/simulated. Integrate real GCash API, Dragonpay/BillEase for bank transfers, and card payments via Stripe or Xendit.
2. **Backend/API Server** — Migrate from local Room-only to a cloud backend (Firebase Firestore/Firebase Functions or Node.js/Express + PostgreSQL) for multi-device sync, real user accounts, and order processing.
3. **Real Image Loading** — Currently uses placeholder icons. Integrate Coil with Cloudinary or Firebase Storage for real product/farm photos.
4. **Password Security** — Replace plaintext passwords with hashed authentication (bcrypt/Argon2 on backend, or at minimum local hashing).
5. **Email/SMS Notifications** — Add FCM push notifications for order updates, payment confirmations, and delivery alerts.
6. **Order History Detail** — Add a dedicated order detail screen showing line items, delivery tracking map, and proof-of-delivery.
7. **Rating & Reviews** — Allow clients to rate farmers and products post-delivery; display on product cards and farmer profiles.
8. **Real-time Inventory Sync** — When stock reaches 0, auto-delist products; show low-stock warnings on farmer dashboard.

### Medium Priority

9. **Map Integration** — Add Google Maps SDK for delivery tracking (rider location, route visualization) and depot/farm location display.
10. **Barcode/QR Scanning** — Allow riders to scan order barcodes for quick POD recording and order lookup.
11. **Advanced Analytics Dashboard** — Admin charts for GMV trends, order volume, top-selling products, and regional performance.
12. **Multi-language Support** — Add Filipino (Tagalog) localization alongside English.
13. **Wishlist/Favorites** — Allow clients to bookmark favorite orchards and products for quick re-order.
14. **Promo Codes & Voucher System** — Discount codes for bulk orders, first-time buyers, and seasonal promotions.
15. **Farmer Production Scheduling** — Calendar view for harvest planning, planting reminders, and vault scheduling.
16. **Cold-Chain IoT Integration** — Connect actual temperature/humidity sensors to the vault telemetry display for real-time monitoring.
17. **Bulk Order / B2B Features** — Wholesale pricing tiers, order templates, and recurring order scheduling for restaurants/hotels.
18. **Dispute Resolution** — Allow clients/riders to dispute POD or COD collection, with admin mediation flow.
19. **Export/Import Reports** — Admin can export order, farmer, rider, and financial data as CSV/PDF.
20. **Dark Mode Refinement** — Complete and polish the dark color scheme throughout all screens.

### Low Priority / Future Considerations

21. **Loyalty Program** — Accumulate points per purchase, redeemable for discounts or free delivery.
22. **Subscription Model** — Monthly "Vault Box" subscription with curated seasonal produce.
23. **Community Forum** — In-app chat or forum for farmers to share best practices and weather updates.
24. **AI-Powered Recommendations** — Use Gemini AI for personalized product recommendations based on purchase history.
25. **Sustainability Impact Dashboard** — Track and display food waste prevented, CO2 saved, and farmer earnings uplift.
26. **QR-Based Authentication** — Admin can approve users via QR scan on device.
27. **Offline-First Resilience** — Enhance Room caching with conflict resolution strategies for when backend sync resumes.

---

## How to Run

```bash
# Build
./gradlew assembleDebug        # Linux/macOS
gradlew.bat assembleDebug     # Windows

# Test
./gradlew :app:testDebugUnitTest

# Install & Run
./gradlew installDebug
adb shell am start -n com.aistudio.tropivault.kvzpqa/com.example.MainActivity
```

See `README.md` for full VS Code setup guide.
