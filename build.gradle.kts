plugins {
    id("java")
    id("org.jetbrains.kotlin.jvm") version "1.9.24"
    id("org.jetbrains.intellij") version "1.17.3"
}

group = "com.cleanarch.com"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven("https://cache-redirector.jetbrains.com/intellij-dependencies")
}

intellij {
    version.set("2023.1.6") // or 2023.3.6 if supported
    type.set("IC")
}

tasks {
    withType<JavaCompile> {
        sourceCompatibility = "17"
        targetCompatibility = "17"
    }
    withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
        kotlinOptions.jvmTarget = "17"
    }

    patchPluginXml {
        sinceBuild.set("231")
        untilBuild.set("242.*")
    }
}
