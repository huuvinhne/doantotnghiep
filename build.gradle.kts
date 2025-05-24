
// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.compose) apply false
    kotlin("jvm") version "1.9.0" // Đảm bảo bạn có plugin Kotlin
    id("org.jetbrains.kotlin.kapt") version "1.9.0" // Thêm dòng này
}