package fr.maif.codelab.spring.backkotlincoroutinepokedex.infrastructure.models

data class PokemonInfra(
    val id: Int,
    val name: String,
    val sprites: SpriteInfra,
    val weight: Int,
    val cries: CriesInfra,
    val height: Int,
    val types: List<TypeInfra>,
    val stats: List<StatInfra>,
    val abilities: List<AbilityInfra>
)
