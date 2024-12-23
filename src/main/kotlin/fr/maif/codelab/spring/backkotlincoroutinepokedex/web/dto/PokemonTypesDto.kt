package fr.maif.codelab.spring.backkotlincoroutinepokedex.web.dto

import fr.maif.codelab.spring.backkotlincoroutinepokedex.domain.models.PokemonTypes

enum class PokemonTypesDto {
    NORMAL,
    FIGHTING,
    FLYING,
    POISON,
    GROUND,
    ROCK,
    BUG,
    GHOST,
    STEEL,
    FIRE,
    WATER,
    GRASS,
    ELECTRIC,
    PSYCHIC,
    ICE,
    DRAGON,
    DARK,
    FAIRY,
    STELLAR,
    SHADOW,
    UNKNOWN;

    companion object StaticFun {
        fun mapPokemonTypesToPokemonTypesDto(pokemonTypes: PokemonTypes): PokemonTypesDto {
            return when(pokemonTypes) {
                PokemonTypes.NORMAL -> NORMAL
                PokemonTypes.FIGHTING -> FIGHTING
                PokemonTypes.FLYING -> FLYING
                PokemonTypes.POISON -> POISON
                PokemonTypes.GROUND -> GROUND
                PokemonTypes.ROCK -> ROCK
                PokemonTypes.BUG -> BUG
                PokemonTypes.GHOST -> GHOST
                PokemonTypes.STEEL -> STEEL
                PokemonTypes.FIRE -> FIRE
                PokemonTypes.WATER -> WATER
                PokemonTypes.GRASS -> GRASS
                PokemonTypes.ELECTRIC -> ELECTRIC
                PokemonTypes.PSYCHIC -> PSYCHIC
                PokemonTypes.ICE -> ICE
                PokemonTypes.DRAGON -> DRAGON
                PokemonTypes.DARK -> DARK
                PokemonTypes.FAIRY -> FAIRY
                PokemonTypes.STELLAR -> STELLAR
                PokemonTypes.SHADOW -> SHADOW
                else -> UNKNOWN
            }
        }
    }
}