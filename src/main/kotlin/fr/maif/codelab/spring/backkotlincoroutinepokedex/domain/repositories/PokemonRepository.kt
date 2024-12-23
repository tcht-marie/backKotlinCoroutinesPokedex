package fr.maif.codelab.spring.backkotlincoroutinepokedex.domain.repositories

import arrow.core.Either
import fr.maif.codelab.spring.backkotlincoroutinepokedex.domain.models.*
import fr.maif.codelab.spring.backkotlincoroutinepokedex.domain.services.PokemonServiceImpl
import reactor.core.publisher.Mono
import java.util.UUID

interface PokemonRepository {
    fun findPokemonsByPage(limit: Int, offset: Int): Mono<Either<PokemonServiceImpl.PokemonServiceErrors, PokedexPage>>

    fun searchPokemonsByName(limit: Int, offset: Int, query: String): Mono<Either<PokemonServiceImpl.PokemonServiceErrors, PokedexPage>>

    fun findPokemonById(idPokemon: Int): Mono<Either<PokemonServiceImpl.PokemonServiceErrors, PokemonDetails>>

    fun findPokemonByIds(pokemonIds: List<Int>): Mono<Either<PokemonServiceImpl.PokemonServiceErrors, List<Pokemon>>>

    fun findVersions(): Mono<Either<PokemonServiceImpl.PokemonServiceErrors, List<Version>>>

    fun findItems(limit: Int, offset: Int): Mono<Either<PokemonServiceImpl.PokemonServiceErrors, List<Item>>>

    fun findItemDetailsById(idItem: Int): Mono<Either<PokemonServiceImpl.PokemonServiceErrors, ItemDetails>>

    fun findMoves(limit: Int, offset: Int): Mono<Either<PokemonServiceImpl.PokemonServiceErrors, List<Move>>>

    fun findMoveDetailsById(idMove: Int): Mono<Either<PokemonServiceImpl.PokemonServiceErrors, MoveDetails>>

    fun findSpeciesById(id: Int): Mono<Either<PokemonServiceImpl.PokemonServiceErrors, Pokemon>>

    fun findChainEvolutionById(id: Int): Mono<Either<PokemonServiceImpl.PokemonServiceErrors, EvolutionChain>>

    fun getTrainerPokedex(trainerId: UUID): Mono<Either<PokemonServiceImpl.PokemonServiceErrors, List<Int>>>

    fun addPokemon(pokemonId: Int, trainerId: UUID): Mono<Either<PokemonServiceImpl.PokemonServiceErrors, Int>>

    fun deletePokemon(pokemonId: Int, trainerId: UUID): Mono<Either<PokemonServiceImpl.PokemonServiceErrors, Int>>

    fun deleteAllPokemons(trainerId: UUID): Mono<Either<PokemonServiceImpl.PokemonServiceErrors, Unit>>
}