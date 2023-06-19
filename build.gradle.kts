plugins {
    id("java")
}

group = "com.spanner"
version = "0.0.0"

repositories {
    mavenCentral()
    maven(url = "https://jitpack.io")
}

dependencies {
    compileOnly("com.github.Minestom:Minestom:954e8b3915")
    implementation("net.kyori:adventure-text-minimessage:4.14.0")
}

tasks {
    processResources {
        expand("VERSION" to version)
    }
}