import com.github.benmanes.gradle.versions.updates.DependencyUpdatesTask

plugins {
    kotlin("jvm")
    id("com.diffplug.spotless")
    id("com.github.ben-manes.versions")
}

repositories {
    mavenCentral()
}

tasks.test {
    useJUnitPlatform()
}

dependencies {
    implementation(project(":core"))

    implementation("io.github.microutils:kotlin-logging:3.0.5")
    implementation("ch.qos.logback:logback-classic:1.4.11")

    testImplementation(kotlin("test"))
    testImplementation("org.junit.jupiter:junit-jupiter:5.10.0")
    testImplementation("org.assertj:assertj-core:3.24.2")
    testImplementation("org.mockito.kotlin:mockito-kotlin:5.1.0")
}

fun String.isNonStable(): Boolean = listOf("SNAPSHOT", "RC", "BETA").any { uppercase().contains(it) }
tasks.withType<DependencyUpdatesTask> {
    rejectVersionIf {
        candidate.version.isNonStable()
    }
}

spotless {
    kotlin { ktlint() }
    kotlinGradle { ktlint() }
}
