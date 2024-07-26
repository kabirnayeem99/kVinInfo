plugins {
    alias(libs.plugins.jvm) // <1>
    `java-library` // <2>
}

repositories {
    mavenCentral() // <3>
}

dependencies {
    testImplementation(kotlin("test"))
}

tasks.named<Test>("test") {
    useJUnitPlatform() // <8>
}
