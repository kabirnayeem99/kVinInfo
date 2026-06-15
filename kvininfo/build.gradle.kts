import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidKotlinMultiplatformLibrary)
    alias(libs.plugins.kotlinxSerialization)
    alias(libs.plugins.maven.publish)
    alias(libs.plugins.kover)
}

mavenPublishing {
    coordinates(
        groupId = "io.github.kabirnayeem99",
        artifactId = "kvininfo",
        version = "2.0.0",
    )

    pom {
        name.set("kVinInfo")
        description.set("A Kotlin Multiplatform library for VIN (Vehicle Identification Number) decoding and validation.")
        inceptionYear.set("2026")
        url.set("https://github.com/kabirnayeem99/kVinInfo")

        licenses {
            license {
                name.set("MIT")
                url.set("https://opensource.org/licenses/MIT")
            }
        }

        developers {
            developer {
                id.set("kabirnayeem99")
                name.set("Naimul Kabir")
                email.set("kabirnayeem.99@gmail.com")
            }
        }

        scm {
            url.set("https://github.com/kabirnayeem99/kVinInfo")
            connection.set("scm:git:git://github.com/kabirnayeem99/kVinInfo.git")
            developerConnection.set("scm:git:ssh://github.com/kabirnayeem99/kVinInfo.git")
        }
    }

    publishToMavenCentral()
    signAllPublications()
}

kover {
    reports {
        verify {
            rule("Line coverage must be at least 90%") {
                minBound(90)
            }
        }
    }
}

kotlin {
    jvm()
    android {
        namespace = "io.github.kabirnayeem99.kvininfo"
        compileSdk = libs.versions.android.compileSdk.get().toInt()
        minSdk = libs.versions.android.minSdk.get().toInt()
        @OptIn(ExperimentalKotlinGradlePluginApi::class)
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_1_8)
        }
    }
    iosX64()
    iosArm64()
    iosSimulatorArm64()
    linuxX64()

    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach {
        it.binaries.framework {
            baseName = "kvininfo"
            isStatic = true
        }
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(libs.kotlinx.serialization.json)
                implementation(libs.kotlinx.coroutines.core)
                implementation(libs.ktor.client.core)
                implementation(libs.ktor.client.content.negotiation)
                implementation(libs.ktor.serialization.kotlinx.json)
                implementation(libs.kotlin.test)
                implementation(libs.ktor.client.mock)
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(libs.kotlin.test)
                implementation(libs.ktor.client.mock)
                implementation(libs.kotlinx.coroutines.test)
            }
        }

        val jvmTest by getting {
            dependencies {
                implementation(libs.ktor.client.cio)
            }
        }

        val androidMain by getting {
            dependencies {
                implementation(libs.ktor.client.android)
            }
        }

        val iosArm64Main by getting {
            dependencies {
                implementation(libs.ktor.client.darwin)
            }
        }

        val iosX64Main by getting {
            dependencies {
                implementation(libs.ktor.client.darwin)
            }
        }

        val iosSimulatorArm64Main by getting {
            dependencies {
                implementation(libs.ktor.client.darwin)
            }
        }

    }
}
