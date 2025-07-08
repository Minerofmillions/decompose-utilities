plugins {
    kotlin("jvm") version "2.2.0"
    `java-library`
    `maven-publish`
}

group = "io.github.minerofmillions"
version = "1.0.7"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
    api("com.arkivanov.decompose:decompose:3.3.0")
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

    repositories {
        maven {
            name = "github"
            url = uri("https://maven.pkg.github.com/Minerofmillions/decompose-utilities")
            credentials {
                username = project.findProperty("gpr.user") as? String ?: System.getenv("USERNAME")
                password = project.findProperty("gpr.key") as? String ?: System.getenv("PACKAGES_TOKEN")
            }
        }
    }
}
