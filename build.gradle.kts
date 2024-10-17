import org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL

plugins {
    java
    application
    `project-report`
    alias(libs.plugins.versions)
    alias(libs.plugins.catalog.update)

    alias(libs.plugins.spotless)
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(23))
    }
}

val enablePreviewFlag = "--enable-preview"

tasks.withType<JavaCompile> {
    options.compilerArgs.add(enablePreviewFlag)
}

tasks.withType<JavaExec> {
    jvmArgs(enablePreviewFlag)
}

spotless {
    java {
        googleJavaFormat()
    }
    kotlinGradle {
        ktlint()
    }
}

testing {
    suites {
        withType<JvmTestSuite> {
            useJUnitJupiter()
            targets {
                all {
                    testTask.configure {
                        jvmArgs(enablePreviewFlag)
                        systemProperty("java.util.logging.manager", "org.jboss.logmanager.LogManager")
                        testLogging {
                            exceptionFormat = FULL
                            showStandardStreams = true
                            events("skipped", "failed")
                        }
                    }
                }
            }
        }
    }
}

dependencies {
    implementation(platform(libs.spring.boot))

    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-aop")
    implementation("org.springframework.boot:spring-boot-starter-actuator")

    implementation(libs.bundles.nats)

    implementation("uk.co.senapt.crm:crm-common-ext-nats-js:1.0-SNAPSHOT")

    implementation("io.micrometer:micrometer-tracing")
    implementation("io.micrometer:micrometer-tracing-bridge-otel")
    implementation("io.opentelemetry:opentelemetry-exporter-otlp")
    implementation("io.opentelemetry:opentelemetry-semconv:1.30.1-alpha")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.junit.jupiter:junit-jupiter-api")
    testImplementation("org.junit.jupiter:junit-jupiter-params")
    testImplementation(libs.assertj)
    testImplementation("org.awaitility:awaitility")
    testImplementation(libs.wiremock)
    testImplementation("org.mockito:mockito-core")
    testImplementation("org.mockito:mockito-junit-jupiter")
}

application {
    mainClass.set("uk.co.senapt.nats.test.NatsTest")
    applicationDefaultJvmArgs =
        listOfNotNull(
            "-XX:+DisableAttachMechanism",
            "-Dcom.sun.management.jmxremote",
            "-Dcom.sun.management.jmxremote.port=9000",
            "-Dcom.sun.management.jmxremote.local.only=false",
            "-Dcom.sun.management.jmxremote.authenticate=false",
            "-Dcom.sun.management.jmxremote.ssl=false",
            "-Dcom.sun.management.jmxremote.rmi.port=9000",
            "-Djava.rmi.server.hostname=127.0.0.1",
        )
}
