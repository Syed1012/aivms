rootProject.name = "aivms-backend"

dependencyResolutionManagement {
    repositories {
        mavenCentral()
    }
}

//includeBuild("api")
includeBuild("app")
includeBuild("lib")