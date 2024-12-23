package fr.maif.codelab.spring.backkotlincoroutinepokedex.infrastructure.models

import com.fasterxml.jackson.annotation.JsonProperty

data class OtherSprites(@JsonProperty("official-artwork") val officialArtwork: OfficialArtworkInfra)
