rootProject.name = "aivms-backend"

dependencyResolutionManagement {
    repositories {
        mavenCentral()
    }
}

includeBuild("auth-service")
includeBuild("vehicle-service")
includeBuild("booking-service")
includeBuild("notification-service")