plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)

    alias(libs.plugins.kotlin.ksp)

    alias(libs.plugins.hilt.ksp)

    alias(libs.plugins.kotlin.serialization)

}

val mapboxApiKey = project.findProperty("MAPBOX_API_KEY") as String

val keystorePassword = project.findProperty("KEYSTORE_PASSWORD") as String
val keyPassword = project.findProperty("KEY_PASSWORD") as String

android {
    namespace = "com.example.nearbynote"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.nearbynote"
        minSdk = 30
        targetSdk = 35
        versionCode = 1
        versionName = "1.0.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        resValue("string", "mapbox_access_token", mapboxApiKey)
        buildConfigField("String", "MAPBOX_API_KEY", "\"${mapboxApiKey}\"")
    }

    signingConfigs {
        create("release") {
            storeFile = file("app/keystore/nearbynote-release.jks")
            storePassword = keystorePassword
            keyAlias = "nearbynote"
            keyPassword = keyPassword
        }
    }

    buildTypes {
        getByName("release") {
            signingConfig = signingConfigs.getByName("release")
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.1"
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)


    // Room Database with KSP
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    ksp(libs.androidx.room.ksp.room.compiler)

    //Hilt
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)

    // Coroutines for asynchronous operations
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.coroutines.android)

    //Coroutine play service
    implementation(libs.kotlinx.coroutines.play.service)

    //Ktor
    implementation(libs.ktor.client.core)
    implementation(libs.ktor.client.okhttp)
    implementation(libs.ktor.client.serialization)
    implementation(libs.ktor.serialization.json)
    implementation(libs.ktor.client.cio)


    //Geofence API
    implementation(libs.gms.play.services.location)

    //Work manager
    implementation(libs.androidx.work.manager)

    //viewModel (MVVM)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)

    //Jetpack compose navigation
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.hilt.navigation.compose)

    //Accompanist
    implementation(libs.accompanist.jetpack.compose.permission)

    //mapbox map
    implementation(libs.mapbox.map)

    //Test
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    //Testing for viewmodel
    testImplementation(libs.test.mockK)
    //Testing for flow
    testImplementation(libs.test.turbine)
    testImplementation(libs.test.coroutine)

}