plugins {
    kotlin("jvm") version "1.5.31"
}

group = "dev.twelveoclock"
version = "1.0.0"

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    implementation("org.ow2.asm:asm:9.2")
}