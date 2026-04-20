plugins {
    id("org.springframework.boot") version "3.5.13"
    id("io.spring.dependency-management") version "1.1.7"
    kotlin("plugin.spring") version "2.0.21"
    kotlin("plugin.allopen") version "2.0.21"
}

dependencies {
    implementation(project(":finance-service:domain"))
    implementation(project(":finance-service:application"))
    implementation(project(":finance-service:infrastructure"))
    implementation(project(":shared-api"))

    implementation("io.nats:jnats:2.25.2")
    implementation("org.springframework.boot:spring-boot-starter-data-mongodb-reactive")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("io.projectreactor.kotlin:reactor-kotlin-extensions")

    testImplementation("io.projectreactor:reactor-test")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
}

tasks.bootJar {
    archiveBaseName.set("finance-service")
}

tasks.test {
    useJUnitPlatform()
}
