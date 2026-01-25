plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.compose) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.ksp) apply false
    alias(libs.plugins.hilt) apply false
    alias(libs.plugins.kotlin.serialization) apply false
}

tasks.register<Copy>("buildAllApps") {
    group = "custom-build"
    description = "Build all apks for release"

    val aiDialerModule = ":apps:ai-dialer"
    val speedDialerModule = ":apps:speed-dialer"
    val versionName = "1.0.0"

    dependsOn("$aiDialerModule:assembleRelease", ":$speedDialerModule:assembleRelease")

    val aiDialerBuildDir = project(aiDialerModule).layout.buildDirectory
    val speedDialerBuildDir = project(speedDialerModule).layout.buildDirectory

    into(layout.projectDirectory.dir("release"))

    from(aiDialerBuildDir.dir("outputs/apk/release")) {
        include("*.apk")
        rename { "AiDialer-$versionName-release.apk" }
    }
    from(speedDialerBuildDir.dir("outputs/apk/release")) {
        include("*.apk")
        rename { "SpeedDialer-$versionName-release.apk" }
    }

    doLast {
        println("🚀 Products have been summarized in: ${destinationDir.absolutePath}")
    }
}