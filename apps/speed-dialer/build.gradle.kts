import java.io.FileInputStream
import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt)
}

val keystoreProperties = Properties()
val keystorePropertiesFile = rootProject.file("local.properties")
if (keystorePropertiesFile.exists()) {
    keystoreProperties.load(FileInputStream(keystorePropertiesFile))
}

android {
    namespace = "love.yuzi.speed_dialer"
    compileSdk {
        version = release(36)
    }

    defaultConfig {
        applicationId = "love.yuzi.speed_dialer"
        minSdk = 28
        targetSdk = 36
        versionCode = 100
        versionName = "1.0.0"

        ndk {
            abiFilters.addAll(listOf("arm64-v8a"))
        }

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    signingConfigs {
        create("release") {
            val sFile = keystoreProperties.getProperty("signing.storeFile")
            if (sFile != null) {
                storeFile = file(sFile)
                storePassword = keystoreProperties.getProperty("signing.storePassword")
                keyAlias = keystoreProperties.getProperty("signing.keyAlias")
                keyPassword = keystoreProperties.getProperty("signing.keyPassword")

                enableV1Signing = true
                enableV2Signing = true
                enableV3Signing = true
                enableV4Signing = false
            } else {
                println("signing.storeFile is null")
            }
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            signingConfig = signingConfigs.getByName("release")
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
}

dependencies {
    implementation(libs.androidx.core.ktx)

    implementation(libs.androidx.core.splashscreen)
    implementation(libs.hilt.android)
    ksp(libs.hilt.android.compiler)
    implementation(libs.timber)
    implementation(project(":base"))
    implementation(project(":dialer"))
    implementation(project(":contact"))

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}