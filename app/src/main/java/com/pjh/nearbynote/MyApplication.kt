package com.pjh.nearbynote

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

//Application class
@HiltAndroidApp
class MyApplication : Application() {

    /*        override fun onCreate() {
            super.onCreate()

            // use this code when you need to delete previous (room) database
            deleteDatabase("nearby_note.db")
        }*/

    // Useful prompt command, clean and rebuild
    // ./gradlew clean
    // ./gradlew build
    // ./gradlew build --refresh-dependencies
}