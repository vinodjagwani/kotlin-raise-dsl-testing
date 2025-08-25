plugins {
    kotlin("jvm") version "2.1.21"
}

group = "com.example.raise"
version = "1.0.0"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
    // Arrow (Raise DSL lives in arrow-core)
    implementation("io.arrow-kt:arrow-core:2.1.2")
// --- JUnit + Mockito + AssertJ stack ---
    testImplementation(platform("org.junit:junit-bom:5.10.3"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation("org.mockito:mockito-core:5.19.0")
    testImplementation("org.mockito.kotlin:mockito-kotlin:5.3.1")
    testImplementation("org.assertj:assertj-core:3.27.4")
// --- Kotest + MockK stack ---
    testImplementation("io.kotest:kotest-runner-junit5:6.0.0")
    testImplementation("io.kotest:kotest-assertions-core:6.0.0")
    testImplementation("io.mockk:mockk:1.14.5")
}

tasks.test {
    useJUnitPlatform()
    testLogging {
        events("passed", "skipped", "failed")
    }
}

kotlin {
    jvmToolchain(21)
}