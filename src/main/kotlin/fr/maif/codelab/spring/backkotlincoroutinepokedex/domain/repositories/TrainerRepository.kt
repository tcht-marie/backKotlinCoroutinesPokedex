package fr.maif.codelab.spring.backkotlincoroutinepokedex.domain.repositories

import fr.maif.codelab.spring.backkotlincoroutinepokedex.domain.models.Trainer
import reactor.core.publisher.Mono

interface TrainerRepository  {
    fun register(username: String, password: String): Mono<Trainer>

    fun logIn(username: String): Mono<Trainer>
}