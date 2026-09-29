# 🌾 FarmVault
> **Good Harvests. Longer Tomorrows.**  
> *Tropical Agriculture Marketplace & Climate-Controlled Food Preservation Management System*

[![Platform](https://img.shields.io/badge/Platform-Android%20%7C%20Jetpack%20Compose-075B45?style=flat-square&logo=android)](https://developer.android.com/jetpack/compose)
[![Architecture](https://img.shields.io/badge/Architecture-MVVM%20%2B%20Room%20%2B%20Flow-F5B700?style=flat-square)](https://developer.android.com/topic/architecture)
[![Design](https://img.shields.io/badge/Design-Material%203%20Tropical-219653?style=flat-square)](https://m3.material.io)
[![Multi--Role](https://img.shields.io/badge/Roles-Client%20%7C%20Farmer%20%7C%20Rider%20%7C%20Admin-064E3B?style=flat-square)](#-role-based-user-journeys)

---

## 📖 Overview

**FarmVault** directly bridges Filipino and Southeast Asian fruit and crop growers with households, culinary businesses, and markets. By integrating solar-powered, climate-controlled cold storage vaults with real-time crop telemetry and transparent logistics, FarmVault slows natural fruit respiration, dramatically cuts post-harvest food waste, guarantees fair farmgate prices, and extends produce shelf-life by up to 3x.

---

## 🌟 How It Works

```
 ┌──────────────────────────────────────────────────────────────────────────┐
 │                                FARMVAULT                                 │
 └──────────────────────────────────────────────────────────────────────────┘
         │
         ├── 1. Onboarding PageView (Get Started ──► How It Works ──► Public Market)
         │
         ├── 2. Marketplace & Vault Preservation
         │      ├── Live Search & Category Filtering (Mangoes, Bananas, Citrus, etc.)
         │      ├── Provenance by Origin (Guimaras, Davao, Bukidnon, Batangas)
         │      └── Real-time Vault Metrics: 12°C Solar-Chill • Nitrogen Vault • Shelf-Life Days
         │
         ├── 3. Transparent Multi-Role Architecture
         │      ├── 🛒 Client / Buyer    : Browse, Add to Cart, Thermal Delivery or Pickup
         │      ├── 🚜 Farmer Partner    : List Crops, Vault Telemetry, Stock Control, Fulfillment
         │      ├── 🛵 Logistics Rider   : Accept Orders, Transit Steps, POD & COD Cash Receipt
         │      └── 🛡️ Admin Overseer    : Verify Farmers/Riders, Verify GCash/Banks, Assign Riders
         │
         └── 4. Checkout & Financial Settlement
                ├── GCash e-Wallet (Account: 0917-888-VAULT + Reference Tracking)
                ├── Philippine Banks (BDO, BPI, Metrobank, UnionBank, LandBank)
                ├── Cash on Delivery (COD with Rider Receipt Verification)
                └── Cash on Pickup (COP at Regional Cold Hubs)
```

### 1. Unified Onboarding (`PageView` / `HorizontalPager`)
- **Page 0 (Get Started):** Cinematic hero harvest visual, signature brand emblem, tagline, primary **Get Started** button, **Log In** button, and **Skip to Marketplace**.
- **Page 1 (How It Works):** 4 feature cards explaining *Discover Tropical Products*, *Support Local Farmers*, *Order & Pay Easily*, and *Delivery or Pickup*, with animated indicator dots and direct **Explore Marketplace** navigation.
- **Persistence:** Onboarding status is remembered locally in preferences so returning users go directly to the marketplace.

### 2. Public Marketplace & Preservation Engine
- **Open Browsing:** Any guest can search, filter by crop category or provincial origin, sort by price or longest shelf-life, and inspect harvest provenance without being forced to log in first.
- **Preservation Intelligence:** Every crop showcases live vault conditions (e.g. *12°C Controlled Atmosphere*, *Cryo-Freeze Drying*, *Ozone Sanitized Chill*), storage temperatures, and remaining days of peak freshness.

### 3. Role-Based User Journeys

| Role | Core Responsibilities & Features |
| :--- | :--- |
| **Client / Buyer** | Browse harvests, add produce to cart with quantity controls, select Thermal Delivery (₱75) or Depot Pickup (Free), check out with GCash / Bank / COD / COP, and track active deliveries. |
| **Farmer Partner** | Access Farm Vault dashboard, register new crop harvests with preservation specifications, adjust stock levels in real time (+/-10kg), monitor nitrogen & solar humidity telemetry, and mark incoming orders as *Ready for Dispatch*. |
| **Rider Partner** | Inspect vehicle profile (e.g. Motorcycle with Insulated Vault Box), browse and accept available trips, update transit milestones (*Ready* ➔ *In Transit* ➔ *Delivered*), and record Proof of Delivery (POD) notes and COD cash collected. |
| **Administrator** | Oversee platform metrics (Total GMV, crop listings, active orders), review and approve/reject pending Farmer & Rider partner applications, verify GCash and Bank transfer reference numbers, and assign riders to deliveries. |

---

## 🖥️ Separate, Fully Functional Dashboards

TropiVault provides four completely independent, specialized operational dashboards tailored for each user role:

### 1. 🛒 Client Dashboard (`ClientDashboardScreen`)
- **Customer Control Center**: Live order status timeline (`PLACED` ➔ `PROCESSING` in climate vault ➔ `IN_TRANSIT` with thermal rider ➔ `DELIVERED`).
- **One-Tap Actions**: Confirm order delivery, cancel pending orders, update delivery address.
- **Produce Freshness Tracker**: Active shelf-life tracker indicating days of freshness remaining for purchased fruits.
- **Store Credit Wallet**: Real-time balance with GCash top-up dialog.
- **Quick Re-order**: Instant 1-tap re-ordering for favorite orchards and seasonal produce.

### 2. 🚜 Farmer Dashboard (`FarmerDashboardScreen`)
- **Farm Vault Overview**: Crop inventory, total stock in kg, gross farmgate revenue, and orders awaiting packing.
- **Smart Climate Telemetry**: Target temperature setpoint slider, Nitrogen controlled atmosphere toggle, humidity sensor, solar battery monitor.
- **Produce Catalog Management**:
  - Add new harvest dialog (name, category, price, stock kg, storage temperature, shelf-life days, preservation notes).
  - Edit harvest dialog, instant `+10kg` / `-10kg` stock adjustments, delist/delete crop.
- **Fulfillment Operations**: View incoming orders for the farm's produce; mark *"Packed & Stored in Climate Vault"* or *"Ready for Rider Dispatch"*.

### 3. 🛵 Rider Dashboard (`RiderDashboardScreen`)
- **Transit Terminal**: Active delivery trip card with pickup origin, drop-off recipient name, phone, and delivery address.
- **Thermal Preservation Telemetry**: Real-time monitoring of the motorcycle insulated cold-box sensor (11.8°C).
- **Delivery Workflow**: One-tap *"Start Transit"* and *"Complete Delivery & POD"*.
- **Proof of Delivery (POD)**: Record customer receipt notes and verify Cash on Delivery (COD) collection.
- **Available Delivery Queue**: Inspect unassigned orders across regional hubs and claim trips with 1 tap.
- **Earnings & Payouts**: Completed deliveries tracker (₱75 per completed trip) and GCash payout request modal.

### 4. 🛡️ Administrator Dashboard (`AdminDashboardScreen`)
- **Control Center Oversight**: Total platform GMV, total orders, pending approvals, and active catalog count.
- **Sub-Panels**:
  - **Approvals**: Review partner applications for Farmers and Riders; 1-tap Approve & Activate or Reject.
  - **Payments**: Real-time verification of GCash references and bank deposit slips, transitioning payments from `PENDING_VERIFICATION` to `PAID`.
  - **Dispatch**: Assign and reassign verified drivers to unassigned customer deliveries.
  - **Catalog Moderation**: Toggle active or delisted status for any crop in the marketplace. When farmers upload harvest photos via the AI scanner, admin can approve them with 1 tap to auto-publish them directly to the marketplace with the farmer's uploaded image.
  - **User Management**: Search and inspect all registered clients, farmers, riders, and administrators; suspend or activate accounts.

---

## 🖥️ How to Use the Admin Dashboard Separately on a Laptop or Desktop

You can run and monitor the Administrator Dashboard separately on a laptop, desktop PC, or tablet while riders and clients use their mobile devices:

### Method 1: Web Browser Access (Direct Cloud Instance)
1. On your laptop or desktop computer, open any web browser (Google Chrome, Microsoft Edge, Safari, or Mozilla Firefox).
2. Navigate to the deployed live application URL:
   ```
   https://ais-pre-uqlgzccj5iap2cckqetss2-119534462456.asia-southeast1.run.app
   ```
3. Click **Log In** and enter the dedicated admin credentials:
   - **Email:** `admin@store.com`
   - **Password:** `admin123`
4. The system will open directly into the **TropiVault Control Center**:
   - The desktop-wide viewport displays platform metrics, partner KYC verifications, GCash payment reference verifications, live rider dispatch, and produce moderation simultaneously.
   - You can leave this browser window open on a second monitor as a live operations command center.

### Method 2: Local VS Code Desktop Development Setup
1. If you are developing locally on your laptop, launch the project in VS Code:
   ```bash
   code /path/to/tropivault
   ```
2. Run the application on an Android Emulator configured with tablet or desktop screen dimensions (e.g. 1920x1080 resolution).
3. Log in with `admin@store.com` / `admin123`. The responsive Compose layout automatically scales to take full advantage of wide screens.

---

## 👤 Real Account Registration (No Demo Accounts Required)

TropiVault is a fully functional system where you can register real accounts for **any** role directly from the app:
1. Tap **Create Account** on the Login screen.
2. Select your role:
   - **Client / Buyer**: Register with your name, email, phone, delivery address, and password. Immediately enters the **Client Dashboard**.
   - **Farmer Partner**: Register with your name, farm name, location, and password. Immediate activation opens the **Farmer Dashboard**.
   - **Rider Partner**: Register with your vehicle type, license number, and password. Immediate activation opens the **Rider Dashboard**.
   - **Administrator**: Enter your credentials alongside the master admin key (`TROPI2026`) to instantly access the **Admin Dashboard**.
3. All registered users, products, orders, and wallet transactions are persistently stored in the local SQLite Room database.

---

## 💻 How to Run on VS Code (Step-by-Step)

Follow these instructions to clone, build, and debug TropiVault on your local machine using **Visual Studio Code**.

### Step 1: System Prerequisites
Make sure you have the following installed on your machine:
1. **Java Development Kit (JDK 17 or JDK 21)**
   - Verify with: `java -version`
2. **Android SDK / Command-Line Tools**
   - Typically installed via Android Studio or standalone cmdline-tools.
   - Set environment variables in your shell (`~/.bashrc`, `~/.zshrc`, or Windows Environment Variables):
     ```bash
     export ANDROID_HOME=$HOME/Android/Sdk
     export PATH=$PATH:$ANDROID_HOME/emulator
     export PATH=$PATH:$ANDROID_HOME/platform-tools
     export PATH=$PATH:$ANDROID_HOME/cmdline-tools/latest/bin
     ```
3. **Visual Studio Code** ([Download](https://code.visualstudio.com/))

---

### Step 2: Install Recommended VS Code Extensions
Open VS Code, press `Ctrl+P` (or `Cmd+P` on macOS), and install these extensions:
- **Kotlin** (`fwcd.kotlin`)
- **Gradle for Java** (`vscjava.vscode-gradle`)
- **Extension Pack for Java** (`vscjava.vscode-java-pack`)
- **Android iOS Emulator** (`Diatemb.vscode-android-ios-emulator`) *(Optional for quick device launching)*

---

### Step 3: Open the Project in VS Code
1. Launch VS Code:
   ```bash
   code /path/to/tropivault
   ```
2. Open an integrated terminal in VS Code (`Ctrl+\`` or `Terminal` ➔ `New Terminal`).

---

### Step 4: Validate Gradle & Build the App
Run the Gradle assemble task to download dependencies and verify build integrity:

```bash
# On Linux / macOS:
./gradlew assembleDebug

# On Windows:
gradlew.bat assembleDebug
```

> **Note:** If Gradle gives a permission error on macOS/Linux, run `chmod +x gradlew`.

---

### Step 5: Run Unit and Local Robolectric Tests
Verify all business logic and calculations:

```bash
./gradlew :app:testDebugUnitTest
```
All unit tests should complete with `BUILD SUCCESSFUL`.

---

### Step 6: Launch on an Android Emulator or Physical Device
1. **Start an Android Emulator** (or connect your physical Android phone with USB Debugging enabled):
   ```bash
   # List available emulators:
   emulator -list-avds

   # Start an emulator:
   emulator -avd <Your_AVD_Name>
   ```
2. Verify ADB detects the device:
   ```bash
   adb devices
   ```
3. **Install and run the Debug APK:**
   ```bash
   ./gradlew installDebug
   ```
4. **Launch TropiVault on the device via ADB:**
   ```bash
   adb shell am start -n com.aistudio.tropivault.kvzpqa/com.example.MainActivity
   ```

---

### Step 7: (Optional) VS Code Debugging Configuration (`launch.json`)
To enable 1-click debugging in VS Code, create `.vscode/launch.json`:

```json
{
  "version": "0.2.0",
  "configurations": [
    {
      "type": "java",
      "name": "Run TropiVault Unit Tests",
      "request": "launch",
      "mainClass": "",
      "projectName": "app"
    }
  ]
}
```

---

## 🎨 Tropical Design System & Palette

| Token | Hex | Role & Visual Usage |
| :--- | :--- | :--- |
| **Forest Green** | `#075B45` | Primary brand color, app bars, primary action buttons, headers |
| **Deep Green** | `#064E3B` | High-contrast typography, dark container surfaces, footers |
| **Fresh Green** | `#219653` | Secondary CTAs, Add-to-Cart buttons, positive approval badges |
| **Golden Yellow** | `#F5B700` | Accent badges, shelf-life highlights, brand icons, logo emblem |
| **Tropical Orange**| `#F59E0B` | Ratings, urgency tags, warning indicators, and telemetry |
| **TropiCream** | `#FFFDF5` | Warm natural background evoking tropical farm freshness |
| **White** | `#FFFFFF` | Card surfaces, modals, elevated sheets, input backgrounds |

---

## 📁 Repository Structure

```text
tropivault/
├── app/
│   ├── build.gradle.kts           # App-level build script with Room, Compose, Coil
│   └── src/
│       ├── main/
│       │   ├── AndroidManifest.xml # Permissions (INTERNET, VIBRATE) & activities
│       │   ├── java/com/example/
│       │   │   ├── MainActivity.kt # Root Activity, Navigation routing & Top/Bottom Scaffolds
│       │   │   ├── tropivault/
│       │   │   │   ├── data/
│       │   │   │   │   ├── local/      # Room AppDatabase, DAOs, Entities, Seed Initializer
│       │   │   │   │   └── repository/ # TropiVaultRepository & UserPreferences
│       │   │   │   ├── model/          # Domain User, UserRole, UserStatus, UserSessionState
│       │   │   │   └── ui/
│       │   │   │       ├── TropiVaultViewModel.kt # StateFlow reactive state & actions
│       │   │   │       ├── components/ # Reusable ProductCard, TopBar, BottomBar, Badges
│       │   │   │       ├── screens/    # OnboardingScreen (PageView), Market, Detail, Cart,
│       │   │   │       │               # Checkout, Auth, Farmer, Rider, Admin, Profile
│       │   │   │       └── theme/      # Color.kt, Theme.kt, Type.kt
│       │   └── res/
│       │       ├── drawable/          # Vector drawables & AI-generated harvest visuals
│       │       └── mipmap-*/          # Adaptive TropiVault launcher icons
│       └── test/                      # Unit & Robolectric test suites
├── mobile/lib/                        # Cross-platform Flutter / Riverpod reference models
│   ├── models/user_model.dart         # UserModel with role differentiation & serialization
│   └── providers/session_provider.dart# Riverpod StateNotifierProvider & role selectors
├── settings.gradle.kts
└── README.md                          # Project documentation and VS Code run guide
```

---

## 🥭 Summary of Features Implemented
- [x] **Complete Onboarding Flow:** `Get Started` ➔ `How It Works` (with `HorizontalPager` / `PageView`) ➔ `Public Marketplace`.
- [x] **Public Marketplace:** Search by crop/farm, filter by category/location, and sort by shelf-life or price.
- [x] **Vault Preservation Metrics:** Live storage temperatures, hypobaric preservation protocols, and remaining freshness days.
- [x] **Real Cart & Checkout:** Fulfillment selection (Delivery vs Regional Depot Pickup), breakdown calculations.
- [x] **Multi-Payment Integration:** GCash (account info + reference verification), Bank Transfer, COD, and Cash on Pickup.
- [x] **Role Portals:** Dedicated dashboards for Farmers (crop inventory & vault telemetry), Riders (delivery trips & POD), and Admins (partner approvals & payment checks).
- [x] **Instant Role Switcher:** Fast switching between test accounts without manual login.
- [x] **Adaptive App Icon:** Custom golden mango and vault leaf emblem on `#075B45`.
