package fr.maif.codelab.spring.backkotlincoroutinepokedex.domain.repositories

import arrow.core.Either
import fr.maif.codelab.spring.backkotlincoroutinepokedex.domain.models.*
import fr.maif.codelab.spring.backkotlincoroutinepokedex.domain.services.PokemonServiceImpl
import reactor.core.publisher.Mono
import java.util.UUID

interface PokemonRepository {
    suspend fun findPokemonsByPage(limit: Int, offset: Int): Either<PokemonServiceImpl.PokemonServiceErrors, PokedexPage>

    suspend fun searchPokemonsByName(limit: Int, offset: Int, query: String): Either<PokemonServiceImpl.PokemonServiceErrors, PokedexPage>

    suspend fun findPokemonById(idPokemon: Int): Either<PokemonServiceImpl.PokemonServiceErrors, PokemonDetails>

    suspend fun findPokemonByIds(pokemonIds: List<Int>): Either<PokemonServiceImpl.PokemonServiceErrors, List<Pokemon>>

    suspend fun findVersions(): Either<PokemonServiceImpl.PokemonServiceErrors, List<Version>>

    suspend fun findItems(limit: Int, offset: Int): Either<PokemonServiceImpl.PokemonServiceErrors, List<Item>>

    suspend fun findItemDetailsById(idItem: Int): Either<PokemonServiceImpl.PokemonServiceErrors, ItemDetails>

    suspend fun findMoves(limit: Int, offset: Int): Either<PokemonServiceImpl.PokemonServiceErrors, List<Move>>

    suspend fun findMoveDetailsById(idMove: Int): Either<PokemonServiceImpl.PokemonServiceErrors, MoveDetails>

    suspend fun findSpeciesById(id: Int): Either<PokemonServiceImpl.PokemonServiceErrors, Pokemon>

    suspend fun findChainEvolutionById(id: Int): Either<PokemonServiceImpl.PokemonServiceErrors, EvolutionChain>

    suspend fun getTrainerPokedex(trainerId: UUID): Either<PokemonServiceImpl.PokemonServiceErrors, List<Int>>

    suspend fun addPokemon(pokemonId: Int, trainerId: UUID): Either<PokemonServiceImpl.PokemonServiceErrors, Int>

    suspend fun deletePokemon(pokemonId: Int, trainerId: UUID): Either<PokemonServiceImpl.PokemonServiceErrors, Int>

    suspend fun deleteAllPokemons(trainerId: UUID): Either<PokemonServiceImpl.PokemonServiceErrors, Unit>
}