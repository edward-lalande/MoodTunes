plugins {
    kotlin("jvm") version "1.9.23"
    kotlin("plugin.serialization") version "1.9.23"
    application
}

repositories {
    mavenCentral()
}

val ktorVersion = "2.3.5"

dependencies {
    implementation("io.ktor:ktor-server-core:$ktorVersion")
    implementation("io.ktor:ktor-server-netty:$ktorVersion")
    implementation("io.ktor:ktor-server-content-negotiation:$ktorVersion")
    implementation("io.ktor:ktor-serialization-kotlinx-json:$ktorVersion")
    implementation("ch.qos.logback:logback-classic:1.4.14")

    implementation("io.github.smiley4:ktor-swagger-ui:2.6.0")
}

application {
    mainClass.set("com.moodtunes.ApplicationKt")
}

kotlin {
    jvmToolchain(17)
}
