package fr.maif.codelab.spring.backkotlincoroutinepokedex.web.controllers

import fr.maif.codelab.spring.backkotlincoroutinepokedex.domain.services.TrainerService
import fr.maif.codelab.spring.backkotlincoroutinepokedex.web.dto.LoginRequestDto
import fr.maif.codelab.spring.backkotlincoroutinepokedex.web.dto.TrainerDto
import fr.maif.codelab.spring.backkotlincoroutinepokedex.web.mapper.TrainerMapperDto
import kotlinx.coroutines.reactor.awaitSingle
import kotlinx.coroutines.reactor.awaitSingleOrNull
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.authentication.ReactiveAuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContext
import org.springframework.security.core.context.SecurityContextHolderStrategy
import org.springframework.security.web.server.context.ServerSecurityContextRepository
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/auth")
class TrainerController(
    private val trainerService: TrainerService,
    private val trainerMapperDto: TrainerMapperDto,
    private val authenticationManager: ReactiveAuthenticationManager,
    private val securityContextHolderStrategy: SecurityContextHolderStrategy,
    private val serverSecurityContextRepository: ServerSecurityContextRepository
) {

    @PostMapping("/register", produces = [MediaType.APPLICATION_JSON_VALUE])
    suspend fun register(@RequestBody loginRequestDto: LoginRequestDto): ResponseEntity<TrainerDto> {
        return trainerService.register(loginRequestDto.username, loginRequestDto.password)
            // Attends la valeur unique du Mono donné sans bloquer le thread,
            // puis renvoie la valeur résultante. S'il produit une erreur, lève l'exception correspondante.
            // Si pas de valeur, renvoie null
            .awaitSingleOrNull()
            ?.let { trainerMapperDto.mapTrainerToTrainerDto(it) }
            ?.let { ResponseEntity.ok().body(it) }
            ?: ResponseEntity.internalServerError().build()
    }

    @PostMapping("/login", produces = [MediaType.APPLICATION_JSON_VALUE])
    suspend fun login(
        @RequestBody loginRequestDto: LoginRequestDto,
        exchange: ServerWebExchange
    ): ResponseEntity<Unit> {
        val token: UsernamePasswordAuthenticationToken = UsernamePasswordAuthenticationToken.unauthenticated(
            loginRequestDto.username, loginRequestDto.password
        )
        val authentication = authenticationManager.authenticate(token).awaitSingle()
        return if (authentication.isAuthenticated) {
            val context: SecurityContext = securityContextHolderStrategy.createEmptyContext()
            context.authentication = authentication
            serverSecurityContextRepository.save(exchange, context).awaitSingle()
            ResponseEntity.ok().build()
        } else {
            ResponseEntity.internalServerError().build()
        }
    }

    /*@PostMapping("/register", produces = [MediaType.APPLICATION_JSON_VALUE])
    fun register(@RequestBody loginRequestDto: LoginRequestDto): Mono<ResponseEntity<TrainerDto>> {
        return trainerService.register(loginRequestDto.username, loginRequestDto.password)
            // .map { trainer -> trainerMapperDto.mapTrainerToTrainerDto(trainer) }
            //.map(trainerMapperDto::mapTrainerToTrainerDto)
            .map { trainerMapperDto.mapTrainerToTrainerDto(it) }
            // ResponseEntity = wrapper complet autour de la réponse http
            .map { ResponseEntity.ok().body(it) }
            .defaultIfEmpty(ResponseEntity.internalServerError().build())
    }*/

    /*@PostMapping("/login", produces = [MediaType.APPLICATION_JSON_VALUE])
    fun login(@RequestBody loginRequestDto: LoginRequestDto, exchange: ServerWebExchange): Mono<Void> {
        val token: UsernamePasswordAuthenticationToken = UsernamePasswordAuthenticationToken.unauthenticated(
            loginRequestDto.username, loginRequestDto.password
        )
            return authenticationManager.authenticate(token)
                .flatMap {
                    if (it.isAuthenticated) {
                        val context: SecurityContext = securityContextHolderStrategy.createEmptyContext()
                        context.authentication = it
                        serverSecurityContextRepository.save(exchange, context)
                    } else {
                        Mono.error(BadCredentialsException("Invalid credentials"))
                    }
                }
    }*/
}