package fr.maif.codelab.spring.backkotlincoroutinepokedex.infrastructure.repositories

import arrow.core.Either
import arrow.core.left
import arrow.core.raise.result
import arrow.core.right
import fr.maif.codelab.spring.backkotlincoroutinepokedex.domain.models.*
import fr.maif.codelab.spring.backkotlincoroutinepokedex.domain.repositories.PokemonRepository
import fr.maif.codelab.spring.backkotlincoroutinepokedex.domain.services.PokemonServiceImpl
import fr.maif.codelab.spring.backkotlincoroutinepokedex.infrastructure.models.*
import fr.maif.codelab.spring.backkotlincoroutinepokedex.infrastructure.repositories.mapper.PokemonMapper
import kotlinx.coroutines.reactive.awaitSingle
import org.springframework.core.ParameterizedTypeReference
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.r2dbc.core.DatabaseClient
import org.springframework.r2dbc.core.awaitSingleOrNull
import org.springframework.stereotype.Repository
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.awaitBody
import org.springframework.web.reactive.function.client.awaitExchange
import reactor.core.publisher.Mono
import java.util.*

@Repository
class PokemonRepositoryImpl(
    private val webClient: WebClient,
    private val databaseClient: DatabaseClient,
    private val pokemonMapper: PokemonMapper,
) : PokemonRepository {

    private val PATH_POKEDEX: String = "/pokedex/1"
    private val PATH_POKEMONDETAILS: String = "/pokemon/{idPokemon}"
    private val PATH_VERSIONS: String = "/version?limit=60"
    private val PATH_ITEMS: String = "/item?limit={limit}&offset={offset}"
    private val PATH_ITEMDETAILS: String = "/item/{idItem}"
    private val PATH_EVOLUTION: String = "/evolution-chain/{idEvo}"
    private val PATH_SPECIES: String = "/pokemon-species/{idPokemon}"
    private val PATH_MOVES: String = "/move?limit={limit}&offset={offset}"
    private val PATH_MOVEDETAILS: String = "/move/{idMove}"

    private suspend fun getPokedex(): Either<PokemonServiceImpl.PokemonServiceErrors, PokedexInfra> =
        try {
            val response = webClient.get().uri(PATH_POKEDEX).awaitExchange { clientResponse ->
                if (clientResponse.statusCode() == HttpStatus.OK) {
                    val body = clientResponse.awaitBody<PokedexInfra>()
                    Either.Right(body)
                } else {
                    Either.Left(PokemonServiceImpl.PokemonServiceErrors.TechnicalError)
                }
            }
            response
        } catch (e: Exception) {
            Either.Left(PokemonServiceImpl.PokemonServiceErrors.TechnicalError)
        }

    override suspend fun findPokemonsByPage(
        limit: Int,
        offset: Int
    ): Either<PokemonServiceImpl.PokemonServiceErrors, PokedexPage> {
        return getPokedex().fold(
            { Either.Left(it) },
            { pokedexInfra ->
                Either.Right(
                    pokemonMapper.mapPokedexInfraToPokedexPage(pokedexInfra, limit, offset)
                )
            }
        )
    }

    override suspend fun searchPokemonsByName(
        limit: Int, offset: Int, query: String
    ): Either<PokemonServiceImpl.PokemonServiceErrors, PokedexPage> {
        return getPokedex().fold(
            { Either.Left(it) },
            { pokedexInfra ->
                Either.Right(
                    pokemonMapper.mapPokedexInfraToPokedexByName(
                        pokedexInfra, query, limit, offset
                    )
                )
            }
        )
    }

    override suspend fun findPokemonByIds(pokemonIds: List<Int>): Either<PokemonServiceImpl.PokemonServiceErrors, List<Pokemon>> =
        getPokedex().fold(
            { Either.Left(it) },
            { pokedex ->
                Either.Right(
                    pokemonMapper.mapPokemonsIdsToPokemon(pokemonIds, pokedex)
                )
            }
        )

    override suspend fun findPokemonById(idPokemon: Int): Either<PokemonServiceImpl.PokemonServiceErrors, PokemonDetails> {
        return try {
            val response = webClient.get().uri(PATH_POKEMONDETAILS, idPokemon).awaitExchange { clientResponse ->
                if (clientResponse.statusCode() == HttpStatus.OK) {
                    val body = clientResponse.awaitBody<PokemonInfra>()
                    val pokemonDetails = pokemonMapper.mapPokemonInfraToPokemonDetails(body)
                    Either.Right(pokemonDetails)
                } else {
                    Either.Left(PokemonServiceImpl.PokemonServiceErrors.TechnicalError)
                }
            }
            response
        } catch (e: Exception) {
            Either.Left(PokemonServiceImpl.PokemonServiceErrors.TechnicalError)
        }
    }

    override suspend fun findVersions(): Either<PokemonServiceImpl.PokemonServiceErrors, List<Version>> {
        return try {
            val response = webClient.get().uri(PATH_VERSIONS).awaitExchange { clientResponse ->
                if (clientResponse.statusCode() == HttpStatus.OK) {
                    val body = clientResponse.awaitBody<PageGenericInfra<VersionInfra>>()
                    val versions = pokemonMapper.mapVersionInfraToVersion(body)
                    Either.Right(versions)
                } else {
                    Either.Left(PokemonServiceImpl.PokemonServiceErrors.TechnicalError)
                }
            }
            response
        } catch (e: Exception) {
            Either.Left(PokemonServiceImpl.PokemonServiceErrors.TechnicalError)
        }
    }

    override suspend fun findItems(
        limit: Int,
        offset: Int
    ): Either<PokemonServiceImpl.PokemonServiceErrors, List<Item>> {
        return try {
            val response = webClient.get().uri(PATH_ITEMS, limit, offset).awaitExchange { clientResponse ->
                if (clientResponse.statusCode() == HttpStatus.OK) {
                    val body = clientResponse.awaitBody<PageGenericInfra<ItemInfra>>()
                    val items = pokemonMapper.mapItemInfraToItem(body)
                    Either.Right(items)
                } else {
                    Either.Left(PokemonServiceImpl.PokemonServiceErrors.TechnicalError)
                }
            }
            response
        } catch (e: Exception) {
            Either.Left(PokemonServiceImpl.PokemonServiceErrors.TechnicalError)
        }
    }

    override suspend fun findItemDetailsById(idItem: Int): Either<PokemonServiceImpl.PokemonServiceErrors, ItemDetails> {
        return try {
            val response = webClient.get().uri(PATH_ITEMDETAILS, idItem).awaitExchange { clientResponse ->
                if (clientResponse.statusCode() == HttpStatus.OK) {
                    val body = clientResponse.awaitBody<ItemDetailsInfra>()
                    val itemDetails = pokemonMapper.mapItemDetailsInfraToItemDetails(body)
                    Either.Right(itemDetails)
                } else {
                    Either.Left(PokemonServiceImpl.PokemonServiceErrors.TechnicalError)
                }
            }
            response
        } catch (e: Exception) {
            Either.Left(PokemonServiceImpl.PokemonServiceErrors.TechnicalError)
        }
    }

    override suspend fun findMoves(
        limit: Int,
        offset: Int
    ): Either<PokemonServiceImpl.PokemonServiceErrors, List<Move>> {
        return try {
            val response = webClient.get().uri(PATH_MOVES, limit, offset).awaitExchange { clientResponse ->
                if (clientResponse.statusCode() == HttpStatus.OK) {
                    val body = clientResponse.awaitBody<PageGenericInfra<MoveInfra>>()
                    val moves = pokemonMapper.mapMoveInfraToMove(body)
                    Either.Right(moves)
                } else {
                    Either.Left(PokemonServiceImpl.PokemonServiceErrors.TechnicalError)
                }
            }
            response
        } catch (e: Exception) {
            Either.Left(PokemonServiceImpl.PokemonServiceErrors.TechnicalError)
        }
    }

    override suspend fun findMoveDetailsById(idMove: Int): Either<PokemonServiceImpl.PokemonServiceErrors, MoveDetails> {
        return try {
            val response = webClient.get().uri(PATH_MOVEDETAILS, idMove).awaitExchange { clientResponse ->
                if (clientResponse.statusCode() == HttpStatus.OK) {
                    val body = clientResponse.awaitBody<MoveDetailsInfra>()
                    val moveDetails = pokemonMapper.mapMoveDetailsInfraToMoveDetails(body)
                    Either.Right(moveDetails)
                } else {
                    Either.Left(PokemonServiceImpl.PokemonServiceErrors.TechnicalError)
                }
            }
            response
        } catch (e: Exception) {
            Either.Left(PokemonServiceImpl.PokemonServiceErrors.TechnicalError)
        }
    }

    override suspend fun findSpeciesById(id: Int): Either<PokemonServiceImpl.PokemonServiceErrors, Pokemon> {
        return try {
            val response = webClient.get().uri(PATH_SPECIES, id).awaitExchange { clientResponse ->
                if (clientResponse.statusCode() == HttpStatus.OK) {
                    val body = clientResponse.awaitBody<PokemonSpeciesInfra>()
                    val species = pokemonMapper.mapSpeciesInfraToPokemon(body)
                    Either.Right(species)
                } else {
                    Either.Left(PokemonServiceImpl.PokemonServiceErrors.TechnicalError)
                }
            }
            response
        } catch (e: Exception) {
            Either.Left(PokemonServiceImpl.PokemonServiceErrors.TechnicalError)
        }
    }

    override suspend fun findChainEvolutionById(id: Int): Either<PokemonServiceImpl.PokemonServiceErrors, EvolutionChain> {
        return try {
            val response = webClient.get().uri(PATH_EVOLUTION, id).awaitExchange { clientResponse ->
                if (clientResponse.statusCode() == HttpStatus.OK) {
                    val body = clientResponse.awaitBody<EvolutionChainDetailsInfra>()
                    val evoChain = pokemonMapper.mapEvoDetailsToChainEvolution(body)
                    Either.Right(evoChain)
                } else {
                    Either.Left(PokemonServiceImpl.PokemonServiceErrors.TechnicalError)
                }
            }
            response
        } catch (e: Exception) {
            Either.Left(PokemonServiceImpl.PokemonServiceErrors.TechnicalError)
        }
    }

    override suspend fun getTrainerPokedex(trainerId: UUID): Either<PokemonServiceImpl.PokemonServiceErrors, List<Int>> {
        return try {
            val pokedex = databaseClient.sql("SELECT pokemon_id FROM pokedex WHERE trainers_id = :trainerId")
                .bind("trainerId", trainerId.toString())
                .map { row -> row.get(0, Int::class.javaObjectType) }
                .all()
                .collectList()
                .awaitSingle()
                .filterNotNull()
                .right()
            pokedex
        } catch (e: Exception) {
            Either.Left(PokemonServiceImpl.PokemonServiceErrors.TechnicalError)
        }
    }

    override suspend fun addPokemon(
        pokemonId: Int,
        trainerId: UUID
    ): Either<PokemonServiceImpl.PokemonServiceErrors, Int> =
        try {
            val pokemon = databaseClient.sql("INSERT INTO pokedex(pokemon_id, trainers_id) VALUES(:pokemonId, :trainerId)")
                .bind("pokemonId", pokemonId)
                .bind("trainerId", trainerId.toString())
                .fetch()
                .rowsUpdated()
                .map { it.toInt() }
                .awaitSingle()
                .right()
        } catch (e: Exception) {
            Either.Left(PokemonServiceImpl.PokemonServiceErrors.TechnicalError)
        }

    override fun deletePokemon(
        pokemonId: Int,
        trainerId: UUID
    ): Mono<Either<PokemonServiceImpl.PokemonServiceErrors, Int>> =
        databaseClient.sql("DELETE FROM pokedex WHERE pokemon_id = :pokemonId AND trainers_id = :trainerId")
            .bind("pokemonId", pokemonId)
            .bind("trainerId", trainerId.toString())
            .fetch()
            .rowsUpdated()
            .map { it.toInt() }
            .map {
                it?.right() ?: PokemonServiceImpl.PokemonServiceErrors.TechnicalError.left()
            }

    override fun deleteAllPokemons(trainerId: UUID): Mono<Either<PokemonServiceImpl.PokemonServiceErrors, Unit>> =
        databaseClient.sql("DELETE FROM pokedex WHERE trainers_id = :trainerId")
            .bind("trainerId", trainerId.toString())
            .fetch()
            .rowsUpdated()
            .map { }
            .map { it.right() }
}