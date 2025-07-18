rootProject.name = "app"

dependencyResolutionManagement {
    repositories {
        mavenCentral()
    }
}

include("auth-service")
// later: include("user-service"), etc.