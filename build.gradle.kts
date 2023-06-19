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
    implementation("com.github.Minestom:Minestom:954e8b3915")
}