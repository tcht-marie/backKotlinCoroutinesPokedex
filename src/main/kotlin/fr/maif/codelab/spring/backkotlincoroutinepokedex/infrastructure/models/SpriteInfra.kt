package fr.maif.codelab.spring.backkotlincoroutinepokedex.infrastructure.models

import com.fasterxml.jackson.annotation.JsonProperty

data class SpriteInfra(
    @JsonProperty("default") val defaultSprite: String?,
    val other: OtherSprites?
)
