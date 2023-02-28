plugins {
    application
    kotlin("jvm") version "1.8.10"
    id("com.github.johnrengelman.shadow") version "8.1.0"
}

group = "dev.twelveoclock"
version = "1.0.0"

repositories {
    mavenCentral()
}

dependencies {

    // Kotlin
    implementation(kotlin("stdlib-jdk8"))
    implementation("org.jetbrains.kotlinx:kotlinx-cli:0.3.5")

    // Transformers
    implementation("org.ow2.asm:asm:9.4")
    implementation("com.guardsquare:proguard-core:9.0.7")
    implementation("net.bytebuddy:byte-buddy:1.12.23")
    implementation("org.jetbrains.kotlinx:kotlinx-metadata-jvm:0.6.0")


    // Testing
    testImplementation(kotlin("test-junit"))
    testImplementation("com.github.tschuchortdev:kotlin-compile-testing:1.4.9")
}

application {
    mainClass.set("dev.twelveoclock.apicreator.Main")
}