plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    id("io.realm.kotlin") version "1.11.0"
    id("com.google.dagger.hilt.android") version "2.50" apply false
}

buildscript {
    repositories {
        google()
        mavenCentral()
    }
    dependencies {
        classpath("com.android.tools.build:gradle:8.0.2")
        classpath("io.realm:realm-gradle-plugin:10.15.1")
        classpath(libs.hilt.android.gradle.plugin)
    }
}