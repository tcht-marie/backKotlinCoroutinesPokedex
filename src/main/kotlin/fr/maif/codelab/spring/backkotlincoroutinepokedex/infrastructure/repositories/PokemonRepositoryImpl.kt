package fr.maif.codelab.spring.backkotlincoroutinepokedex.infrastructure.repositories

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import fr.maif.codelab.spring.backkotlincoroutinepokedex.domain.models.*
import fr.maif.codelab.spring.backkotlincoroutinepokedex.domain.repositories.PokemonRepository
import fr.maif.codelab.spring.backkotlincoroutinepokedex.domain.services.PokemonServiceImpl
import fr.maif.codelab.spring.backkotlincoroutinepokedex.infrastructure.models.*
import fr.maif.codelab.spring.backkotlincoroutinepokedex.infrastructure.repositories.mapper.PokemonMapper
import org.springframework.core.ParameterizedTypeReference
import org.springframework.http.HttpStatus
import org.springframework.r2dbc.core.DatabaseClient
import org.springframework.stereotype.Repository
import org.springframework.web.reactive.function.client.WebClient
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

    private fun getPokedex(): Mono<Either<PokemonServiceImpl.PokemonServiceErrors, PokedexInfra>> =
        webClient.get().uri(PATH_POKEDEX).exchangeToMono { response ->
            if (response.statusCode() == HttpStatus.OK) {
                response.bodyToMono(PokedexInfra::class.java).map { it.right() }
            } else {
                Mono.just(PokemonServiceImpl.PokemonServiceErrors.TechnicalError.left())
            }
        }

    override fun findPokemonsByPage(
        limit: Int,
        offset: Int
    ): Mono<Either<PokemonServiceImpl.PokemonServiceErrors, PokedexPage>> {
        return getPokedex().flatMap { either ->
            either.fold(
                { Mono.just(it.left()) },
                { pokedexInfra ->
                    Mono.just(
                        pokemonMapper.mapPokedexInfraToPokedexPage(pokedexInfra, limit, offset).right()
                    )
                }
            )
        }
    }


    override fun searchPokemonsByName(
        limit: Int, offset: Int, query: String
    ): Mono<Either<PokemonServiceImpl.PokemonServiceErrors, PokedexPage>> {
        return getPokedex().flatMap { either ->
            either.fold(
                { Mono.just(it.left()) },
                { pokedexInfra ->
                    Mono.just(
                        pokemonMapper.mapPokedexInfraToPokedexByName(
                            pokedexInfra,
                            query,
                            limit,
                            offset
                        ).right()
                    )
                }
            )
        }
    }

    override fun findPokemonByIds(pokemonIds: List<Int>): Mono<Either<PokemonServiceImpl.PokemonServiceErrors, List<Pokemon>>> =
        getPokedex().flatMap { either ->
            either.fold(
                { Mono.just(it.left()) },
                { pokedex ->
                    Mono.just(
                        pokemonMapper.mapPokemonsIdsToPokemon(pokemonIds, pokedex).right()
                    )
                }
            )
        }

    override fun findPokemonById(idPokemon: Int): Mono<Either<PokemonServiceImpl.PokemonServiceErrors, PokemonDetails>> {
        return webClient.get().uri(PATH_POKEMONDETAILS, idPokemon).exchangeToMono { response ->
            if (response.statusCode() == HttpStatus.OK) {
                response.bodyToMono(PokemonInfra::class.java).map {
                    pokemonMapper.mapPokemonInfraToPokemonDetails(it).right()
                }
            } else {
                Mono.just(PokemonServiceImpl.PokemonServiceErrors.TechnicalError.left())
            }
        }
    }

    override fun findVersions(): Mono<Either<PokemonServiceImpl.PokemonServiceErrors, List<Version>>> {
        return webClient.get().uri(PATH_VERSIONS)
            .exchangeToMono { response ->
                if (response.statusCode() == HttpStatus.OK) {
                    response.bodyToMono(object : ParameterizedTypeReference<PageGenericInfra<VersionInfra>>() {})
                        .mapNotNull {
                            pokemonMapper.mapVersionInfraToVersion(it).right()
                        }
                } else {
                    // just = crée un mono à partir de rien (constructor pour mono)
                    // left = crée un either left qui contient l'objet d'erreur
                    Mono.just(PokemonServiceImpl.PokemonServiceErrors.TechnicalError.left())
                }
            }
    }

    override fun findItems(limit: Int, offset: Int): Mono<Either<PokemonServiceImpl.PokemonServiceErrors, List<Item>>> {
        return webClient.get().uri(PATH_ITEMS, limit, offset)
            .exchangeToMono { response ->
                if (response.statusCode() == HttpStatus.OK) {
                    response.bodyToMono(object : ParameterizedTypeReference<PageGenericInfra<ItemInfra>>() {})
                        .mapNotNull { pokemonMapper.mapItemInfraToItem(it).right() }
                } else {
                    Mono.just(PokemonServiceImpl.PokemonServiceErrors.TechnicalError.left())
                }
            }
    }

    override fun findItemDetailsById(idItem: Int): Mono<Either<PokemonServiceImpl.PokemonServiceErrors, ItemDetails>> {
        return webClient.get().uri(PATH_ITEMDETAILS, idItem)
            .exchangeToMono { response ->
                if (response.statusCode() == HttpStatus.OK) {
                    response.bodyToMono(ItemDetailsInfra::class.java).mapNotNull {
                        pokemonMapper.mapItemDetailsInfraToItemDetails(it).right()
                    }
                } else {
                    Mono.just(PokemonServiceImpl.PokemonServiceErrors.TechnicalError.left())
                }
            }
    }

    override fun findMoves(limit: Int, offset: Int): Mono<Either<PokemonServiceImpl.PokemonServiceErrors, List<Move>>> {
        return webClient.get().uri(PATH_MOVES, limit, offset)
            .exchangeToMono { response ->
                if (response.statusCode() == HttpStatus.OK) {
                    response.bodyToMono(object : ParameterizedTypeReference<PageGenericInfra<MoveInfra>>() {})
                        .mapNotNull { pokemonMapper.mapMoveInfraToMove(it).right() }
                } else {
                    Mono.just(PokemonServiceImpl.PokemonServiceErrors.TechnicalError.left())
                }
            }
    }

    override fun findMoveDetailsById(idMove: Int): Mono<Either<PokemonServiceImpl.PokemonServiceErrors, MoveDetails>> {
        return webClient.get().uri(PATH_MOVEDETAILS, idMove)
            .exchangeToMono { response ->
                if (response.statusCode() == HttpStatus.OK) {
                    response.bodyToMono(MoveDetailsInfra::class.java).mapNotNull {
                        pokemonMapper.mapMoveDetailsInfraToMoveDetails(it).right()
                    }
                } else {
                    Mono.just(PokemonServiceImpl.PokemonServiceErrors.TechnicalError.left())
                }
            }
    }

    override fun findSpeciesById(id: Int): Mono<Either<PokemonServiceImpl.PokemonServiceErrors, Pokemon>> {
        return webClient.get().uri(PATH_SPECIES, id)
            .exchangeToMono { response ->
                if (response.statusCode() == HttpStatus.OK) {
                    response.bodyToMono(PokemonSpeciesInfra::class.java).mapNotNull {
                        pokemonMapper.mapSpeciesInfraToPokemon(it).right()
                    }
                } else {
                    Mono.just(PokemonServiceImpl.PokemonServiceErrors.TechnicalError.left())
                }
            }
    }

    override fun findChainEvolutionById(id: Int): Mono<Either<PokemonServiceImpl.PokemonServiceErrors, EvolutionChain>> {
        return webClient.get().uri(PATH_EVOLUTION, id)
            .exchangeToMono { response ->
                if (response.statusCode() == HttpStatus.OK) {
                    response.bodyToMono(EvolutionChainDetailsInfra::class.java).mapNotNull {
                        pokemonMapper.mapEvoDetailsToChainEvolution(it).right()
                    }
                } else {
                    Mono.just(PokemonServiceImpl.PokemonServiceErrors.TechnicalError.left())
                }
            }
    }

    override fun getTrainerPokedex(trainerId: UUID): Mono<Either<PokemonServiceImpl.PokemonServiceErrors, List<Int>>> =
        databaseClient.sql("SELECT pokemon_id FROM pokedex WHERE trainers_id = :trainerId")
            .bind("trainerId", trainerId.toString())
            .map { row -> row.get(0, Int::class.javaObjectType) }
            .all()
            .collectList()
            .map { it.filterNotNull().right() }
            /*.map { pokedexId ->
                if (pokedexId.isNotEmpty()) {
                    pokedexId.right()
                } else {
                    PokemonServiceImpl.PokemonServiceErrors.TechnicalError.left()
                }
            }*/

    override fun addPokemon(
        pokemonId: Int,
        trainerId: UUID
    ): Mono<Either<PokemonServiceImpl.PokemonServiceErrors, Int>> =
        databaseClient.sql("INSERT INTO pokedex(pokemon_id, trainers_id) VALUES(:pokemonId, :trainerId)")
            .bind("pokemonId", pokemonId)
            .bind("trainerId", trainerId.toString())
            .fetch()
            .rowsUpdated()
            .map { it.toInt() }
            .map {
                it?.right() ?: PokemonServiceImpl.PokemonServiceErrors.TechnicalError.left()
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