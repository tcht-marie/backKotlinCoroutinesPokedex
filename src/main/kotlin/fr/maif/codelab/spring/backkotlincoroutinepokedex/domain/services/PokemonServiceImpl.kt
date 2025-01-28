package fr.maif.codelab.spring.backkotlincoroutinepokedex.domain.services

import arrow.core.*
import arrow.core.raise.either
import fr.maif.codelab.spring.backkotlincoroutinepokedex.domain.models.*
import fr.maif.codelab.spring.backkotlincoroutinepokedex.domain.repositories.PokemonRepository
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.util.*

@Service
class PokemonServiceImpl(private val pokemonRepository: PokemonRepository) : PokemonService {

    override suspend fun getPokemonsByPage(limit: Int, offset: Int): Either<PokemonServiceErrors, List<Pokemon>> {
        return pokemonRepository.findPokemonsByPage(limit, offset).map { it.pokemonList }
        /*return pokemonRepository.findPokemonsByPage(limit, offset).flatMap { page ->
            Either.Right(page.pokemonList).mapLeft {
                PokemonServiceErrors.TechnicalError
            }
        }*/
    }

    override suspend fun getPokedexByName(
        limit: Int,
        offset: Int,
        query: String
    ): Either<PokemonServiceErrors, List<Pokemon>> {
        return pokemonRepository.searchPokemonsByName(limit, offset, query).map { it.pokemonList }
    }

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
    /*{
        // appel du repo pour trouver la liste d'items
        return pokemonRepository.findItems(limit, offset).flatMap { itemList ->
            // pour chaque éléments de la liste
            val itemDetailsList = itemList.map { item ->
                // récupération des détails
                pokemonRepository.findItemDetailsById(item.id)
            }
            val errors = itemDetailsList.filterIsInstance<Either.Left<PokemonServiceErrors>>()
            if (errors.isNotEmpty()) {
                errors.first().value.left()
            } else {
                itemDetailsList.filterIsInstance<Either.Right<ItemDetails>>()
                    .map { it.value }.right()
            }}}*/

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
                        pokemonList.firstOrNull()?.right() ?: PokemonServiceErrors.TechnicalError.left()
                    }
            }

    override suspend fun deletePokemon(pokemonId: Int, trainerId: UUID): Either<PokemonServiceErrors, Pokemon> =
        pokemonRepository.deletePokemon(pokemonId, trainerId)
            .flatMap {
                pokemonRepository.findPokemonByIds(listOf(pokemonId))
                    .flatMap { listPokemon ->
                        listPokemon.firstOrNull()?.right() ?: PokemonServiceErrors.TechnicalError.left()
                    }
            }

    override suspend fun deleteAllPokemons(trainerId: UUID): Either<PokemonServiceErrors, Unit> =
        pokemonRepository.deleteAllPokemons(trainerId)

    sealed interface PokemonServiceErrors {
        data object TechnicalError : PokemonServiceErrors
    }
}