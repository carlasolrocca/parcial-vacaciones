plugins {
    kotlin("jvm") version "2.1.10"
}

group = "ar.edu.unsam.algo2"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
    implementation("org.uqbar-project:geodds-xtend:1.0.3")
    implementation("org.uqbar-project:geodds-xtend:1.0.3")
    implementation ("com.fasterxml.jackson.module:jackson-module-kotlin:2.18.+")
    implementation ("com.fasterxml.jackson.module:jackson-module-kotlin:2.18.+")
}

tasks.withType<Test> {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(21)
}