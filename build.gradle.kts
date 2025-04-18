plugins {
    application
    kotlin("jvm") version "2.0.21"
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

group = "dev.twelveoclock"
version = "1.0.0"

repositories {
    mavenCentral()
}

dependencies {

    // Kotlin
    implementation(kotlin("stdlib-jdk8"))
    implementation("org.jetbrains.kotlinx:kotlinx-cli:0.3.6")

    // Transformers
    implementation("org.ow2.asm:asm:9.8")
    implementation("com.guardsquare:proguard-core:9.1.7")
    implementation("net.bytebuddy:byte-buddy:1.17.5")
    implementation("org.jetbrains.kotlinx:kotlinx-metadata-jvm:0.7.0")


    // Testing
    testImplementation(kotlin("test-junit"))
    testImplementation("com.github.tschuchortdev:kotlin-compile-testing:1.5.0")
}

application {
    mainClass.set("dev.twelveoclock.apicreator.Main")
}