package fr.maif.codelab.spring.backkotlincoroutinepokedex.domain.services

import fr.maif.codelab.spring.backkotlincoroutinepokedex.domain.models.Trainer
import fr.maif.codelab.spring.backkotlincoroutinepokedex.domain.repositories.TrainerRepository
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class TrainerServiceImpl(
    private val trainerRepository: TrainerRepository,
    private val passwordEncoder: PasswordEncoder
) : TrainerService {

    override fun register(username: String, password: String): Mono<Trainer> {
        val encodedPassword: String = passwordEncoder.encode(password)
        return trainerRepository.register(username, encodedPassword)
    }

    override fun findByUsername(username: String?): Mono<UserDetails> {
        return username?.let { trainerRepository.logIn(it) }
            ?.map { it } ?: Mono.empty()
    }
}