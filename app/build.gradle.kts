import org.gradle.api.Project
import java.util.Properties

fun Project.loadLocalProperties(): Properties {
    val properties = Properties()
    val localPropertiesFile = file("../local.properties")
    if (localPropertiesFile.exists()) {
        localPropertiesFile.inputStream().use { properties.load(it) }
    }
    return properties
}

plugins {
    alias(libs.plugins.android.application)
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.soundsenseapp"
    compileSdk = 34
    val localProperties = loadLocalProperties()
    val apiKey = localProperties.getProperty("CHATGPT_API_KEY") ?: ""

    defaultConfig {
        buildConfigField("String", "CHATGPT_API_KEY", "\"$apiKey\"")
        applicationId = "com.example.soundsenseapp"
        minSdk = 33
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    buildFeatures {
        viewBinding = true
        buildConfig = true
    }
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.constraintlayout)
    implementation(libs.navigation.fragment)
    implementation(libs.navigation.ui)
    implementation(libs.annotation)
    implementation(libs.lifecycle.livedata.ktx)
    implementation(libs.lifecycle.viewmodel.ktx)
    implementation(libs.legacy.support.v4)
    implementation(libs.activity)
    implementation(libs.firebase.auth)
    implementation(libs.play.services.location)
    implementation("com.android.volley:volley:1.2.1")
    implementation("com.squareup.picasso:picasso:2.71828")
    implementation("com.spotify.android:auth:1.2.5")
    implementation("androidx.security:security-crypto:1.1.0-alpha03")
    implementation("org.xerial:sqlite-jdbc:3.36.0.3")
    implementation("androidx.lifecycle:lifecycle-viewmodel:2.5.1")
    implementation("androidx.lifecycle:lifecycle-livedata:2.5.1")

    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)

    // Import the Firebase BoM
    implementation(platform("com.google.firebase:firebase-bom:33.5.1"))
    // TODO: Add the dependencies for Firebase products you want to use
    // When using the BoM, don't specify versions in Firebase dependencies
    // https://firebase.google.com/docs/android/setup#available-libraries
    implementation(platform("com.google.firebase:firebase-auth-ktx:23.0.0"))
    implementation("com.google.firebase:firebase-analytics")
}