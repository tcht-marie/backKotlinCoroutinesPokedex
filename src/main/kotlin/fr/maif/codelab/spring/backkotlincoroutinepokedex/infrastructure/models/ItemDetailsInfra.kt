package fr.maif.codelab.spring.backkotlincoroutinepokedex.infrastructure.models

import com.fasterxml.jackson.annotation.JsonProperty

data class ItemDetailsInfra(
    val sprites: SpriteInfra?,
    val name: String,
    val id: Int,
    @JsonProperty("effect_entries") val effectEntries: List<EffectEntryInfra>,
    val category: CategoryInfra
)
