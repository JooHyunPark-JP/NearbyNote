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

You can quickly create notes via **voice recognition** or **text input**,  
and easily save locations using the interactive map.

---

## Key Features

### 1. Note Creation
- Create notes via text or voice input *(currently supports English only)*
- Converts voice to text using the Google Speech API

### 2. Location-Based notes Notifications
- Set a location and geofence radius for each note
- Get notified automatically when you enter the defined area

### 3. Map Integration
- Map UI powered by Mapbox
- Create notes by clicking on the map or searching an address
- View notes by saved location

### 4. Accurate Location Detection & Recovery
- Continues location tracking even after app termination, reboot, or in background
- ForegroundService (Only runs when at least one location-based note exists) ensures consistent and accurate location updates
- Uses optimized Android APIs (Geofencing, Location Services), resulting in low battery usage

### 5. Permissions Management UI
- A dedicated page shows the current status of all permissions at a glance

---

## Tech Stack

| Category         | Technology                          |
|------------------|--------------------------------------|
| UI               | Jetpack Compose                     |
| Architecture     | MVVM, Hilt (Dependency Injection)   |
| Storage          | Room Database                       |
| Async            | Kotlin Coroutines, Flow             |
| Maps & Location  | Mapbox Places API, Geofencing API   |
| Voice            | Google Speech API                   |
| Network          | Ktor + kotlinx.serialization        |
| Background Tasks | ForegroundService, BroadcastReceiver, Notification |
| Permissions      | Accompanist                         |
| Testing          | JUnit, MockK                        |
| CI/CD            | GitHub Actions (automated testing & APK build) |

---

## Screenshots (Coming Soon)

Screenshots of the app UI will be added here.


## 📸 Screenshots (Coming Soon)

Screenshots of the app UI will be added here.


