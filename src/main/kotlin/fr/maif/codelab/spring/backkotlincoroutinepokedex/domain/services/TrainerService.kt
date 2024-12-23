package fr.maif.codelab.spring.backkotlincoroutinepokedex.domain.services

import fr.maif.codelab.spring.backkotlincoroutinepokedex.domain.models.Trainer
import org.springframework.security.core.userdetails.ReactiveUserDetailsService
import reactor.core.publisher.Mono

interface TrainerService: ReactiveUserDetailsService {
    fun register(username: String, password: String): Mono<Trainer>

    //fun logIn(username: String): Mono<Trainer>
}