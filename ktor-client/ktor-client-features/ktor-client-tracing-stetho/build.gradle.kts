/*
 * Copyright 2014-2020 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

val experimentalAnnotations = listOf(
    "kotlin.RequiresOptIn",
    "kotlin.ExperimentalUnsignedTypes",
    "io.ktor.util.KtorExperimentalAPI",
    "io.ktor.util.InternalAPI",
    "kotlinx.coroutines.ExperimentalCoroutinesApi",
    "io.ktor.utils.io.core.ExperimentalIoApi",
    "io.ktor.utils.io.core.internal.DangerousInternalIoApi",
    "kotlin.contracts.ExperimentalContracts",
    "kotlin.Experimental",
    "kotlinx.io.core.ExperimentalIoApi",
    "kotlinx.io.core.internal.DangerousInternalIoApi"
)

repositories {
    google()
}

plugins {
    id("com.android.library")
    id("kotlin-android-extensions")
}

kotlin {
    android {
        publishAllLibraryVariants()
    }

    sourceSets {
        val androidMain by getting {
            dependencies {
                implementation(project(":ktor-client:ktor-client-core"))
                implementation("com.facebook.stetho:stetho:1.5.1")
            }
        }

        val androidTest by getting {
            dependencies {
                api("org.jetbrains.kotlin:kotlin-test:1.3.61")
                api("junit:junit:4.12")
            }
        }
    }

    sourceSets.all {
        experimentalAnnotations.forEach { annotation ->
            languageSettings.useExperimentalAnnotation(annotation)
        }
    }
}

android {
    compileSdkVersion(29)
    packagingOptions {
        exclude("META-INF/kotlinx-coroutines-core.kotlin_module")
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    defaultConfig {
        minSdkVersion(9)
        targetSdkVersion(29)
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "android.support.test.runner.AndroidJUnitRunner"
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android.txt"), "proguard-rules.pro")
        }
    }
}
