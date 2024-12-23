package fr.maif.codelab.spring.backkotlincoroutinepokedex.domain.services

import arrow.core.Either
import arrow.core.flatMap
import arrow.core.left
import arrow.core.right
import fr.maif.codelab.spring.backkotlincoroutinepokedex.domain.models.*
import fr.maif.codelab.spring.backkotlincoroutinepokedex.domain.repositories.PokemonRepository
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.util.*

@Service
class PokemonServiceImpl(

    private val pokemonRepository: PokemonRepository

) : PokemonService {
    override fun getPokemonsByPage(limit: Int, offset: Int): Mono<Either<PokemonServiceErrors, List<Pokemon>>> {
        val pokedexPage: Mono<Either<PokemonServiceErrors, PokedexPage>> =
            pokemonRepository.findPokemonsByPage(limit, offset)
        return pokedexPage.map { either ->
            either.map { page -> page.pokemonList }
        }
    }

    override fun getPokedexByName(
        limit: Int,
        offset: Int,
        query: String
    ): Mono<Either<PokemonServiceErrors, List<Pokemon>>> {
        val pokedexPageSearch: Mono<Either<PokemonServiceErrors, PokedexPage>> =
            pokemonRepository.searchPokemonsByName(limit, offset, query)
        return pokedexPageSearch.map { either ->
            either.map { page -> page.pokemonList }
        }
    }

    override fun getPokemonById(id: Int): Mono<Either<PokemonServiceErrors, CompletePokemon>> {
        val pokemonDetailsMono = pokemonRepository.findPokemonById(id)
        val speciesMono = pokemonRepository.findSpeciesById(id)

        return pokemonDetailsMono.zipWith(speciesMono).flatMap { tuple ->
            val pokemonDetailsEither = tuple.t1
            val speciesEither = tuple.t2
            val pokemonDetails = pokemonDetailsEither.getOrNull()
            val species = speciesEither.getOrNull()

            if (pokemonDetails == null || species == null) {
                return@flatMap Mono.just(PokemonServiceErrors.TechnicalError.left())
            }
            val flavorText = species.flavorText
            val evoChainId = species.evolutionChainId

            if (flavorText == null || evoChainId == null) {
                return@flatMap Mono.just(PokemonServiceErrors.TechnicalError.left())
            }

            pokemonRepository.findChainEvolutionById(evoChainId).map { evolutionChainEither ->
                evolutionChainEither.fold(
                    { error -> error.left() },
                    { evolutionChain ->
                        CompletePokemon(
                            id = pokemonDetails.id,
                            name = pokemonDetails.name,
                            weight = pokemonDetails.weight,
                            cries = pokemonDetails.cries,
                            height = pokemonDetails.height,
                            imageUrl = pokemonDetails.imageUrl,
                            flavorText = flavorText,
                            pokemonTypes = pokemonDetails.pokemonTypes,
                            pokemonStat = pokemonDetails.pokemonStat,
                            abilities = pokemonDetails.abilities,
                            evolutionChain = evolutionChain
                        ).right()
                    }
                )
            }
        }
    }

    /*
    override fun findPokemonById(id: Int): Mono<Either<PokemonServiceErrors, CompletePokemon>> {
        val pokemonDetailsMono = pokemonRepository.findPokemonById(id)
        val speciesMono = pokemonRepository.findSpeciesById(id)

        return pokemonDetailsMono.zipWith(speciesMono).flatMap { tuple ->
            either {
                val pokemonDetails = tuple.t1.bind()
                val species = tuple.t2.bind()
                species.flavorText?.let {
                    species.evolutionChainId?.let {
                        pokemonRepository.findEvolutionChainById(it).map { evolutionChainEither ->
                            CompletePokemon(
                                id = pokemonDetails.id,
                                name = pokemonDetails.name,
                                flavorText = species.flavorText,
                                height = pokemonDetails.height,
                                cries = pokemonDetails.cries,
                                weight = pokemonDetails.weight,
                                imageUrl = pokemonDetails.imageUrl,
                                pokemonTypes = pokemonDetails.pokemonTypes,
                                pokemonStats = pokemonDetails.pokemonStats,
                                abilities = pokemonDetails.abilities,
                                evolutionChain = evolutionChainEither.bind(),
                            ).right()
                        }
                    }
                } ?: raise(PokemonServiceErrors.TechnicalError)
            }.getOrNull() ?: Mono.just(PokemonServiceErrors.TechnicalError.left())
        }
    }*/

    override fun getVersions(): Mono<Either<PokemonServiceErrors, List<Version>>> =
        pokemonRepository.findVersions()

    override fun getItemsByPage(limit: Int, offset: Int): Mono<Either<PokemonServiceErrors, List<ItemDetails>>> {
        return pokemonRepository.findItems(limit, offset).flatMap { either ->
            either.fold(
                { error -> Mono.just(error.left()) },
                { itemList ->
                    val itemDetailsMono = itemList.map { item ->
                        pokemonRepository.findItemDetailsById(item.id)
                    }
                    Flux.mergeSequential(itemDetailsMono).collectList()
                        .map { detailsList ->
                            val errors = detailsList.filterIsInstance<Either.Left<PokemonServiceErrors>>()
                            if (errors.isNotEmpty()) {
                                errors.first().value.left()
                            } else {
                                detailsList.filterIsInstance<Either.Right<ItemDetails>>()
                                    .map { it.value }
                                    .right()
                            }
                        }
                }
            )
        }
    }

    /*override fun findItemsByPage(
        limit: Int,
        offset: Int,
    ): Mono<Either<PokemonServiceErrors, List<ItemDetails>>> =
        pokemonRepository.findItems(limit, offset).flatMap { either1 ->
            when (either1) {
                is Either.Left -> either1.toMono()
                is Either.Right -> either1.value
                    .map { pokemonRepository.findItemDetailsById(it.id) }
                    .let { Flux.mergeSequential(it).collectList().map { either { it.bindAll() } } }
            }
        }*/

    override fun getMovesByPage(limit: Int, offset: Int): Mono<Either<PokemonServiceErrors, List<MoveDetails>>> =
        pokemonRepository.findMoves(limit, offset).flatMap { either ->
            either.fold(
                { error -> Mono.just(error.left()) },
                { moveList ->
                    val moveDetailsMono = moveList.map { move ->
                        pokemonRepository.findMoveDetailsById(move.id)
                    }
                    Flux.mergeSequential(moveDetailsMono).collectList()
                        .map { detailsList ->
                            val errors = detailsList.filterIsInstance<Either.Left<PokemonServiceErrors>>()
                            if (errors.isNotEmpty()) {
                                errors.first().value.left()
                            } else {
                                detailsList.filterIsInstance<Either.Right<MoveDetails>>()
                                    .map { it.value }
                                    .right()
                            }
                        }
                }
            )
        }

    override fun getTrainerPokedex(trainerId: UUID): Mono<Either<PokemonServiceErrors, List<Pokemon>>> =
        pokemonRepository.getTrainerPokedex(trainerId).flatMap { either ->
            either.fold(
                { error -> Mono.just(error.left()) },
                { pokemonRepository.findPokemonByIds(it) }
            )
        }

    override fun addPokemon(pokemonId: Int, trainerId: UUID): Mono<Either<PokemonServiceErrors, Pokemon>> {
        val arrayList: List<Int> = listOf(pokemonId)
        return pokemonRepository.addPokemon(pokemonId, trainerId)
            .flatMap { pokemonRepository.findPokemonByIds(arrayList) }
            .map { either ->
                either.flatMap { list ->
                        list.firstOrNull()?.right() ?: PokemonServiceErrors.TechnicalError.left()
                    }
            }
    }

    /*override fun addPokemonToPokedex(
        pokemonId: Int,
        trainerId: UUID,
    ): Mono<Either<PokemonServiceErrors, Pokemon>> =
        pokemonRepository.addPokemonToPokedex(pokemonId, trainerId).flatMap { either1 ->
            when (either1) {
                is Either.Left -> either1.toMono()
                is Either.Right -> pokemonRepository.findPokemonByIds(listOf(either1.value)).map { either2 ->
                    when (either2) {
                        is Either.Left -> either2
                        is Either.Right -> either2.value.firstOrNull()?.right()
                            ?: PokemonServiceErrors.TechnicalError.left()
                    }
                }
            }
        }*/

    override fun deletePokemon(pokemonId: Int, trainerId: UUID): Mono<Either<PokemonServiceErrors, Pokemon>> {
        val arrayList: List<Int> = listOf(pokemonId)
        return pokemonRepository.deletePokemon(pokemonId, trainerId)
            .flatMap { pokemonRepository.findPokemonByIds(arrayList) }
            .map { either ->
                either.flatMap { list ->
                    list.firstOrNull()?.right() ?: PokemonServiceErrors.TechnicalError.left()
                }
            }

    }

    override fun deleteAllPokemons(trainerId: UUID): Mono<Either<PokemonServiceErrors, Unit>> =
        pokemonRepository.deleteAllPokemons(trainerId)

    sealed interface PokemonServiceErrors {
        data object TechnicalError : PokemonServiceErrors
    }
}