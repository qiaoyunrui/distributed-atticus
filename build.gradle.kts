import org.jetbrains.kotlin.cli.jvm.main
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
    //    compile("org.codehaus.groovy:groovy-all:2.3.11")
    compile(kotlinModule("stdlib-jdk8", kotlin_version))
    testCompile("junit", "junit", "4.12")
    compile("com.google.code.gson:gson:2.8.4")
    compile("redis.clients:jedis:2.6.2")
    compile(fileTree("libs") {
        include("*.jar")
    })
}

//api fileTree(include: ['*.jar'], dir: 'libs')

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
        group = "Juhezi"
        description = "Print the SourcesSet"
        doLast {
            java.sourceSets.asMap.forEach({ key, value ->
                println("$key | $value")
            })
        }
    }
}