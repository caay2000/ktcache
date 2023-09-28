import com.github.benmanes.gradle.versions.updates.DependencyUpdatesTask

plugins {
    `java-library`
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
    testImplementation(kotlin("test"))
    testImplementation("org.jetbrains.kotlin:kotlin-stdlib:1.9.10")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
    testImplementation("org.junit.jupiter:junit-jupiter:5.10.0")
    testImplementation("org.assertj:assertj-core:3.24.2")
    testImplementation("org.mockito.kotlin:mockito-kotlin:5.1.0")
    testImplementation("io.github.microutils:kotlin-logging:3.0.5")
    testImplementation("ch.qos.logback:logback-classic:1.4.11")
}

tasks.jar {
    manifest {
        attributes(
            mapOf(
                "Implementation-Title" to "${rootProject.group}.${rootProject.name}-core",
                "Implementation-Version" to version,
            ),
        )
    }
    archiveBaseName.set("${rootProject.group}.${rootProject.name}-core.${rootProject.version}")
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
