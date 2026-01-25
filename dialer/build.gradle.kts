import java.text.SimpleDateFormat
import java.util.Date

plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt)
    alias(libs.plugins.kotlin.serialization)
}

fun getBuildTime(): String {
    return SimpleDateFormat("yyyyMMdd_HHmm").format(Date())
}

android {
    namespace = "love.yuzi.dialer"
    compileSdk {
        version = release(36)
    }

    defaultConfig {
        minSdk = 28

        val vCode = 100
        val vName = "1.0.0"

        ndk {
            abiFilters.addAll(listOf("arm64-v8a"))
        }

        buildConfigField("int", "VERSION_CODE", "$vCode")
        buildConfigField("String", "VERSION_NAME", "\"$vName\"")
        buildConfigField("String", "BUILD_TIME", "\"${getBuildTime()}\"")

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
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
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
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)

    implementation(project(":contact"))
    implementation(libs.timber)

    implementation(libs.hilt.android)
    ksp(libs.hilt.android.compiler)

    implementation(libs.icons.extended)
    implementation(libs.accompanist.permissions)
    implementation(libs.compose.placeholder)
    implementation(libs.coil.compose)

    implementation(libs.androidx.navigation3.runtime)
    implementation(libs.androidx.navigation3.ui)
    implementation(libs.androidx.lifecycle.viewmodel.navigation3)
    implementation(libs.androidx.hilt.navigation.compose)

    implementation(libs.kotlinx.serialization.core)

    implementation(libs.androidx.annotation)
    implementation(libs.androidx.datastore.preferences)
    implementation(libs.reorderable)
    implementation(libs.balloon)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
}
