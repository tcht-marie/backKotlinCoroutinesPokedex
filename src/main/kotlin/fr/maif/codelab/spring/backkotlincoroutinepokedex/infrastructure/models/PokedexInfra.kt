package fr.maif.codelab.spring.backkotlincoroutinepokedex.infrastructure.models

import com.fasterxml.jackson.annotation.JsonProperty

data class PokedexInfra(@JsonProperty("pokemon_entries") val pokemonEntries: List<PokemonEntryInfra>)
