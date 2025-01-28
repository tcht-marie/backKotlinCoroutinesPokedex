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
import kotlinx.coroutines.reactor.awaitSingleOrNull
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

    private suspend fun getPokedex(): Either<PokemonServiceImpl.PokemonServiceErrors, PokedexInfra> =
        webClient.get().uri(PATH_POKEDEX).awaitExchange { clientResponse ->
            if (clientResponse.statusCode() == HttpStatus.OK) {
                val body = clientResponse.awaitBody<PokedexInfra>()
                Either.Right(body)
            } else {
                Either.Left(PokemonServiceImpl.PokemonServiceErrors.TechnicalError)
            }
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

    override suspend fun findPokemonById(idPokemon: Int): Either<PokemonServiceImpl.PokemonServiceErrors, PokemonDetails> =
        webClient.get().uri(PATH_POKEMONDETAILS, idPokemon).awaitExchange { clientResponse ->
            if (clientResponse.statusCode() == HttpStatus.OK) {
                val body = clientResponse.awaitBody<PokemonInfra>()
                val pokemonDetails = pokemonMapper.mapPokemonInfraToPokemonDetails(body)
                Either.Right(pokemonDetails)
            } else {
                Either.Left(PokemonServiceImpl.PokemonServiceErrors.TechnicalError)
            }
        }

    override suspend fun findVersions(): Either<PokemonServiceImpl.PokemonServiceErrors, List<Version>> =
        webClient.get().uri(PATH_VERSIONS).awaitExchange { clientResponse ->
            if (clientResponse.statusCode() == HttpStatus.OK) {
                val body = clientResponse.awaitBody<PageGenericInfra<VersionInfra>>()
                val versions = pokemonMapper.mapVersionInfraToVersion(body)
                Either.Right(versions)
            } else {
                Either.Left(PokemonServiceImpl.PokemonServiceErrors.TechnicalError)
            }
        }

    override suspend fun findItems(
        limit: Int,
        offset: Int
    ): Either<PokemonServiceImpl.PokemonServiceErrors, List<Item>> =
        webClient.get().uri(PATH_ITEMS, limit, offset).awaitExchange { clientResponse ->
            if (clientResponse.statusCode() == HttpStatus.OK) {
                val body = clientResponse.awaitBody<PageGenericInfra<ItemInfra>>()
                val items = pokemonMapper.mapItemInfraToItem(body)
                Either.Right(items)
            } else {
                Either.Left(PokemonServiceImpl.PokemonServiceErrors.TechnicalError)
            }
        }

    override suspend fun findItemDetailsById(idItem: Int): Either<PokemonServiceImpl.PokemonServiceErrors, ItemDetails> =
        webClient.get().uri(PATH_ITEMDETAILS, idItem).awaitExchange { clientResponse ->
            if (clientResponse.statusCode() == HttpStatus.OK) {
                val body = clientResponse.awaitBody<ItemDetailsInfra>()
                val itemDetails = pokemonMapper.mapItemDetailsInfraToItemDetails(body)
                Either.Right(itemDetails)
            } else {
                Either.Left(PokemonServiceImpl.PokemonServiceErrors.TechnicalError)
            }
        }

    override suspend fun findMoves(
        limit: Int,
        offset: Int
    ): Either<PokemonServiceImpl.PokemonServiceErrors, List<Move>> =
        webClient.get().uri(PATH_MOVES, limit, offset).awaitExchange { clientResponse ->
            if (clientResponse.statusCode() == HttpStatus.OK) {
                val body = clientResponse.awaitBody<PageGenericInfra<MoveInfra>>()
                val moves = pokemonMapper.mapMoveInfraToMove(body)
                Either.Right(moves)
            } else {
                Either.Left(PokemonServiceImpl.PokemonServiceErrors.TechnicalError)
            }
        }

    override suspend fun findMoveDetailsById(idMove: Int): Either<PokemonServiceImpl.PokemonServiceErrors, MoveDetails> =
        webClient.get().uri(PATH_MOVEDETAILS, idMove).awaitExchange { clientResponse ->
            if (clientResponse.statusCode() == HttpStatus.OK) {
                val body = clientResponse.awaitBody<MoveDetailsInfra>()
                val moveDetails = pokemonMapper.mapMoveDetailsInfraToMoveDetails(body)
                Either.Right(moveDetails)
            } else {
                Either.Left(PokemonServiceImpl.PokemonServiceErrors.TechnicalError)
            }
        }

    override suspend fun findSpeciesById(id: Int): Either<PokemonServiceImpl.PokemonServiceErrors, Pokemon> =
        webClient.get().uri(PATH_SPECIES, id).awaitExchange { clientResponse ->
            if (clientResponse.statusCode() == HttpStatus.OK) {
                val body = clientResponse.awaitBody<PokemonSpeciesInfra>()
                val species = pokemonMapper.mapSpeciesInfraToPokemon(body)
                Either.Right(species)
            } else {
                Either.Left(PokemonServiceImpl.PokemonServiceErrors.TechnicalError)
            }
        }

    override suspend fun findChainEvolutionById(id: Int): Either<PokemonServiceImpl.PokemonServiceErrors, EvolutionChain> =
        webClient.get().uri(PATH_EVOLUTION, id).awaitExchange { clientResponse ->
            if (clientResponse.statusCode() == HttpStatus.OK) {
                val body = clientResponse.awaitBody<EvolutionChainDetailsInfra>()
                val evoChain = pokemonMapper.mapEvoDetailsToChainEvolution(body)
                Either.Right(evoChain)
            } else {
                Either.Left(PokemonServiceImpl.PokemonServiceErrors.TechnicalError)
            }
        }

    /* utilisation de try catch =
        si une erreur vient de la base de données, elle ne sera pas gérée sans le try catch et remontera jusqu'au
        controller. Le catch vient donc récupérer n'importe quelles erreurs et la passe dans le either left
    */
    override suspend fun getTrainerPokedex(trainerId: UUID): Either<PokemonServiceImpl.PokemonServiceErrors, List<Int>> =
        try {
            databaseClient.sql("SELECT pokemon_id FROM pokedex WHERE trainers_id = :trainerId")
                .bind("trainerId", trainerId.toString())
                .map { row -> row.get(0, Int::class.javaObjectType) }
                .all()
                .collectList()
                .awaitSingle()
                .filterNotNull()
                .right()
        } catch (e: Exception) {
            Either.Left(PokemonServiceImpl.PokemonServiceErrors.TechnicalError)
        }

    override suspend fun addPokemon(
        pokemonId: Int,
        trainerId: UUID
    ): Either<PokemonServiceImpl.PokemonServiceErrors, Int> =
        try {
            databaseClient.sql("INSERT INTO pokedex(pokemon_id, trainers_id) VALUES(:pokemonId, :trainerId)")
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

    override suspend fun deletePokemon(
        pokemonId: Int,
        trainerId: UUID
    ): Either<PokemonServiceImpl.PokemonServiceErrors, Int> =
        try {
            databaseClient.sql("DELETE FROM pokedex WHERE pokemon_id = :pokemonId AND trainers_id = :trainerId")
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

    override suspend fun deleteAllPokemons(trainerId: UUID): Either<PokemonServiceImpl.PokemonServiceErrors, Unit> =
        try {
            databaseClient.sql("DELETE FROM pokedex WHERE trainers_id = :trainerId")
                .bind("trainerId", trainerId.toString())
                .fetch()
                .rowsUpdated()
                .map { }
                .awaitSingle()
                .right()
        } catch (e: Exception) {
            Either.Left(PokemonServiceImpl.PokemonServiceErrors.TechnicalError)
        }

    companion object {
        private const val PATH_POKEDEX: String = "/pokedex/1"
        private const val PATH_POKEMONDETAILS: String = "/pokemon/{idPokemon}"
        private const val PATH_VERSIONS: String = "/version?limit=60"
        private const val PATH_ITEMS: String = "/item?limit={limit}&offset={offset}"
        private const val PATH_ITEMDETAILS: String = "/item/{idItem}"
        private const val PATH_EVOLUTION: String = "/evolution-chain/{idEvo}"
        private const val PATH_SPECIES: String = "/pokemon-species/{idPokemon}"
        private const val PATH_MOVES: String = "/move?limit={limit}&offset={offset}"
        private const val PATH_MOVEDETAILS: String = "/move/{idMove}"
    }
}