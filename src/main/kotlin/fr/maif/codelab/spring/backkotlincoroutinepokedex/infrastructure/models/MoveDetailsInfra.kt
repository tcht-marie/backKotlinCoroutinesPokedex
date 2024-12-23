package fr.maif.codelab.spring.backkotlincoroutinepokedex.infrastructure.models

import com.fasterxml.jackson.annotation.JsonProperty

data class MoveDetailsInfra(
    val name: String,
    val power: Int,
    val pp: Int,
    val type: SubTypeInfra,
    @JsonProperty("flavor_text_entries") val flavorText: List<FlavorTextEntryInfra>,
    @JsonProperty("learned_by_pokemon") val pokemon: List<PokemonSpeciesInfra>
    )
