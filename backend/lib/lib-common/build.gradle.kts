plugins {
	kotlin("jvm") version "1.9.25"
	id("java-library") // this will enable other modules to depend on it
}

group = "de.syed.aivms"
version = "0.0.1-SNAPSHOT"

repositories {
	mavenCentral()
}

dependencies {
	implementation("org.jetbrains.kotlin:kotlin-reflect")
	implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
}

tasks.withType<Test> {
	useJUnitPlatform()
}
