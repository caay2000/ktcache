plugins {
    `java-library`
    kotlin("jvm")
    id("org.jlleitschuh.gradle.ktlint")
}

repositories {
    mavenCentral()
}

tasks.test {
    useJUnitPlatform()
}

dependencies {
    testImplementation(kotlin("test"))
    testImplementation("org.jetbrains.kotlin:kotlin-stdlib:1.7.10")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.4")
    testImplementation("org.junit.jupiter:junit-jupiter:5.9.0")
    testImplementation("org.assertj:assertj-core:3.23.1")
    testImplementation("org.mockito.kotlin:mockito-kotlin:4.0.0")
    testImplementation("io.github.microutils:kotlin-logging:3.0.0")
    testImplementation("ch.qos.logback:logback-classic:1.4.1")
}
