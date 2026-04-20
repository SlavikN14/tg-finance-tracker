val telegramBotVersion = "9.5.0"

plugins {
    id("org.springframework.boot") version "3.5.13"
    id("io.spring.dependency-management") version "1.1.7"
    kotlin("plugin.spring") version "2.0.21"
    kotlin("plugin.allopen") version "2.0.21"
}

dependencies {
    implementation(project(":shared-api"))

    implementation("org.telegram:telegrambots-springboot-longpolling-starter:$telegramBotVersion")
    implementation("org.telegram:telegrambots-client:$telegramBotVersion")

    implementation("com.google.code.gson:gson:2.10.1")
    implementation("io.nats:jnats:2.25.2")

    implementation("org.springframework.boot:spring-boot-starter-webflux")
    implementation("org.springframework.boot:spring-boot-starter-data-mongodb-reactive")
    implementation("org.springframework.boot:spring-boot-starter-data-redis-reactive")
    implementation("org.springframework.kafka:spring-kafka")

    implementation("net.devh:grpc-spring-boot-starter:2.15.0.RELEASE")
    implementation("io.confluent:kafka-protobuf-serializer:7.5.1")

    implementation("io.projectreactor.kafka:reactor-kafka:1.3.21")
    implementation("io.projectreactor.kotlin:reactor-kotlin-extensions")

    testImplementation("io.projectreactor:reactor-test")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
}
