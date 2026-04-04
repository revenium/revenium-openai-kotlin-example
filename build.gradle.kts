plugins {
    kotlin("jvm") version "1.9.24"
    application
}

group = "com.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenLocal()
    mavenCentral()
}

dependencies {
    implementation("com.openai:openai-java:4.30.0")
    implementation("io.revenium.metering:revenium-middleware-openai-java:0.1.4-SNAPSHOT")
    implementation("org.slf4j:slf4j-simple:2.0.16")
}

kotlin {
    jvmToolchain(21)
}

application {
    mainClass.set(project.findProperty("mainClass") as String? ?: "MainKt")
}
