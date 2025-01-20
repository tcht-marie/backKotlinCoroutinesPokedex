package fr.maif.codelab.spring.backkotlincoroutinepokedex

import com.github.tomakehurst.wiremock.client.WireMock
import com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig
import com.github.tomakehurst.wiremock.junit5.WireMockExtension
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.core.io.ClassPathResource
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.util.StreamUtils
import java.nio.charset.StandardCharsets

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@AutoConfigureWebTestClient
class FindPokemonByIdTest {
    @Autowired
    private lateinit var webTestClient: WebTestClient

    companion object {
        @JvmStatic
        @RegisterExtension
        val wireMock: WireMockExtension = WireMockExtension
            .newInstance()
            .options(wireMockConfig().dynamicPort())
            .build()

        @JvmStatic
        @DynamicPropertySource
        fun configureProperties(registry: DynamicPropertyRegistry) {
            registry.add("pokeapi.base-url") { wireMock.baseUrl() }
        }
    }

    @AfterEach
    fun tearDown() {
        wireMock.runtimeInfo.wireMock.resetMappings()
        wireMock.runtimeInfo.wireMock.resetRequests()
        wireMock.runtimeInfo.wireMock.resetScenarios()
    }

    @Test
    fun findPokemonByIdTest() {
        wireMock.stubFor(
            WireMock.get(WireMock.urlPathEqualTo("/pokemon/104"))
                .willReturn(
                    WireMock.aResponse()
                        .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                        .withBody(
                            StreamUtils.copyToString(
                                ClassPathResource("data/findPokemonById/pokemon_details_104.json").inputStream,
                                StandardCharsets.UTF_8
                            )
                        )
                )
        )

        wireMock.stubFor(
            WireMock.get(WireMock.urlPathEqualTo("/pokemon-species/104"))
                .willReturn(
                    WireMock.aResponse()
                        .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                        .withBody(
                            StreamUtils.copyToString(
                                ClassPathResource("data/findPokemonById/species_104.json").inputStream,
                                StandardCharsets.UTF_8
                            )
                        )
                )
        )

        wireMock.stubFor(
            WireMock.get(WireMock.urlPathEqualTo("/evolution-chain/46"))
                .willReturn(
                    WireMock.aResponse()
                        .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                        .withBody(
                            StreamUtils.copyToString(
                                ClassPathResource("data/findPokemonById/evolution_chain_46.json").inputStream,
                                StandardCharsets.UTF_8
                            )
                        )
                )
        )

        webTestClient.get().uri("/pokemons/pokemon/104")
            .exchange()
            .expectStatus().isOk
            .expectBody().json(
                StreamUtils.copyToString(
                    ClassPathResource("data/findPokemonById/result.json").inputStream,
                    StandardCharsets.UTF_8
                ), true
            )
    }
}
// FIXME : error
// org.springframework.core.io.buffer.DataBufferLimitException: Exceeded limit on max bytes to buffer : 262144