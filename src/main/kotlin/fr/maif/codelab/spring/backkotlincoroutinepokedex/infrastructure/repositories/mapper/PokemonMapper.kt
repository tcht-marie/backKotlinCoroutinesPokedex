package fr.maif.codelab.spring.backkotlincoroutinepokedex.infrastructure.repositories.mapper

import fr.maif.codelab.spring.backkotlincoroutinepokedex.domain.models.*
import fr.maif.codelab.spring.backkotlincoroutinepokedex.infrastructure.models.*
import org.springframework.stereotype.Component
import java.util.*

@Component
class PokemonMapper {
    fun mapPokedexInfraToPokedexPage(pokedexInfra: PokedexInfra, limit: Int, offset: Int): PokedexPage {
        val max: Int = (offset + limit).coerceAtMost(pokedexInfra.pokemonEntries.size)
        val pokemonSpecies: List<Pokemon> = pokedexInfra.pokemonEntries.subList(offset, max).map { pokemonEntry ->
            val id: Int = pokemonEntry.entryNumber;
            Pokemon(id, pokemonEntry.pokemonSpecies.name, null, null)
        }
        return PokedexPage(pokemonSpecies, pokedexInfra.pokemonEntries.size)
    }

    fun mapPokedexInfraToPokedexByName(
        pokedexInfra: PokedexInfra, query: String, limit: Int, offset: Int
    ): PokedexPage {
        val pokemonSearch: List<PokemonEntryInfra> =
            pokedexInfra.pokemonEntries.filter { it.pokemonSpecies.name.contains(query.lowercase()) }
        val max: Int = (offset + limit).coerceAtMost(pokemonSearch.size)
        val pokemonSpecies: List<Pokemon> = pokemonSearch.subList(offset, max).map {
            val id: Int = it.entryNumber;
            Pokemon(id, it.pokemonSpecies.name, null, null)
        }
        return PokedexPage(pokemonSpecies, pokedexInfra.pokemonEntries.size)
    }

    fun mapPokemonInfraToPokemonDetails(pokemonInfra: PokemonInfra): PokemonDetails {
        val pokemonTypes: List<PokemonTypes> = pokemonInfra.types.map { type ->
            PokemonTypes.mapStringToPokemonType(type.type.name)
        }
        val pokemonStat: List<PokemonStat> = pokemonInfra.stats.map {
            PokemonStat(it.baseStat, StatName.mapStringToStatName(it.stat.name))
        }
        val abilities: List<String> = pokemonInfra.abilities.map { it.ability.name }
        val image: String = pokemonInfra.sprites.other!!.officialArtwork.frontDefault

        return PokemonDetails(
            pokemonInfra.id,
            pokemonInfra.name,
            pokemonInfra.weight,
            pokemonInfra.cries.latest,
            pokemonInfra.height,
            image,
            pokemonTypes,
            pokemonStat,
            abilities
        )
    }

    fun mapVersionInfraToVersion(pageVersionInfra: PageGenericInfra<VersionInfra>): List<Version> =
        pageVersionInfra.results.map { Version(it.name) }

    fun mapItemInfraToItem(pageItemInfra: PageGenericInfra<ItemInfra>): List<Item> =
        pageItemInfra.results.mapNotNull { item ->
            getIdFromUrl(item.url)?.let { Item(item.name, it) }
        }

    fun mapItemDetailsInfraToItemDetails(itemDetailsInfra: ItemDetailsInfra): ItemDetails {
        val effect: String = itemDetailsInfra.effectEntries.map { EffectEntryInfra::shortEffect }.first().toString()
        val defaultSprite: String = itemDetailsInfra.sprites?.defaultSprite?.let { it }?.first().toString()
        return ItemDetails(
            defaultSprite,
            itemDetailsInfra.name,
            itemDetailsInfra.id,
            effect,
            itemDetailsInfra.category.name
        )
    }

    fun mapMoveInfraToMove(pageMoveInfra: PageGenericInfra<MoveInfra>): List<Move> =
        pageMoveInfra.results.mapNotNull { move ->
            getIdFromUrl(move.url)?.let { Move(move.name, it) }
        }

    fun mapMoveDetailsInfraToMoveDetails(moveDetailsInfra: MoveDetailsInfra): MoveDetails {
        val pokemonTypes: PokemonTypes = PokemonTypes.mapStringToPokemonType(moveDetailsInfra.type.name)
        val flavorText: String = moveDetailsInfra.flavorText.map { FlavorTextEntryInfra::flavorText }.first().toString()
        val pokemons: List<Pokemon> = moveDetailsInfra.pokemon.map { mapSpeciesInfraToPokemon(it) }
        return MoveDetails(
            moveDetailsInfra.name,
            moveDetailsInfra.power,
            moveDetailsInfra.pp,
            pokemonTypes,
            flavorText,
            pokemons
        )
    }

    fun mapSpeciesInfraToPokemon(pokemonSpeciesInfra: PokemonSpeciesInfra): Pokemon {
        val flavorText = pokemonSpeciesInfra.flavorTextEntry?.filter { element ->
            element.language.name == "en"
        }?.map { it.flavorText }?.firstOrNull()
        val evoChainId = getIdFromUrl(pokemonSpeciesInfra.evolutionChainInfra?.url)
        val id = pokemonSpeciesInfra.id ?: getIdFromUrl(pokemonSpeciesInfra.url)
        return id?.let {
            Pokemon(
                it,
                pokemonSpeciesInfra.name,
                flavorText,
                evoChainId
            )
        } ?: throw NoSuchElementException("Pokemon not found")
    }

    /*fun mapSpeciesInfraToPokemon(pokemonSpeciesInfra: PokemonSpeciesInfra): Pokemon {
        val flavorText = pokemonSpeciesInfra.flavorTextEntry?.filter { element ->
            element.language.name == "en"
        }?.map { it.flavorText }?.firstOrNull()
        val evoChainId: Int? = pokemonSpeciesInfra.evolutionChainInfra
            ?.let { EvolutionChainInfra::url }
            ?.let { getIdFromUrl(it.toString()) }
        //val id: Int? = getIdFromUrl(pokemonSpeciesInfra.url)
        return getIdFromUrl(pokemonSpeciesInfra.url)?.let {
            Pokemon(
                it,
                pokemonSpeciesInfra.name,
                flavorText,
                evoChainId
            )
        }
            ?: throw NoSuchElementException("Pokemon not found")
    }*/

    fun mapEvoDetailsToChainEvolution(evoChainDetailsInfra: EvolutionChainDetailsInfra): EvolutionChain =
        EvolutionChain(mapEvoToDetailsInfraToEvoToDetails(evoChainDetailsInfra.chain))

    fun mapEvoToDetailsInfraToEvoToDetails(evolvesToDetailsInfra: EvolvesToDetailsInfra): EvolvesToDetails {
        val pokemon: Pokemon = mapSpeciesInfraToPokemon(evolvesToDetailsInfra.species)
        val evolvesToDetails: List<EvolvesToDetails> = evolvesToDetailsInfra.evolvesTo.map {
            mapEvoToDetailsInfraToEvoToDetails(it)
        }
        return EvolvesToDetails(pokemon, evolvesToDetails)
    }

    fun mapPokemonsIdsToPokemon(pokemonIds: List<Int>, pokedex: PokedexInfra): List<Pokemon> =
        pokedex.pokemonEntries.filter { element ->
            pokemonIds.contains(element.entryNumber)
        }.map { element ->
            Pokemon(
                element.entryNumber,
                element.pokemonSpecies.name,
                null,
                null
            )
        }

    private fun getIdFromUrl(url: String?): Int? =
        url?.let {
            val split: List<String> = url.split("/")
            split[6].toInt()
        }
    /*private fun getIdFromUrl(url: String?): Int? {
        return url?.let {
            val split: List<String> = it.split("/")
            print("split $split")
            if (split.size > 6) {
                print("if split $split")
                split[6].toInt()
            } else {
                null
            }
        }
    }*/
}