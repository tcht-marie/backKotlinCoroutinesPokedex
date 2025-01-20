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
class FindMoveDetailsByPageTest {
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
    fun findMoveDetailsByPageTest() {
        wireMock.stubFor(
            WireMock.get(WireMock.urlPathEqualTo("/move"))
                .willReturn(
                    WireMock.aResponse()
                        .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                        .withBody(
                            StreamUtils.copyToString(
                                ClassPathResource("data/findMoveDetailsByPage/moves.json").inputStream,
                                StandardCharsets.UTF_8
                            )
                        )
                )
        )

        wireMock.stubFor(
            WireMock.get(WireMock.urlPathEqualTo("/move/1"))
                .willReturn(
                    WireMock.aResponse()
                        .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                        .withBody(
                            StreamUtils.copyToString(
                                ClassPathResource("data/findMoveDetailsByPage/move_details_1.json").inputStream,
                                StandardCharsets.UTF_8
                            )
                        )
                )
        )

        webTestClient.get().uri("/pokemons/moves?limit=1&offset=0")
            .exchange()
            .expectStatus().isOk
            .expectBody().json(
                StreamUtils.copyToString(
                    ClassPathResource("data/findMoveDetailsByPage/result.json").inputStream,
                    StandardCharsets.UTF_8
                ), true
            )
    }
}
// FIXME : error
//[0].flavorText
//Expected: Pounds with fore­ legs or tail.
//     got: val fr.maif.codelab.spring.backkotlincoroutinepokedex.infrastructure.models.FlavorTextEntryInfra.flavorText: kotlin.String?
// ; [0]
//Expected: pokemonTypes
//     but none found
// ; [0]
//Unexpected: types