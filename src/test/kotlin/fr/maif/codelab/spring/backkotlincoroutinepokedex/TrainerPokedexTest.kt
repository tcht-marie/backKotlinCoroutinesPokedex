package fr.maif.codelab.spring.backkotlincoroutinepokedex

import com.github.tomakehurst.wiremock.client.WireMock
import com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig
import com.github.tomakehurst.wiremock.junit5.WireMockExtension
import fr.maif.codelab.spring.backkotlincoroutinepokedex.web.dto.LoginRequestDto
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.extension.RegisterExtension
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.core.io.ClassPathResource
import org.springframework.r2dbc.core.DatabaseClient
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.util.StreamUtils
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import java.nio.charset.StandardCharsets
import kotlin.test.Test


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@AutoConfigureWebTestClient(timeout = "100000")
@Testcontainers
class TrainerPokedexTest {
    @Autowired
    private lateinit var webTestClient: WebTestClient

    @Autowired
    private lateinit var databaseClient: DatabaseClient

    companion object {
        @JvmStatic
        @RegisterExtension
        val wireMock: WireMockExtension = WireMockExtension
            .newInstance()
            .options(wireMockConfig().dynamicPort())
            .build()

        @Container
        var postgresqlContainer = PostgreSQLContainer("postgres:16.6")

        @JvmStatic
        @DynamicPropertySource
        fun configureProperties(registry: DynamicPropertyRegistry) {
            registry.add("pokeapi.base-url") { wireMock.baseUrl() }
            registry.add("spring.liquibase.url") { postgresqlContainer.jdbcUrl }
            registry.add("spring.liquibase.user") { postgresqlContainer.username }
            registry.add("spring.liquibase.password") { postgresqlContainer.password }
            registry.add("spring.r2dbc.url") {
                "r2dbc:postgresql://${postgresqlContainer.host}:${postgresqlContainer.firstMappedPort}/${postgresqlContainer.databaseName}"
            }
            registry.add("spring.r2dbc.username") { postgresqlContainer.username }
            registry.add("spring.r2dbc.password") { postgresqlContainer.password }
        }
    }

    @AfterEach
    fun tearDown() {
        wireMock.runtimeInfo.wireMock.resetMappings()
        wireMock.runtimeInfo.wireMock.resetRequests()
        wireMock.runtimeInfo.wireMock.resetScenarios()
    }

    @Test
    fun trainerAuthTest() {
        webTestClient.post().uri("/auth/register").bodyValue(LoginRequestDto("Marie", "test"))
            .exchange()
            .expectStatus().isOk

        webTestClient.post().uri("/auth/login").bodyValue(LoginRequestDto("Marie", "test"))
            .exchange()
            .expectStatus().isOk
            .expectCookie().httpOnly("SESSION", true)

        webTestClient.get().uri("/logout")
            .exchange()
            .expectStatus().isOk
    }

    @Test
    fun trainerPokedexTest() {
        databaseClient.sql("DELETE FROM trainers;").fetch().rowsUpdated().block()
        webTestClient.post().uri("/auth/register").bodyValue(LoginRequestDto("Marie", "test"))
            .exchange()
            .expectStatus().isOk

        val cookieAuth = webTestClient.post().uri("/auth/login")
            .bodyValue(LoginRequestDto("Marie", "test"))
            .exchange()
            .returnResult(Unit::class.java).responseCookies.getFirst("SESSION")!!

        wireMock.stubFor(
            WireMock.get("/pokedex/1")
                .willReturn(
                    WireMock.aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody(
                            StreamUtils.copyToString(
                                ClassPathResource("data/findPokemonByPage/pokedex_1.json").inputStream,
                                StandardCharsets.UTF_8
                            )
                        )
                )
        )
        webTestClient.post().uri("/pokemons/pokedex/1/me")
            .cookie(cookieAuth.name, cookieAuth.value)
            .exchange().expectStatus().isOk
            .expectBody().json(
                StreamUtils.copyToString(
                    ClassPathResource("data/trainerPokedex/add_or_delete_pokemon_1.json").inputStream,
                    StandardCharsets.UTF_8
                )
            )

        webTestClient.post().uri("/pokemons/pokedex/2/me")
            .cookie(cookieAuth.name, cookieAuth.value)
            .exchange().expectStatus().isOk
            .expectBody().json(
                StreamUtils.copyToString(
                    ClassPathResource("data/trainerPokedex/add_or_delete_pokemon_2.json").inputStream,
                    StandardCharsets.UTF_8
                )
            )

        webTestClient.get().uri("/pokemons/pokedex/me")
            .cookie(cookieAuth.name, cookieAuth.value)
            .exchange().expectStatus().isOk
            .expectBody().json(
                StreamUtils.copyToString(
                    ClassPathResource("data/trainerPokedex/trainer_pokedex.json").inputStream,
                    StandardCharsets.UTF_8
                )
            )

        webTestClient.delete().uri("/pokemons/pokedex/me")
            .cookie(cookieAuth.name, cookieAuth.value)
            .exchange().expectStatus().isOk

        webTestClient.get().uri("/pokemons/pokedex/me")
            .cookie(cookieAuth.name, cookieAuth.value)
            .exchange().expectStatus().isOk
    }
}
