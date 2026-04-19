import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "2.0.21"
    id("io.gitlab.arturbosch.detekt") version "1.23.8"
}

allprojects {
    group = "com.ajaxproject"
    version = "0.0.1-SNAPSHOT"

    repositories {
        mavenCentral()
        maven {
            setUrl("https://packages.confluent.io/maven/")
        }
    }
}

subprojects {

    apply(plugin = "kotlin")
    apply(plugin = "io.gitlab.arturbosch.detekt")

    dependencies {
        implementation("io.projectreactor:reactor-core:3.7.0")

        implementation("io.grpc:grpc-protobuf:1.68.1")
        implementation("io.grpc:grpc-netty:1.68.1")
        implementation("io.grpc:grpc-stub:1.68.1")
        implementation("com.salesforce.servicelibs:reactor-grpc:1.2.4")
        implementation("com.salesforce.servicelibs:reactive-grpc-common:1.2.4")
        implementation("com.salesforce.servicelibs:reactor-grpc-stub:1.2.4")
    }

    tasks.withType<KotlinCompile> {
        compilerOptions {
            freeCompilerArgs.add("-Xjsr305=strict")
            jvmTarget.set(JvmTarget.JVM_21)
        }
    }

    java {
        sourceCompatibility = JavaVersion.VERSION_21
    }
}
