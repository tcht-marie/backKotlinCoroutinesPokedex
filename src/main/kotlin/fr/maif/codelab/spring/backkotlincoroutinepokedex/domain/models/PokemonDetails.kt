package fr.maif.codelab.spring.backkotlincoroutinepokedex.domain.models

data class PokemonDetails(
    val id: Int,
    val name: String,
    val weight: Int,
    val cries: String,
    val height: Int,
    val imageUrl: String,
    val pokemonTypes: List<PokemonTypes>,
    val pokemonStat: List<PokemonStat>,
    val abilities: List<String>
)
