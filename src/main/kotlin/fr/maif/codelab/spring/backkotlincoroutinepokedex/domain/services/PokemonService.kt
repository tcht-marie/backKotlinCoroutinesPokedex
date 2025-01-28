package fr.maif.codelab.spring.backkotlincoroutinepokedex.domain.services

import arrow.core.Either
import fr.maif.codelab.spring.backkotlincoroutinepokedex.domain.models.*
import fr.maif.codelab.spring.backkotlincoroutinepokedex.domain.services.PokemonServiceImpl.PokemonServiceErrors
import reactor.core.publisher.Mono
import java.util.*

interface PokemonService {
    suspend fun getPokemonsByPage(limit: Int, offset: Int): Either<PokemonServiceErrors, List<Pokemon>>

    suspend fun getPokedexByName(limit: Int, offset: Int, query: String): Either<PokemonServiceErrors, List<Pokemon>>

    suspend fun getPokemonById(id: Int): Either<PokemonServiceErrors, CompletePokemon>

    suspend fun getVersions(): Either<PokemonServiceErrors, List<Version>>

    suspend fun getItemsByPage(limit: Int, offset: Int): Either<PokemonServiceErrors, List<ItemDetails>>

    suspend fun getMovesByPage(limit: Int, offset: Int): Either<PokemonServiceErrors, List<MoveDetails>>

    suspend fun getTrainerPokedex(trainerId: UUID): Either<PokemonServiceErrors, List<Pokemon>>

    suspend fun addPokemon(pokemonId: Int, trainerId: UUID): Either<PokemonServiceErrors, Pokemon>

    suspend fun deletePokemon(pokemonId: Int, trainerId: UUID): Either<PokemonServiceErrors, Pokemon>

    suspend fun deleteAllPokemons(trainerId: UUID): Either<PokemonServiceErrors, Unit>
}