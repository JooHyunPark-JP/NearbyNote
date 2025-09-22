# NearbyNote

## Quick Start
1. Download the **APK** from the [latest release](https://github.com/JooHyunPark-JP/NearbyNote/releases/latest)
2. Install on your Android device.

## Screenshots
<img src="./image/nearbynote_notelist.png" alt="Example Image" width="200"/> <img src="./image/nearbynote_writenote.png" alt="Example Image" width="200"/> 
<img src="./image/nearbynote_setlocation.png" alt="Example Image" width="200"/> <img src="./image/nearbynote_mapview.jpg" alt="Example Image" width="200"/> 
<img src="./image/nearbynote_readnoteonmap.png" alt="Example Image" width="200"/> <img src="./image/nearbynote_favouriteaddress.jpg" alt="Example Image" width="200"/> 

# App Overview

**NearbyNote** is a smart location-based memo app. Create a note (text or voice), set a place + radius, and get a notification when you enter the area.

**Example Use Cases:**
- Arriving at the office → “Prepare for today’s meeting”
- Getting to the grocery store → “Buy fruits and eggs”
- Reaching home → “Open the windows”
  
---

## Key Features

### 1. Note Creation
- Create notes via text or voice (currently defaults to English)
- Uses Android SpeechRecognizer to convert voice to text

### 2. Location-Based Notes Notifications
- Set a location and geofence radius per note
- Notifications fire on ENTER events

### 3. Map Integration
- Map UI powered by Mapbox Maps SDK (v11)
- Address search powered by Mapbox Geocoding API
- Create notes by tapping on the map or searching for an address
- View notes by saved location

### 4. Saved Places (Favourites)
- Save frequently used addresses; reuse when creating new notes

### 5. Reliable in the Background
- No persistent foreground service
- Works after app termination, reboot, and in background
- Uses Google Play services Geofencing, BroadcastReceivers
- Uses optimized Android APIs (Geofencing + Google Play Services Location), designed for low battery usage
- Periodic `WorkManager` job to reconcile/re-register geofences

### 6. Permissions Management UI
- Dedicated screen showing Location / Background Location / Notifications / Microphone status

---

## Tech Stack

| Category         | Technology |
|------------------|------------|
| UI               | Jetpack Compose (Material 3) |
| Architecture     | MVVM, Hilt (DI) |
| Storage          | Room Database |
| Async            | Kotlin Coroutines, Flow |
| Maps & Location  | Mapbox Maps SDK (v11), Mapbox Geocoding API, Google Play Services Location (Geofencing) |
| Voice            | Android SpeechRecognizer |
| Network          | Ktor + kotlinx.serialization |
| Background Tasks | BroadcastReceiver, WorkManager (reconcile), Notifications (no foreground service) |
| Permissions      | Accompanist Permissions |
| Testing          | JUnit, MockK, Turbine |
| CI/CD            | GitHub Actions |
