package fr.maif.codelab.spring.backkotlincoroutinepokedex.web.mapper

import fr.maif.codelab.spring.backkotlincoroutinepokedex.domain.models.*
import fr.maif.codelab.spring.backkotlincoroutinepokedex.web.dto.*
import org.springframework.stereotype.Component
import java.text.DecimalFormat
import java.text.NumberFormat

@Component
class PokemonMapperDto {
    private fun mapImageUrl(pokId: Int): String =
        "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/official-artwork/${pokId}.png"

    private fun mapIdLabel(pokId: Int): String =
        String.format("#%04d", pokId)

    private fun mapFlavorText(flavorText: String): String =
        flavorText.replace("\n", " ").replace("\u000c", " ")

    private fun mapIntToKg(data: Int): String {
        val formatter: NumberFormat = DecimalFormat()
        return formatter.format(data / 10)
    }

    fun mapPokemonToPokemonDto(pokemon: Pokemon): PokemonDto {
        val name: String = pokemon.name.replaceFirstChar { it.uppercase() }
        return PokemonDto(pokemon.id, mapIdLabel(pokemon.id), name, mapImageUrl(pokemon.id))
    }

    fun mapCompletePokemonToCompletePokemonDto(completePokemon: CompletePokemon): CompletePokemonDto {
        val pokemonStatDto: List<PokemonStatDto> =
            completePokemon.pokemonStat.mapNotNull { pokemonStat ->
                StatNameDto.mapStatNameToStatNameDto(pokemonStat.statName)
                    ?.let { PokemonStatDto(pokemonStat.baseStat, it) }
            }

        val pokemonTypesDto: List<PokemonTypesDto> = completePokemon.pokemonTypes.map {
            PokemonTypesDto.mapPokemonTypesToPokemonTypesDto(it)
        }
        val evoChain =
            EvolutionChainDto(mapEvolvesToDetailsToEvoToDto(completePokemon.evolutionChain.evolvesTo))

        return CompletePokemonDto(
            completePokemon.id,
            mapIdLabel(completePokemon.id),
            completePokemon.name.replaceFirstChar { it.uppercase() },
            mapIntToKg(completePokemon.weight),
            completePokemon.cries,
            mapIntToKg(completePokemon.height),
            completePokemon.imageUrl,
            mapFlavorText(completePokemon.flavorText),
            pokemonTypesDto,
            pokemonStatDto,
            completePokemon.abilities.map { it.uppercase() },
            evoChain
        )
    }

    fun mapEvolvesToDetailsToEvoToDto(evolvesToDetails: EvolvesToDetails): EvolvesToDto {
        val pokemon: PokemonDto = mapPokemonToPokemonDto(evolvesToDetails.pokemon)
        val evolvesDetails: List<EvolvesToDto> = evolvesToDetails.evolvesToDetails.map {
            mapEvolvesToDetailsToEvoToDto(it)
        }
        return EvolvesToDto(evolvesDetails, pokemon)
    }

    fun mapVersionToVersionDto(version: Version): VersionDto {
        val imageVersion = "https://img.pokemondb.net/boxes/lg/${version.name}-large.jpg"
        return VersionDto(version.name.replaceFirstChar { it.uppercase() }, imageVersion)
    }

    fun mapItemDetailsToItemDetailsDto(itemDetails: ItemDetails): ItemDetailsDto =
        ItemDetailsDto(
            itemDetails.spriteUrl,
            itemDetails.name.replaceFirstChar { it.uppercase() },
            itemDetails.id,
            itemDetails.effect,
            itemDetails.category
        )

    fun mapMoveDetailsToMoveDetailsDto(moveDetails: MoveDetails): MoveDetailsDto {
        val pokemonTypes: PokemonTypesDto = PokemonTypesDto.mapPokemonTypesToPokemonTypesDto(moveDetails.types)
        val pokemon: List<PokemonDto> = moveDetails.pokemons.map { mapPokemonToPokemonDto(it) }
        return MoveDetailsDto(
            moveDetails.name.replaceFirstChar { it.uppercase() },
            moveDetails.power,
            moveDetails.pp,
            pokemonTypes,
            mapFlavorText(moveDetails.flavorText),
            pokemon
        )
    }
}