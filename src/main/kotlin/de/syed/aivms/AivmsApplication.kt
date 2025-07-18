package de.syed.aivms

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class AivmsApplication

fun main(args: Array<String>) {
    runApplication<AivmsApplication>(*args)
}
