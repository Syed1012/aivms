plugins {
    base
    idea
}

tasks.clean {
    description = "Clean all app microservices"
    dependsOn(subprojects.map { it.tasks["clean"] })
}

tasks.build {
    description = "Build all app microservices"
    dependsOn(subprojects.map { it.tasks["build"] })
}
