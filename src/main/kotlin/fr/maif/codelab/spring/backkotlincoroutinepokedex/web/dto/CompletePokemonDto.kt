package fr.maif.codelab.spring.backkotlincoroutinepokedex.web.dto

data class CompletePokemonDto(
    val id: Int,
    val idLabel: String,
    val name: String,
    val weight: String,
    val cries: String,
    val height: String,
    val imageUrl: String,
    val flavorText: String,
    val pokemonTypes: List<PokemonTypesDto>,
    val pokemonStat: List<PokemonStatDto>,
    val abilities: List<String>,
    val evolutionChain: EvolutionChainDto
)
