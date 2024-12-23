package fr.maif.codelab.spring.backkotlincoroutinepokedex.infrastructure.models

import com.fasterxml.jackson.annotation.JsonProperty

data class OfficialArtworkInfra(@JsonProperty("front_default") val frontDefault: String)
