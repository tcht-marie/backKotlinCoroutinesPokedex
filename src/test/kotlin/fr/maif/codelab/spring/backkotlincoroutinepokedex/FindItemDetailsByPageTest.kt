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
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.util.StreamUtils
import java.nio.charset.StandardCharsets

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@AutoConfigureWebTestClient
class FindItemDetailsByPageTest {
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
    fun findItemDetailsByPageTest() {
        wireMock.stubFor(
            WireMock.get("/item")
                .willReturn(
                    WireMock.aResponse()
                        .withBody(
                            StreamUtils.copyToString(
                                ClassPathResource("data/findItemDetailsByPage/items.json").inputStream,
                                StandardCharsets.UTF_8
                            )
                        )
                )
        )

        wireMock.stubFor(
            WireMock.get("/item/1")
                .willReturn(
                    WireMock.aResponse()
                        .withBody(
                            StreamUtils.copyToString(
                                ClassPathResource("data/findItemDetailsByPage/item_details_1.json").inputStream,
                                StandardCharsets.UTF_8
                            )
                        )
                )
        )

        webTestClient.get().uri("/pokemons/items?limit=1&offset=0")
            .exchange()
            .expectStatus().isOk
            .expectBody().json(
                StreamUtils.copyToString(
                    ClassPathResource("data/findItemDetailsByPage/result.json").inputStream,
                    StandardCharsets.UTF_8
                ), true
            )
    }
}