plugins {
    kotlin("jvm") version "2.0.0"
    `java-library`
    `maven-publish`
}

group = "io.github.minerofmillions"
version = "1.0"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
    api("com.arkivanov.decompose:decompose:3.1.0")
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain {
        languageVersion = JavaLanguageVersion.of(17)
    }
}

java {
    withSourcesJar()
    withJavadocJar()
}

tasks.build {
    dependsOn(tasks.test)
}

publishing {
    publications {
        create<MavenPublication>("decomposeUtilities") {
            from(components["java"])
        }
    }
}
