package fr.maif.codelab.spring.backkotlincoroutinepokedex.infrastructure.models

import com.fasterxml.jackson.annotation.JsonProperty

data class PokemonSpeciesInfra(
    val name: String,
    val id: Int?,
    val url: String?,
    @JsonProperty("evolution_chain") val evolutionChainInfra: EvolutionChainInfra?,
    @JsonProperty("flavor_text_entries") val flavorTextEntry: List<FlavorTextEntryInfra>?
)
