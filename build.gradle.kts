import org.gradle.api.tasks.testing.logging.TestExceptionFormat

plugins {
    kotlin("jvm") version "1.8.21"
    id("io.ebean") version "13.11.0"
    application
}

group = "hxy.dragon"
version = "1.0-SNAPSHOT"
var MainClass = "hxy.dragon.MainKt"

repositories {
    mavenLocal()
    maven("https://maven.aliyun.com/repository/public/")
    mavenCentral()
}

var ktorm_version: String by rootProject.extra // 这个属性值去 gradle.properties里面修改下。
val jackson_version: String by extra("2.15.1")
val ebean_version: String by extra("13.18.0")

dependencies {
    implementation("io.javalin:javalin:5.5.0")
    implementation("com.fasterxml.jackson.core:jackson-databind:$jackson_version")
//    https://github.com/FasterXML/jackson-module-kotlin
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:$jackson_version")

//    第一种数据库ORM框架
    implementation("org.ktorm:ktorm-core:$ktorm_version")
    implementation("org.ktorm:ktorm-jackson:$ktorm_version")
    implementation("org.ktorm:ktorm-support-mysql:$ktorm_version")

//    第二种数据库ORM框架

    implementation("io.ebean:ebean:$ebean_version")
    // query bean generation
    annotationProcessor("io.ebean:querybean-generator:$ebean_version")
    testImplementation("io.ebean:ebean-test:$ebean_version")

    implementation("mysql:mysql-connector-java:8.0.33")

    implementation("org.slf4j:slf4j-api:1.7.32")
    implementation("ch.qos.logback:logback-classic:1.4.7")
//    https://github.com/oshai/kotlin-logging
    implementation("io.github.oshai:kotlin-logging-jvm:4.0.0-beta-29")
//    implementation("io.github.microutils:kotlin-logging-jvm:3.0.5")

    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
    testLogging.showStandardStreams = true
    testLogging.exceptionFormat = TestExceptionFormat.FULL

}

kotlin {
    jvmToolchain(17)
}

application {
    mainClass.set(MainClass)
}

sourceSets {
    getByName("main") {
        java.srcDirs("src/main/kotlin", "src/main/java")
    }
}

// 打包
tasks.jar.configure {
    duplicatesStrategy = DuplicatesStrategy.INCLUDE
    manifest.attributes["Main-Class"] = MainClass
    from(configurations.runtimeClasspath.get().filter { it.name.endsWith("jar") }.map { zipTree(it) })
}

ebean {
    debugLevel = 1
}