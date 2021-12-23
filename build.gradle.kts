plugins {
    kotlin("jvm") version "1.6.0"
}

group = "com.monta"
version = "0.1.0-RC"

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.0")
}