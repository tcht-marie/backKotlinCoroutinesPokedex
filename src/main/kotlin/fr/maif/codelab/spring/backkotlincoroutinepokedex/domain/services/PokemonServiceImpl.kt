package fr.maif.codelab.spring.backkotlincoroutinepokedex.domain.services

import arrow.core.Either
import arrow.core.computations.ResultEffect.bind
import arrow.core.flatMap
import arrow.core.left
import arrow.core.raise.either
import arrow.core.right
import fr.maif.codelab.spring.backkotlincoroutinepokedex.domain.models.*
import fr.maif.codelab.spring.backkotlincoroutinepokedex.domain.repositories.PokemonRepository
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.util.*
import kotlinx.coroutines.async
import kotlinx.coroutines.supervisorScope

@Service
class PokemonServiceImpl(private val pokemonRepository: PokemonRepository) : PokemonService {

    override suspend fun getPokemonsByPage(limit: Int, offset: Int): Either<PokemonServiceErrors, List<Pokemon>> =
        pokemonRepository.findPokemonsByPage(limit, offset).map { it.pokemonList }

    override suspend fun getPokedexByName(
        limit: Int,
        offset: Int,
        query: String
    ): Either<PokemonServiceErrors, List<Pokemon>> =
        pokemonRepository.searchPokemonsByName(limit, offset, query).map { it.pokemonList }

    override suspend fun getPokemonById(id: Int): Either<PokemonServiceErrors, CompletePokemon> =
        either {
            val pokemonDetails = pokemonRepository.findPokemonById(id).bind()
            val pokemonSpecies = pokemonRepository.findSpeciesById(pokemonDetails.id).bind()
            val evoChain = pokemonSpecies.evolutionChainId?.let {
                pokemonRepository.findChainEvolutionById(it).bind()
            }

            pokemonSpecies.flavorText?.let { flavorText ->
                evoChain?.let {
                    CompletePokemon(
                        pokemonDetails.id,
                        pokemonDetails.name,
                        pokemonDetails.weight,
                        pokemonDetails.cries,
                        pokemonDetails.height,
                        pokemonDetails.imageUrl,
                        flavorText,
                        pokemonDetails.pokemonTypes,
                        pokemonDetails.pokemonStat,
                        pokemonDetails.abilities,
                        evoChain
                    )
                }
            } ?: raise(PokemonServiceErrors.TechnicalError)
        }

    override suspend fun getVersions(): Either<PokemonServiceErrors, List<Version>> =
        pokemonRepository.findVersions()

    override suspend fun getItemsByPage(limit: Int, offset: Int): Either<PokemonServiceErrors, List<ItemDetails>> =
        either {
            val items = pokemonRepository.findItems(limit, offset).bind()
            items.map { item ->
                pokemonRepository.findItemDetailsById(item.id).bind()
            }
        }

    override suspend fun getMovesByPage(limit: Int, offset: Int): Either<PokemonServiceErrors, List<MoveDetails>> =
        either {
            val moves = pokemonRepository.findMoves(limit, offset).bind()
            moves.map { move ->
                pokemonRepository.findMoveDetailsById(move.id).bind()
            }
        }

    override suspend fun getTrainerPokedex(trainerId: UUID): Either<PokemonServiceErrors, List<Pokemon>> =
        pokemonRepository.getTrainerPokedex(trainerId).flatMap { pokemonIds ->
            pokemonRepository.findPokemonByIds(pokemonIds)
        }

    override suspend fun addPokemon(pokemonId: Int, trainerId: UUID): Either<PokemonServiceErrors, Pokemon> =
        pokemonRepository.addPokemon(pokemonId, trainerId)
            .flatMap {
                pokemonRepository.findPokemonByIds(listOf(pokemonId))
                    .flatMap { pokemonList ->
                        pokemonList.firstOrNull()?.right()
                            ?: PokemonServiceErrors.TechnicalError.left()
                    }
            }

    override suspend fun deletePokemon(pokemonId: Int, trainerId: UUID): Either<PokemonServiceErrors, Pokemon> =
        pokemonRepository.deletePokemon(pokemonId, trainerId)
            .flatMap {
                pokemonRepository.findPokemonByIds(listOf(pokemonId))
                    .flatMap { listPokemon ->
                        listPokemon.firstOrNull()?.right()
                            ?: PokemonServiceErrors.TechnicalError.left()
                    }
            }

    override suspend fun deleteAllPokemons(trainerId: UUID): Either<PokemonServiceErrors, Unit> =
        pokemonRepository.deleteAllPokemons(trainerId)

    sealed interface PokemonServiceErrors {
        data object TechnicalError : PokemonServiceErrors
    }
}