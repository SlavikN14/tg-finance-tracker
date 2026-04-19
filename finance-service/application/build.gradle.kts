plugins {
    id("io.spring.dependency-management") version "1.1.3"
    kotlin("plugin.spring") version "1.9.0"
}

dependencyManagement {
    imports {
        mavenBom("org.springframework.boot:spring-boot-dependencies:3.1.3")
    }
}

dependencies {
    implementation(project(":finance-service:domain"))
    implementation("org.springframework:spring-context")
    implementation("io.projectreactor.kotlin:reactor-kotlin-extensions:1.2.2")
}
