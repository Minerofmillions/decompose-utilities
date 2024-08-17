plugins {
    kotlin("jvm") version "2.0.0"
    `java-library`
    `maven-publish`
    id("co.uzzu.dotenv.gradle") version "4.0.0"
}

group = "io.github.minerofmillions"
version = "1.0.3"

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

    repositories {
        maven {
            name = "github"
            url = uri("https://maven.pkg.github.com/Minerofmillions/decompose-utilities")
            credentials {
                username = env.USERNAME.orNull() ?: System.getenv("USERNAME")
                password = env.PACKAGES_TOKEN.orNull() ?: System.getenv("PACKAGES_TOKEN")
            }
        }
    }
}
