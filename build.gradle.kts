plugins {
    kotlin("jvm") version "1.8.21"
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

dependencies {
    implementation("io.javalin:javalin:5.5.0")
    implementation("com.fasterxml.jackson.core:jackson-databind:2.15.1")
//    https://github.com/FasterXML/jackson-module-kotlin
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.15.1")

    implementation("org.ktorm:ktorm-core:3.6.0")
    implementation("org.ktorm:ktorm-jackson:3.6.0")
    implementation("org.ktorm:ktorm-support-mysql:3.6.0")

    implementation("mysql:mysql-connector-java:8.0.33")

    implementation("org.slf4j:slf4j-api:1.7.32")
    implementation("ch.qos.logback:logback-classic:1.4.7")
//    https://github.com/oshai/kotlin-logging
    implementation("io.github.microutils:kotlin-logging-jvm:3.0.5")

    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
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