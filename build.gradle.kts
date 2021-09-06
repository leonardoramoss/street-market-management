val ktorVersion: String by project
val kotlinVersion: String by project
val logbackVersion: String by project
val koinVersion: String by project
val exposedVersion: String by project

plugins {
    jacoco
    application
    kotlin("jvm") version "1.5.30"
    id("com.github.johnrengelman.shadow") version "7.0.0"
}

group = "com.streetmarket"
version = "0.0.1"

java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}

jacoco {
    toolVersion = "0.8.7"
}

application {
    mainClass.set("io.ktor.server.cio.EngineMain")
}

repositories {
    mavenLocal()
    mavenCentral()
}

dependencies {
    implementation("io.ktor:ktor-server-core:$ktorVersion")
    implementation("io.ktor:ktor-server-host-common:$ktorVersion")
    implementation("io.ktor:ktor-server-cio:$ktorVersion")
    implementation("io.ktor:ktor-metrics:$ktorVersion")
    implementation("io.ktor:ktor-jackson:$ktorVersion")

    implementation("ch.qos.logback:logback-classic:$logbackVersion")
    implementation("ch.qos.logback.contrib:logback-jackson:0.1.5")
    implementation("ch.qos.logback.contrib:logback-json-classic:0.1.5")
    implementation("net.logstash.logback:logstash-logback-encoder:6.6")

    implementation("com.github.doyaaaaaken:kotlin-csv-jvm:0.15.2")

    implementation("io.insert-koin:koin-core:$koinVersion")
    implementation("io.insert-koin:koin-ktor:$koinVersion")

    implementation("org.jetbrains.exposed:exposed-core:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-jdbc:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-java-time:$exposedVersion")

    implementation("com.zaxxer:HikariCP:5.0.0")
    implementation("org.postgresql:postgresql:42.2.23.jre7")

    testImplementation("com.h2database:h2:1.4.200")
    testImplementation("org.dbunit:dbunit:2.7.2")

    testImplementation("net.minidev:json-smart:2.4.7")
    testImplementation("org.skyscreamer:jsonassert:1.5.0")

    testImplementation("io.ktor:ktor-server-tests:$ktorVersion")
    testImplementation(kotlin("test:$kotlinVersion"))
    testImplementation(kotlin("test-junit5:$kotlinVersion"))
    testImplementation("org.junit.jupiter:junit-jupiter-engine:5.7.2")
    testImplementation("org.junit.jupiter:junit-jupiter-params:5.7.2")
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions {
        jvmTarget = "11"
    }
}

tasks.test {
    useJUnitPlatform()
    finalizedBy(tasks.jacocoTestReport)
    testLogging {
        events("passed", "skipped", "failed")
    }
}

tasks.jacocoTestReport {
    dependsOn(tasks.test)

    reports {
        xml.required.set(false)
        csv.required.set(false)
        html.outputLocation.set(layout.buildDirectory.dir("jacoco"))
    }

    finalizedBy(tasks.jacocoTestCoverageVerification)
}

tasks.jacocoTestCoverageVerification {
    violationRules {
        rule {
            limit {
                minimum = "0.80".toBigDecimal()
            }
        }
        rule {
            enabled = true

            element = "CLASS"

            excludes = listOf(
                "*Application*",
                "*Configuration*",
                "*Exceptions*"
            )

            limit {
                counter = "LINE"
                value = "COVEREDRATIO"
                minimum = ".9".toBigDecimal()
            }

            limit {
                counter = "BRANCH"
                value = "COVEREDRATIO"
                minimum = "0.80".toBigDecimal()
            }

            limit {
                counter = "LINE"
                value = "TOTALCOUNT"
                maximum = "200".toBigDecimal()
            }
        }
    }
}
