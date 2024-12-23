package fr.maif.codelab.spring.backkotlincoroutinepokedex.infrastructure.models

import com.fasterxml.jackson.annotation.JsonProperty

data class EvolvesToDetailsInfra(
    val species: PokemonSpeciesInfra,
    @JsonProperty("evolves_to") val evolvesTo: List<EvolvesToDetailsInfra>
)
