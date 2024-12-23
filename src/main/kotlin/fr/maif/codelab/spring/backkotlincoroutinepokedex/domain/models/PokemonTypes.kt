package fr.maif.codelab.spring.backkotlincoroutinepokedex.domain.models

import java.util.*

enum class PokemonTypes {
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
        fun mapStringToPokemonType(type: String?): PokemonTypes {
            return Arrays.stream(entries.toTypedArray())
                .filter { pokemonTypes: PokemonTypes ->
                    pokemonTypes.name.equals(
                        type,
                        ignoreCase = true
                    )
                }
                .findFirst()
                .orElse(UNKNOWN)
        }
    }
}