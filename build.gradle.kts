import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.gradle.api.tasks.Exec

plugins {
    application
    checkstyle
    idea
    jacoco
    java
    id("com.github.johnrengelman.shadow") version "7.1.2"
    id("com.liferay.node") version "7.2.18"
    id("com.github.psxpaul.execfork") version "0.2.0"
    id("com.palantir.git-version") version "0.13.0"
}

node {
    setDownload(false)
}

application {
    mainClassName = "reposense.RepoSense"
}

repositories {
    mavenCentral()
}

dependencies {
    val jUnitVersion: String = "5.8.2"
    implementation(group = "com.google.code.gson", name = "gson", version = "2.9.0")
    implementation(group = "net.freeutils", name = "jlhttp", version = "2.6")
    implementation(group = "net.sourceforge.argparse4j", name = "argparse4j", version = "0.9.0")
    implementation(group = "org.apache.ant", name = "ant", version = "1.10.12")
    implementation(group = "org.apache.commons", name = "commons-csv", version = "1.9.0")
    implementation(group = "org.fusesource.jansi", name = "jansi", version = "2.4.0")
    testImplementation(group = "org.junit.jupiter", name = "junit-jupiter-api", version = jUnitVersion)
    testImplementation(group = "org.junit.jupiter", name = "junit-jupiter-engine", version = jUnitVersion)
}

val installFrontend = tasks.register<Exec>("installFrontend") {
    setCommandLine("npm.cmd")

    setWorkingDir("frontend/")
    setArgs(listOf("ci"))
}

tasks.register<Exec>("buildFrontend") {
    setDependsOn(listOf(installFrontend))
    setCommandLine("npm.cmd")

    setWorkingDir("frontend/")
    setArgs(listOf("run", "devBuild"))
}