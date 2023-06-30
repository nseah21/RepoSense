import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.gradle.api.tasks.Exec
import org.gradle.api.file.DuplicatesStrategy
import org.gradle.api.plugins.quality.Checkstyle
import org.gradle.nativeplatform.platform.internal.DefaultNativePlatform
import org.gradle.api.tasks.testing.Test

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

val os: OperatingSystem = DefaultNativePlatform.getCurrentOperatingSystem()

application {
    mainClassName = "reposense.RepoSense"
}

node {
    setDownload(false)
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

repositories {
    mavenCentral()
}

/* Still need to extend from testImplementation and testRuntime */
val systemtestImplementation = configurations.create("systemtestImplementation")
val systemtestRuntime = configurations.create("systemtestRuntime")

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

val buildFrontend = tasks.register<Exec>("buildFrontend") {
    setDependsOn(listOf(installFrontend))
    setCommandLine("cmd")

    setWorkingDir("frontend/")
    setArgs(listOf("run", "devBuild"))
}

val zipReport = tasks.register<Zip>("zipReport") {
    setDependsOn(listOf(buildFrontend))
    from("frontend/build/")

    archiveBaseName.set("templateZip")
    destinationDirectory.set(file("src/main/resources"))
}

tasks.register<Copy>("copyCypressConfig") {
    description = "Copies the config files used by the backend to generate the test report for Cypress testing into an isolated working directory"

    from("frontend/cypress/config/")
    into("build/serveTestReport/exampleconfig/")
}

tasks.register<Copy>("copyMainClasses") {
    description = "Copies the backend classes used to generate the test report for Cypress testing into an isolated working directory"
    // setDependsOn(listOf(classes))

    from("build/classes/java/main/")
    into("build/serveTestReport/java/main/")
}

//val compileJava = tasks.compileJava

// tasks.named<ProcessResources>("processSystemtestResources").configure {
//     duplicatesStrategy = DuplicatesStrategy.INCLUDE
// }

tasks.named("run") {
    dependsOn(zipReport)
    tasks.compileJava {
        mustRunAfter(zipReport)
    }
//    args = System.getProperty("args", "").split("")
//    systemProperty("version", getRepoSenseVersion())
}

checkstyle {
    toolVersion = "9.3"
    getConfigDirectory().set(file("${rootProject.projectDir}/config/checkstyle"))
}

//idea {
//    module {
//        sourceSets {
//            named("systemtest") {
//                allSource.srcDirs.forEach { srcDir ->
//                    testSourceDirs.plusAssign(srcDir)
//                }
//            }
//        }
//    }
//}

//tasks.test {
//    systemProperty("REPOSENSE_ENVIRONMENT", "TEST")
//
//    testLogging {
//        events("passed", "skipped", "failed")
//        showStandardStreams = true
//    }
//
////    doFirst {
////        deleteReposAddressDirectory()
////    }
//
//    useJUnitPlatform()
//
////    doLast {
////        deleteReposAddressDirectory()
////    }
//}

tasks.shadowJar {
    dependsOn(zipReport)
}

tasks.named<ShadowJar>("shadowJar") {
//    tasks.compileJava {
//        mustRunAfter("zipReport")
//    }
//    tasks.processResources {
//        mustRunAfter("zipReport")
//    }
    archiveFileName.set("RepoSense.jar")
    destinationDirectory.set(file("${buildDir}/jar/"))

    manifest {
//        getReposenseVersion()
    }
}

tasks.register<Exec>("lintFrontend") {
    setDependsOn(listOf(installFrontend))
    setCommandLine("npm.cmd")

    setWorkingDir("frontend/")
    setArgs(listOf("run", "lint"))
}

val checkstyleMain = tasks.checkstyleMain
val checkstyleTest = tasks.checkstyleTest
// val checkstyleSystemtest = tasks.checkstyleSystemtest

tasks.register<Checkstyle>("checkstyleAll") {
    setDependsOn(listOf(checkstyleMain, checkstyleTest)) //, checkstyleSystemtest))
    tasks.checkstyleTest {
        mustRunAfter(checkstyleMain)
    }
//    tasks.checkstyleSystemtest {
//        mustRunAfter(checkstyleTest)
//    }
}

tasks.register<Exec>("environmentalChecks") {
    setWorkingDir("config/checks/")
    if (os.isWindows()) {
        setCommandLine(listOf("cmd", "/c", "run-checks.bat"))
    } else {
        setCommandLine(listOf("sh", "./run-checks.sh"))
    }
}

tasks.register<Test>("systemtest") {
    testClassesDirs = sourceSets.getByName("systemtest").output.classesDirs
    classpath = sourceSets.getByName("systemtest").runtimeClasspath

    systemProperty("REPOSENSE_ENVIRONMENT", "TEST")

    testLogging {
        events("passed", "skipped", "failed")
        showStandardStreams = true
    }
//    doFirst {
//        deleteReposAddressDirectory()
//    }

    useJUnitPlatform()

//    doLast {
//        deleteReposAddressDirectory()
//    }
}

val serveTestReportInBackground = tasks.register("") {

}