package fr.maif.codelab.spring.backkotlincoroutinepokedex.domain.models

data class MoveDetails(
    val name: String,
    val power: Int,
    val pp: Int,
    val types: PokemonTypes,
    val flavorText: String,
    val pokemons: List<Pokemon>
)
