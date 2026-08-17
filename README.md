# Worker–Employer Marketplace (Native Android)

A complete, production-grade native Android marketplace application connecting workers and employers across Ethiopia and Africa, featuring intelligent skill/location matching, real-time messaging, map radars, application pipelines, ID verification, and administrative moderation.

---

## 🛠️ Technology Stack & Architecture

- **Language:** Kotlin 2.2+ (100% Kotlin DSL)
- **UI Framework:** Jetpack Compose with Material 3 Design
- **Architecture:** Clean Architecture & MVVM with StateFlow & Coroutines
- **Local Persistence:** Room Database for offline resilience and cache synchronization
- **Backend & Cloud:** Firebase Authentication, Cloud Firestore, Firebase Storage, Firebase App Check, Cloud Messaging (FCM)
- **Mapping & Location:** Google Maps Platform / Android Location Services
- **CI/CD:** GitHub Actions workflow for automated Debug and Release APK builds

---

## 📁 Project Structure

```text
WorkerEmployerApp/
├── app/
│   ├── build.gradle.kts
│   └── src/
│       ├── main/
│       │   ├── AndroidManifest.xml
│       │   ├── java/com/example/
│       │   │   ├── MainActivity.kt
│       │   │   ├── data/
│       │   │   │   ├── MarketplaceRepository.kt
│       │   │   │   └── MatchingEngine.kt
│       │   │   ├── model/
│       │   │   │   └── Models.kt
│       │   │   ├── ui/
│       │   │   │   ├── components/
│       │   │   │   │   └── CommonComponents.kt
│       │   │   │   ├── screens/
│       │   │   │   │   ├── AdminAndSettingsScreens.kt
│       │   │   │   │   ├── AuthScreen.kt
│       │   │   │   │   ├── ChatScreens.kt
│       │   │   │   │   ├── EmployerScreens.kt
│       │   │   │   │   ├── MapScreen.kt
│       │   │   │   │   └── WorkerScreens.kt
│       │   │   │   └── theme/
│       │   │   │       ├── Color.kt
│       │   │   │       ├── Theme.kt
│       │   │   │       └── Type.kt
│       │   │   └── viewmodel/
│       │   │       └── MarketplaceViewModel.kt
│       │   └── res/
│       │       ├── drawable/
│       │       ├── mipmap-*/
│       │       ├── values/ (English default)
│       │       ├── values-am/ (Amharic - አማርኛ)
│       │       ├── values-om/ (Oromo - Afaan Oromoo)
│       │       ├── values-ti/ (Tigrinya - ትግርኛ)
│       │       └── values-so/ (Somali - Soomaali)
│       └── test/ (Unit & Robolectric Tests)
├── .github/workflows/
│   └── android-apk.yml
├── firestore.rules
├── storage.rules
├── build.gradle.kts
├── settings.gradle.kts
├── gradle.properties
└── README.md
```

---

## 🔥 Firebase Setup Instructions

### 1. Place `google-services.json`
1. Go to the [Firebase Console](https://console.firebase.google.com/).
2. Create an Android app with package name matching your Application ID:
   `com.aistudio.workermarketplace.ethio` (or your configured `applicationId`).
3. Download `google-services.json` and place it in the `app/` folder:
   ```text
   /app/google-services.json
   ```

### 2. Deploy Firestore & Storage Security Rules
Deploy the included role-based security rules using Firebase CLI:
```bash
firebase deploy --only firestore:rules
firebase deploy --only storage:rules
```

---

## 🗺️ Google Maps Integration

Add your Google Maps API key to your environment or `.env` file:
```properties
MAPS_API_KEY=YOUR_GOOGLE_MAPS_API_KEY
```

---

## 🌐 Supported Languages

1. **English** (Default)
2. **አማርኛ (Amharic)**
3. **Afaan Oromoo (Oromo)**
4. **ትግርኛ (Tigrinya)**
5. **Soomaali (Somali)**

Switch languages dynamically from the in-app **Settings** menu.

---

## 🚀 Building the Project

### Command Line
```bash
# Build Debug APK
./gradlew assembleDebug

# Build Release APK
./gradlew assembleRelease

# Run Unit Tests
./gradlew test
```

### GitHub Actions CI/CD
Whenever code is pushed to `main`, GitHub Actions runs the workflow in `.github/workflows/android-apk.yml` and uploads the built debug APK as an artifact.
