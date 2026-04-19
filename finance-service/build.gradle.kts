plugins {
    id("org.springframework.boot") version "3.1.3"
    id("io.spring.dependency-management") version "1.1.3"
    kotlin("plugin.spring") version "1.9.0"
    kotlin("plugin.allopen") version "1.9.0"
}

dependencies {
    implementation(project(":finance-service:domain"))
    implementation(project(":finance-service:application"))
    implementation(project(":finance-service:infrastructure"))
    implementation(project(":shared-api"))

    implementation("io.nats:jnats:2.16.14")
    implementation("org.springframework.boot:spring-boot-starter-data-mongodb-reactive")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("io.projectreactor.kotlin:reactor-kotlin-extensions:1.2.2")

    testImplementation("io.projectreactor:reactor-test:3.5.11")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
}

tasks.bootJar {
    archiveBaseName.set("finance-service")
}

tasks.test {
    useJUnitPlatform()
}
