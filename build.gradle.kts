import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.7.10"
    application
}

group = "me.user"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven {
        url = uri("https://mvn.mchv.eu/repository/mchv/")
    }
}

dependencies {
    // import the BOM
    implementation(platform("it.tdlight:tdlight-java-bom:2.8.4.1"))

    // do not specify the versions on the dependencies below!
    implementation("it.tdlight:tdlight-java")
    implementation("it.tdlight:tdlight-natives-windows-amd64")

    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "17"
}

application {
    mainClass.set("MainKt")
}