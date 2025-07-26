plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.8.0"
}
rootProject.name = "aivms"

dependencyResolutionManagement {
    repositories {
        mavenCentral()
    }
}

include("backend:auth-service")
include("backend:vehicle-service")
include("backend:booking-service")
include("backend:notification-service")