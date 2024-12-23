package fr.maif.codelab.spring.backkotlincoroutinepokedex.domain.services

import arrow.core.Either
import fr.maif.codelab.spring.backkotlincoroutinepokedex.domain.models.*
import fr.maif.codelab.spring.backkotlincoroutinepokedex.domain.services.PokemonServiceImpl.PokemonServiceErrors
import reactor.core.publisher.Mono
import java.util.*

interface PokemonService {
    fun getPokemonsByPage(limit: Int, offset: Int): Mono<Either<PokemonServiceErrors, List<Pokemon>>>

    fun getPokedexByName(limit: Int, offset: Int, query: String): Mono<Either<PokemonServiceErrors, List<Pokemon>>>

    fun getPokemonById(id: Int): Mono<Either<PokemonServiceErrors, CompletePokemon>>

    fun getVersions(): Mono<Either<PokemonServiceErrors, List<Version>>>

    fun getItemsByPage(limit: Int, offset: Int): Mono<Either<PokemonServiceErrors, List<ItemDetails>>>

    fun getMovesByPage(limit: Int, offset: Int): Mono<Either<PokemonServiceErrors, List<MoveDetails>>>

    fun getTrainerPokedex(trainerId: UUID): Mono<Either<PokemonServiceErrors, List<Pokemon>>>

    fun addPokemon(pokemonId: Int, trainerId: UUID): Mono<Either<PokemonServiceErrors, Pokemon>>

    fun deletePokemon(pokemonId: Int, trainerId: UUID): Mono<Either<PokemonServiceErrors, Pokemon>>

    fun deleteAllPokemons(trainerId: UUID): Mono<Either<PokemonServiceErrors, Unit>>
}