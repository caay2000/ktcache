import com.github.benmanes.gradle.versions.updates.DependencyUpdatesTask

plugins {
    kotlin("jvm") version "1.9.10"
    id("com.diffplug.spotless") version "6.21.0"
    id("com.github.ben-manes.versions") version "0.48.0"
}

tasks.wrapper {
    gradleVersion = "8.3"
    distributionType = Wrapper.DistributionType.ALL
}

group = "com.github.caay2000.ktcache"
version = "0.0.1"

repositories {
    mavenCentral()
}

tasks.check {
    dependsOn(tasks.spotlessCheck)
}

spotless {
    kotlin { ktlint() }
    kotlinGradle { ktlint() }
}

fun String.isNonStable(): Boolean = listOf("SNAPSHOT", "RC", "BETA").any { uppercase().contains(it) }
tasks.withType<DependencyUpdatesTask> {
    rejectVersionIf {
        candidate.version.isNonStable()
    }
}
