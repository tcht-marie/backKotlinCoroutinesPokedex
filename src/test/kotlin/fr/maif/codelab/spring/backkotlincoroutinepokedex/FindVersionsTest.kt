package fr.maif.codelab.spring.backkotlincoroutinepokedex

import com.github.tomakehurst.wiremock.client.WireMock
import com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig
import com.github.tomakehurst.wiremock.junit5.WireMockExtension
import org.junit.jupiter.api.AfterEach
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
import java.io.FileNotFoundException
import java.nio.charset.StandardCharsets
import kotlin.test.Test

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@AutoConfigureWebTestClient
class FindVersionsTest {

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
    fun findVersionsTest() {
        val version = ClassPathResource("data/findVersions/version.json")
        if (!version.exists()) {
            throw FileNotFoundException("Resource not found: ${version.path}")
        }

        wireMock.stubFor(
            WireMock.get(WireMock.urlPathEqualTo("/version"))
                .willReturn(
                    WireMock.aResponse()
                        .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                        .withBody(
                            StreamUtils.copyToString(
                                version.inputStream,
                                StandardCharsets.UTF_8
                            )
                        )
                )
        )

        webTestClient.get().uri("/pokemons/versions")
            .exchange()
            .expectStatus().isOk
            .expectBody().json(
                StreamUtils.copyToString(
                    ClassPathResource("data/findVersions/result.json").inputStream,
                    StandardCharsets.UTF_8
                ), true
            )
    }
}
/*
val version = Paths.get("/data/findVersions/version.json")
if (!Files.exists(version)) {
    throw FileNotFoundException("Resource not found: $version")
}
val jsonContent = Files.readString(version, StandardCharsets.UTF_8)

wireMock.stubFor(
    WireMock.get("/version")
        .willReturn(
            WireMock.aResponse()
                .withBody(jsonContent)
         )
)
*/