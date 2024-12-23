package fr.maif.codelab.spring.backkotlincoroutinepokedex

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class BackKotlinCoroutinePokedexApplication

    fun main(args: Array<String>) {
        runApplication<BackKotlinCoroutinePokedexApplication>(*args)
    }