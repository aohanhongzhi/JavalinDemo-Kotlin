/*
 * This file was generated by the Gradle 'init' task.
 *
 * This generated file contains a sample Kotlin application project to get you started.
 * For more details on building Java & JVM projects, please refer to https://docs.gradle.org/8.4/userguide/building_java_projects.html in the Gradle documentation.
 */

plugins {
    // Apply the org.jetbrains.kotlin.jvm Plugin to add support for Kotlin.
    id("org.jetbrains.kotlin.jvm") version "1.9.20-RC2"
    id("io.ebean") version "13.23.2"

    // Apply the application plugin to add support for building a CLI application in Java.
    application
}

group = "hxy.dragon"
version = "1.0-SNAPSHOT"
var mainKotlinClass = "hxy.dragon.MainKt"

repositories {
    // Use Maven Central for resolving dependencies.
    mavenLocal()
//    maven("https://maven.aliyun.com/repository/public/")
    mavenCentral()
}

var ktorm_version: String by rootProject.extra // 这个属性值去 gradle.properties里面修改下。
val jackson_version: String by extra("2.15.1")
val ebean_version: String by extra("13.23.2") // https://github.com/ebean-orm/ebean


dependencies {


    implementation("io.javalin:javalin:5.6.2")
    implementation("com.fasterxml.jackson.core:jackson-databind:$jackson_version")
//    https://github.com/FasterXML/jackson-module-kotlin
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:$jackson_version")

//    第一种数据库ORM框架 建议放弃
    implementation("org.ktorm:ktorm-core:$ktorm_version")
    implementation("org.ktorm:ktorm-jackson:$ktorm_version")
    implementation("org.ktorm:ktorm-support-mysql:$ktorm_version")

//    第二种数据库ORM框架 推荐
    implementation("io.ebean:ebean:$ebean_version")
    // query bean generation
    annotationProcessor("io.ebean:querybean-generator:$ebean_version")
    testImplementation("io.ebean:ebean-test:$ebean_version")

    implementation("mysql:mysql-connector-java:8.0.33")

    implementation("org.slf4j:slf4j-api:2.0.7")
    implementation("ch.qos.logback:logback-classic:1.4.7")
//    https://github.com/oshai/kotlin-logging
//    implementation("io.github.oshai:kotlin-logging-jvm:4.0.0")
    implementation("io.github.oshai:kotlin-logging-jvm:5.1.0")

//    implementation("io.github.microutils:kotlin-logging-jvm:3.0.5")

    testImplementation(kotlin("test"))

    // Use the Kotlin JUnit 5 integration.
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")

    // Use the JUnit 5 integration.
    testImplementation("org.junit.jupiter:junit-jupiter-engine:5.9.3")

    testRuntimeOnly("org.junit.platform:junit-platform-launcher")

    // This dependency is used by the application.
    implementation("com.google.guava:guava:32.1.1-jre")
}

// Apply a specific Java toolchain to ease working on different environments.
java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

application {
    // Define the main class for the application.
    // 这里特别注意，不要把变量命名成mainClass，否则会导致死循环，最后栈溢出。
    mainClass.set(mainKotlinClass)
}

tasks.named<Test>("test") {
    // Use JUnit Platform for unit tests.
    useJUnitPlatform()
}

sourceSets {
    getByName("main") {
        java.srcDirs("src/main/kotlin", "src/main/java")
    }
}

// 打包
tasks.jar.configure {
    duplicatesStrategy = DuplicatesStrategy.INCLUDE
    manifest.attributes["Main-Class"] = mainKotlinClass
    from(configurations.runtimeClasspath.get().filter { it.name.endsWith("jar") }.map { zipTree(it) })
}

ebean {
    debugLevel = 1
}