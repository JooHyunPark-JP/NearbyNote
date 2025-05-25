# 📱 NearbyNote - Location-Based Notification App

**_Currently developing_**...

---

## 🌟 Overview
NearbyNote is a location-based note notification application that allows users to set specific locations and write notes related to those places.  
When the user approaches the designated location, an automatic notification pops up, displaying the corresponding note.  
For example, if you set a note saying **"Open the window"** when you arrive home, the app will automatically remind you when you get near your house.  

---

## 🌟 Key Features

### 📍 Location-Based Notifications
- Uses the **Geofencing API** to detect a specified location range and display notifications when entering.  
- Alternatively, utilizes **Geofence + BroadcastReceiver + WorkManager** for efficient management.  
- Combines **GPS and Wi-Fi signals** to minimize battery consumption.  
- The **location range** can be customized by the user.  

---

### 🗺️ Address Autocomplete
- Integrates **Mapbox Places API** to provide address autocomplete functionality.  
- Users can **manually enter or select an address** from the suggestions.  

---

### 🗣️ Voice and Text Notes
- Allows users to write notes using **voice or text** and group them by location.  
- The created notes are displayed as **notifications** when the location is detected.  

---

### 🔔 Notification and Note Management
- Manages notes for each **configured location** and displays them as a list.  
- When a notification occurs, the app **reopens and displays the note** on the screen.  

---

### ⚡ User Convenience
- Operates in **low-power mode** to reduce battery consumption.  
- Ensures continuity using **WorkManager**, even when the app is closed.  

---

## 💻 Skill Tech Stack
- **Jetpack Compose:** Modern, reactive UI.  
- **MVVM Architecture:** Clean separation of concerns.  
- **Hilt:** Dependency injection for modular code.  
- **Room Database:** Offline data storage.  
- **Coroutines + Flow:** Asynchronous programming for smooth performance.
- **Ktor**
- **Kotlinx Serialization**
- JUnit + MockK
- Api: Geofencing API, mapbox places api
- BroadcastManager + Workmanager

---


