# NearbyNote

**NearbyNote** is a smart location-based memo app that reminds you of your notes when you arrive at a specific location.

- Quickly create notes using voice recognition or typing  
- Set location-based alerts with customizable geofence radius  
- See your saved notes directly on the map  
- Get notified automatically when you enter a saved location zone — even when the app is not running

---

## Key Features

### Location-Based Reminders
- Uses Geofencing API to detect when entering a defined area
- Automatically sends notifications and opens the related note
- Geofences are restored after device reboot (WorkManager + BootCompleteReceiver)

### Voice-to-Text Notes
- Leverages Google Speech Recognition to create notes with your voice
- Fast and hands-free note creation

### Map-Driven Note Management
- Search locations with Mapbox Places API autocomplete
- View and manage notes directly on the map
- Tap any location on the map to add a note instantly

### General Notes (No Location)
- Create, edit, and delete notes without location data
- Mix regular notes and geofenced notes seamlessly

### Smart Notifications
- Keeps detecting location in the background (Foreground Service)
- Automatically notifies and displays the memo even when app is closed

### Permission UX
- Clear separation and request flow for foreground & background location permissions
- Custom permission status page for transparency
- Fully supports Android 11+ UX constraints (via Accompanist)

---

## Tech Stack

| Category | Technology |
|---------|------------|
| UI | Jetpack Compose |
| Architecture | MVVM, Hilt (DI) |
| Storage | Room Database |
| Async | Kotlin Coroutines, Flow |
| Maps & Location | Mapbox Places API, Geofencing API |
| Voice | Google Speech API |
| Permissions | Accompanist Permissions |
| Network | Ktor + kotlinx.serialization |
| Background Tasks | WorkManager, ForegroundService, BootCompleteReceiver |
| Testing | JUnit, MockK |
| CI/CD | GitHub Actions (automated testing & APK build)


## 📸 Screenshots (Coming Soon)

Screenshots of the app UI will be added here.

---

## 📦 Installation

```bash
# APK download or Play Store link will be added soon


