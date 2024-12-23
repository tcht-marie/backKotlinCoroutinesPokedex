package fr.maif.codelab.spring.backkotlincoroutinepokedex.config

import fr.maif.codelab.spring.backkotlincoroutinepokedex.domain.services.TrainerService
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.authentication.ReactiveAuthenticationManager
import org.springframework.security.authentication.UserDetailsRepositoryReactiveAuthenticationManager
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.config.web.server.ServerHttpSecurity.CsrfSpec
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.context.SecurityContextHolderStrategy
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.server.SecurityWebFilterChain
import org.springframework.security.web.server.authentication.logout.HeaderWriterServerLogoutHandler
import org.springframework.security.web.server.authentication.logout.HttpStatusReturningServerLogoutSuccessHandler
import org.springframework.security.web.server.authentication.logout.WebSessionServerLogoutHandler
import org.springframework.security.web.server.context.ServerSecurityContextRepository
import org.springframework.security.web.server.context.WebSessionServerSecurityContextRepository
import org.springframework.security.web.server.header.ClearSiteDataServerHttpHeadersWriter
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatchers

@Configuration
@EnableWebFluxSecurity
class Security {

    @Bean
    fun securityFilterChain(
        http: ServerHttpSecurity,
        authenticationManager: ReactiveAuthenticationManager,
        serverSecurityContextRepository: ServerSecurityContextRepository
    ): SecurityWebFilterChain {
        val clearSiteData = HeaderWriterServerLogoutHandler(ClearSiteDataServerHttpHeadersWriter(ClearSiteDataServerHttpHeadersWriter.Directive.ALL))

        http
            .csrf(CsrfSpec::disable)
            .authenticationManager(authenticationManager)
            .securityContextRepository(serverSecurityContextRepository)
            .logout { it
                .logoutUrl("/logout")
                .requiresLogout(ServerWebExchangeMatchers.pathMatchers(HttpMethod.GET, "/logout"))
                .logoutHandler(clearSiteData)
                .logoutHandler(WebSessionServerLogoutHandler())
                .logoutSuccessHandler(HttpStatusReturningServerLogoutSuccessHandler())
            }
            .authorizeExchange { exchanges ->
                exchanges.pathMatchers("/pokemons/pokedex/me", "/pokemons/pokedex/{pokemonId}/me").authenticated()
                    .anyExchange().permitAll()
            }

            return http.build()
    }

    @Bean
    fun passwordEncoder(): PasswordEncoder = BCryptPasswordEncoder()

    @Bean
    fun authenticationManager(trainerService: TrainerService, passwordEncoder: PasswordEncoder): ReactiveAuthenticationManager {
        val authentication = UserDetailsRepositoryReactiveAuthenticationManager(trainerService)
        authentication.setPasswordEncoder(passwordEncoder)
        return authentication
    }

    @Bean
    fun securityContextHolderStrategy(): SecurityContextHolderStrategy = SecurityContextHolder.getContextHolderStrategy()

    @Bean
    fun serverSecurityContextRepository(): ServerSecurityContextRepository = WebSessionServerSecurityContextRepository()
}
