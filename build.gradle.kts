plugins {
    alias(libs.plugins.maven.publish) apply false
    alias(libs.plugins.androidKotlinMultiplatformLibrary).apply(false)
    alias(libs.plugins.kotlinMultiplatform).apply(false)
    alias(libs.plugins.androidApplication) apply false
    alias(libs.plugins.kotlinCompose) apply false
    alias(libs.plugins.ktlint)
    alias(libs.plugins.kover) apply false
}
