# NearbyNote

## App Overview

**NearbyNote** is a smart location-based memo app that automatically notifies you of your notes when you arrive at specific locations.

**Example Use Cases:**
- Arriving at the office → “Prepare for today’s meeting”
- Getting to the grocery store → “Buy fruits and eggs”
- Reaching home → “Open the windows”

You can quickly create notes via voice recognition or text input,  
and easily save locations by auto address searching or using the interactive map.

---

## Key Features

### 1. Note Creation
- Create notes via text or voice (currently defaults to en‑US)
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
- Save frequently used addresses and reuse them when creating notes
- Manage/edit favourites to speed up new note setup.

### 5. Reliable in the Background
- No persistent foreground service
- Continues location tracking even after app termination, reboot, or in the background
- Uses Google Play services Geofencing, BroadcastReceivers, and a periodic WorkManager job to reconcile/re‑register geofences as needed.
- Uses optimized Android APIs (Geofencing + Google Play Services Location), designed for low battery usage
- Recovers after reboot and app updates

### 6. Permissions Management UI
- A dedicated screen surfaces the current status of critical permissions (Location, Background Location, Notifications, Microphone) so users can resolve gaps quickly

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


## Screenshots (Coming Soon)

Screenshots of the app UI will be added here.

## Google Play link (Coming Soon)

A link to the app on Google Play will be added here.
