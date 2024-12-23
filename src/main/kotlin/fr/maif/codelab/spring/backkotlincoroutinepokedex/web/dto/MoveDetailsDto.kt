package fr.maif.codelab.spring.backkotlincoroutinepokedex.web.dto

data class MoveDetailsDto(
    val name: String,
    val power: Int,
    val pp: Int,
    val types: PokemonTypesDto,
    val flavorText: String,
    val pokemons: List<PokemonDto>
)
