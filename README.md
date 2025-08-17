# NearbyNote

## Google Play link (Coming Soon)

A link to the app on Google Play will be added here.

## App Overview

**NearbyNote** is a smart location-based memo app that automatically notifies you of your notes when you arrive at specific locations.  
It helps you remember important tasks based on where you are.

**Example Use Cases:**
- When you arrive at the office: "Prepare for today's meeting"
- When you get to the grocery store: "Buy fruits and eggs"
- When you get home: "Open the windows"

You can quickly create notes via voice recognition or text input,  
and easily save locations using the interactive map.

---

## Key Features

### 1. Note Creation
- Create notes via text or voice input *(currently supports English only)*
- Converts voice to text using the built-in Android SpeechRecognizer.

### 2. Location-Based Notes Notifications
- Set a location and geofence radius for each note
- Get notified automatically when you enter the defined area

### 3. Map Integration
- Map UI powered by Mapbox Maps SDK (v11)
- Address search powered by Mapbox Geocoding API
- Create notes by clicking on the map or searching for an address
- View notes by saved location

### 4. Accurate Location Detection & Recovery
- Continues location tracking even after app termination, reboot, or in the background
- Foreground service runs only when at least one location-based note exists
- Uses optimized Android APIs (Geofencing + Google Play Services Location), designed for low battery usage
- Restores geofences after reboot

### 5. Permissions Management UI
- A dedicated page shows the current status of all permissions at a glance

---

## Tech Stack

| Category         | Technology |
|------------------|------------|
| UI               | Jetpack Compose |
| Architecture     | MVVM, Hilt (DI) |
| Storage          | Room Database |
| Async            | Kotlin Coroutines, Flow |
| Maps & Location  | Mapbox Maps SDK (v11), Mapbox Geocoding API, Google Play Services Location (Geofencing) |
| Voice            | Android SpeechRecognizer |
| Network          | Ktor + kotlinx.serialization |
| Background Tasks | Foreground service, BroadcastReceiver, Notification |
| Permissions      | Accompanist Permissions |
| Testing          | JUnit, MockK, Turbine |
| CI/CD            | GitHub Actions |

---

## Platform & Requirements
- **Android 11+ (API 30+)**, target **API 35**

---

## Privacy (Short)
- No account, no analytics or crash reporting
- Notes are stored on-device
- Network calls only to Mapbox Geocoding for address search
- Full policy: see `docs/privacy-policy.md`

## Screenshots (Coming Soon)

Screenshots of the app UI will be added here.
