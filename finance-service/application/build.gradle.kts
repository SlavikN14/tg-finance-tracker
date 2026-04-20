plugins {
    id("io.spring.dependency-management") version "1.1.7"
    kotlin("plugin.spring") version "2.0.21"
}

dependencyManagement {
    imports {
        mavenBom("org.springframework.boot:spring-boot-dependencies:3.5.13")
    }
}

dependencies {
    implementation(project(":finance-service:domain"))
    implementation("org.springframework:spring-context")
    implementation("io.projectreactor.kotlin:reactor-kotlin-extensions")
}
