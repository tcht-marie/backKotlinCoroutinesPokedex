package fr.maif.codelab.spring.backkotlincoroutinepokedex.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.client.WebClient

@Configuration(proxyBeanMethods = false)
class BeanConfig {
    @Bean
    fun webClient(
        @Value("\${pokeapi.base-url}") baseUrl: String,
    ) = WebClient.create(baseUrl)
}