package fr.maif.codelab.spring.backkotlincoroutinepokedex.infrastructure.models

import com.fasterxml.jackson.annotation.JsonProperty

data class PokemonEntryInfra(
    @JsonProperty("entry_number") val entryNumber: Int,
    @JsonProperty("pokemon_species") val pokemonSpecies: PokemonSpeciesInfra
)
