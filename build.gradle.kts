import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

buildscript {
    var kotlin_version: String by extra
    kotlin_version = "1.2.41"

    repositories {
        mavenCentral()
    }
    dependencies {
        classpath(kotlinModule("gradle-plugin", kotlin_version))
    }
}

plugins {
    groovy
    java
    application
}

group = "me.juhezi"
version = "1.0-SNAPSHOT"

apply {
    plugin("kotlin")
}

val kotlin_version: String by extra

repositories {
    mavenCentral()
    jcenter()
}

dependencies {
    compile("org.codehaus.groovy:groovy-all:2.3.11")
    compile(kotlinModule("stdlib-jdk8", kotlin_version))
    testCompile("junit", "junit", "4.12")
}

configure<JavaPluginConvention> {
    sourceCompatibility = JavaVersion.VERSION_1_8
}
tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}
application {
    mainClassName = "distributed.Main"
}
tasks {
    "print" {
        java.sourceSets.asMap.forEach({ key, value ->
            println("$key | $value")
        })
    }
}
