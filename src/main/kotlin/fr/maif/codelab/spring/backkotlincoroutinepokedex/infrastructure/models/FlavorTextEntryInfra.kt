package fr.maif.codelab.spring.backkotlincoroutinepokedex.infrastructure.models

import com.fasterxml.jackson.annotation.JsonProperty

data class FlavorTextEntryInfra(@JsonProperty("flavor_text") val flavorText: String?, val language: LanguageInfra)
