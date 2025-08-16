plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)

    alias(libs.plugins.kotlin.ksp)

    alias(libs.plugins.hilt.ksp)

    alias(libs.plugins.kotlin.serialization)

}


val MAPBOX_API_KEY: String by project
val KEYSTORE_PASSWORD: String by project
val KEY_PASSWORD: String by project

android {
    namespace = "com.pjh.nearbynote"
    compileSdk = 35
    flavorDimensions += "distribution_build"

    defaultConfig {
        applicationId = "com.pjh.nearbynote"
        minSdk = 30
        targetSdk = 35
        versionCode = 2
        versionName = "1.0.1"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        resValue("string", "mapbox_access_token", MAPBOX_API_KEY)
        buildConfigField("String", "MAPBOX_API_KEY", "\"${MAPBOX_API_KEY}\"")
    }

    productFlavors {
        create("review") {
            dimension = "distribution_build"
            buildConfigField("boolean", "REVIEW_MODE", "true")
        }
        create("public") {
            dimension = "distribution_build"
            buildConfigField("boolean", "REVIEW_MODE", "false")
        }
    }

    signingConfigs {
        create("release") {
            storeFile = file("./keystore/nearbynote-release.jks")
            storePassword = KEYSTORE_PASSWORD
            keyAlias = "nearbynote"
            keyPassword = KEY_PASSWORD
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
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.7.3"
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