plugins {
    application
    kotlin("jvm") version "1.8.10"
    id("com.github.johnrengelman.shadow") version "7.1.2"
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
    implementation("org.ow2.asm:asm:9.5")
    implementation("com.guardsquare:proguard-core:9.0.10")
    implementation("net.bytebuddy:byte-buddy:1.14.4")
    implementation("org.jetbrains.kotlinx:kotlinx-metadata-jvm:0.6.0")


    // Testing
    testImplementation(kotlin("test-junit"))
    testImplementation("com.github.tschuchortdev:kotlin-compile-testing:1.4.9")
}

application {
    mainClass.set("dev.twelveoclock.apicreator.Main")
}