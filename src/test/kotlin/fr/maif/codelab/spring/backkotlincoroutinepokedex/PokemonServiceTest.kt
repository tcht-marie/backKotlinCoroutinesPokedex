package fr.maif.codelab.spring.backkotlincoroutinepokedex

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import fr.maif.codelab.spring.backkotlincoroutinepokedex.domain.models.*
import fr.maif.codelab.spring.backkotlincoroutinepokedex.domain.repositories.PokemonRepository
import fr.maif.codelab.spring.backkotlincoroutinepokedex.domain.services.PokemonServiceImpl
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import java.util.*
import kotlin.test.Test
import kotlin.test.assertEquals

class PokemonServiceTest {

    private val pokemonRepository = mockk<PokemonRepository>()
    private val pokemonService = PokemonServiceImpl(pokemonRepository)

    @Test
    fun getPokemonsByPageShouldReturnPokemonList() {
        val pokemons: List<Pokemon> =
            listOf(
                Pokemon(1, "Osselait", "trop mim's", 1),
                Pokemon(2, "Salamèche", "trop mim's", 2)
            )
        val pokedex: Either<PokemonServiceImpl.PokemonServiceErrors, PokedexPage> = PokedexPage(pokemons, 1).right()

        coEvery { pokemonRepository.findPokemonsByPage(10, 0) } returns pokedex

        runBlocking { pokemonService.getPokemonsByPage(10, 0) }
    }

    @Test
    fun getPokemonsByNameShouldReturnPokemonList() {
        val pokemons: List<Pokemon> =
            listOf(
                Pokemon(1, "Osselait", "trop mim's", 1),
                Pokemon(2, "Salamèche", "trop mim's", 2)
            )
        val pokedex: Either<PokemonServiceImpl.PokemonServiceErrors, PokedexPage> = PokedexPage(pokemons, 1).right()

        coEvery { pokemonRepository.searchPokemonsByName(10, 0, "s") } returns pokedex

        runBlocking { pokemonService.getPokedexByName(10, 0, "s") }
    }

    @Test
    fun getPokemonByIdShouldReturnPokemonDetails() {
        val type1 = PokemonTypes.DARK
        val type2 = PokemonTypes.SHADOW
        val stat1 = PokemonStat(1, StatName.EVASION)
        val stat2 = PokemonStat(2, StatName.ACCURACY)

        val pokemonDetails: Either<PokemonServiceImpl.PokemonServiceErrors, PokemonDetails> =
            PokemonDetails(
                1,
                "Osselait",
                7,
                "osselait",
                13,
                "image/osselait",
                listOf(type1, type2),
                listOf(stat1, stat2),
                listOf("lightning-rod", "battle-armor")
            ).right()

        val pokemon = Pokemon(1, "Osselait", "trop mim's", 1)
        val evolvesToDetails = EvolvesToDetails(pokemon, listOf())
        val evoChain = EvolutionChain(evolvesToDetails)
        val completePokemon: Either<PokemonServiceImpl.PokemonServiceErrors, CompletePokemon> = CompletePokemon(
            1, "Osselait", 7, "osselait", 13, "image/osselait", "trop mim's",
            listOf(type1, type2),
            listOf(stat1, stat2), listOf("lightning-rod", "battle-armor"), evoChain
        ).right()

        coEvery { pokemonRepository.findPokemonById(1) } returns pokemonDetails
        coEvery { pokemonRepository.findSpeciesById(1) } returns pokemon.right()
        coEvery { pokemonRepository.findChainEvolutionById(1) } returns evoChain.right()

        runBlocking {
            assertEquals(completePokemon, pokemonService.getPokemonById(1))
        }
    }

    @Test
    fun getVersionsShouldReturnListVersions() {
        val versions: Either<PokemonServiceImpl.PokemonServiceErrors, List<Version>> =
            listOf(Version("Or"), Version("Ruby")).right()

        coEvery { pokemonRepository.findVersions() } returns versions

        runBlocking {
            assertEquals(versions, pokemonService.getVersions())
        }
    }

    @Test
    fun getVersionsShouldReturnAnException() {
        val versions: Either<PokemonServiceImpl.PokemonServiceErrors, List<Version>> =
            PokemonServiceImpl.PokemonServiceErrors.TechnicalError.left()

        coEvery { pokemonRepository.findVersions() } returns versions

        runBlocking {
            assertEquals(versions, pokemonService.getVersions())
        }
    }

    @Test
    fun getItemsByPageShouldReturnItemDetails() {
        val items = listOf(Item("pokeball", 1), Item("berry", 2))
        val itemDetails1 =
            ItemDetails("blabla", "pokeball", 1, "attrape pokemon", "pokeball")
        val itemDetails2 =
            ItemDetails("bla", "berry", 2, "nourrit pokemon", "nourriture")
        val itemList: Either<PokemonServiceImpl.PokemonServiceErrors, List<ItemDetails>> =
            listOf(itemDetails1, itemDetails2).right()

        coEvery { pokemonRepository.findItems(10, 0) } returns items.right()
        coEvery { pokemonRepository.findItemDetailsById(1) } returns itemDetails1.right()
        coEvery { pokemonRepository.findItemDetailsById(2) } returns itemDetails2.right()

        runBlocking {
            assertEquals(itemList, pokemonService.getItemsByPage(10, 0))
        }
    }

    @Test
    fun getMovesByPageShouldReturnMoveDetails() {
        val listMoves = listOf(Move("attack", 1), Move("defense", 2))
        val pokemons: List<Pokemon> =
            listOf(
                Pokemon(1, "Osselait", "trop mim's", 1),
                Pokemon(2, "Salamèche", "trop mim's", 2)
            )
        val moveDetails1 = MoveDetails("attaque", 33, 2, PokemonTypes.DRAGON, "blabla", pokemons)
        val moveDetails2 = MoveDetails("defense", 22, 2, PokemonTypes.BUG, "blablabla", pokemons)
        val listMovesDetails: Either<PokemonServiceImpl.PokemonServiceErrors, List<MoveDetails>> =
            listOf(moveDetails1, moveDetails2).right()

        coEvery { pokemonRepository.findMoves(10, 0) } returns listMoves.right()
        coEvery { pokemonRepository.findMoveDetailsById(1) } returns moveDetails1.right()
        coEvery { pokemonRepository.findMoveDetailsById(2) } returns moveDetails2.right()

        runBlocking {
            assertEquals(listMovesDetails, pokemonService.getMovesByPage(10, 0))
        }
    }

    @Test
    fun getTrainerPokedexShouldReturnPokemonList() {
        val pokemons: Either<PokemonServiceImpl.PokemonServiceErrors, List<Pokemon>> =
            listOf(
                Pokemon(1, "Osselait", "trop mim's", 1),
                Pokemon(2, "Salamèche", "trop mim's", 2)
            ).right()
        val uuid = UUID.randomUUID()
        val pokemonsIds: Either<PokemonServiceImpl.PokemonServiceErrors, List<Int>> = listOf(1, 2).right()

        coEvery { pokemonRepository.getTrainerPokedex(uuid) } returns pokemonsIds
        coEvery { pokemonsIds.getOrNull()?.let { pokemonRepository.findPokemonByIds(it) } } returns pokemons

        runBlocking {
            assertEquals(pokemons, pokemonService.getTrainerPokedex(uuid))
        }
    }

    @Test
    fun addPokemonShouldReturnPokemon() {
        val pokemon: Either<PokemonServiceImpl.PokemonServiceErrors, Pokemon> =
            Pokemon(1, "Osselait", "trop mim's", 1).right()
        val pokedex: Either<PokemonServiceImpl.PokemonServiceErrors, List<Pokemon>> =
            listOf(Pokemon(1, "Osselait", "trop mim's", 1)).right()
        val uuid = UUID.randomUUID()
        val pokemonsId: Either<PokemonServiceImpl.PokemonServiceErrors, Int> = 1.right()
        val pokemonsIds: List<Int> = listOf(1)

        coEvery { pokemonRepository.addPokemon(1, uuid) } returns pokemonsId
        coEvery { pokemonRepository.findPokemonByIds(pokemonsIds) } returns pokedex

        runBlocking {
            assertEquals(pokemon, pokemonService.addPokemon(1, uuid))
        }
    }

    @Test
    fun deletePokemonShouldReturnPokemon() {
        val pokemon: Either<PokemonServiceImpl.PokemonServiceErrors, Pokemon> =
            Pokemon(1, "Osselait", "trop mim's", 1).right()
        val pokedex: Either<PokemonServiceImpl.PokemonServiceErrors, List<Pokemon>> =
            listOf(Pokemon(1, "Osselait", "trop mim's", 1)).right()
        val uuid = UUID.randomUUID()
        val pokemonsId: Either<PokemonServiceImpl.PokemonServiceErrors, Int> = 1.right()
        val pokemonsIds: List<Int> = listOf(1)

        coEvery { pokemonRepository.deletePokemon(1, uuid) } returns pokemonsId
        coEvery { pokemonRepository.findPokemonByIds(pokemonsIds) } returns pokedex

        runBlocking {
            assertEquals(pokemon, pokemonService.deletePokemon(1, uuid))
        }
    }

    @Test
    fun deleteAllPokemonShouldReturnVoid() {
        val uuid = UUID.randomUUID()
        val expected: Either<PokemonServiceImpl.PokemonServiceErrors, Unit> = Unit.right()

        coEvery { pokemonRepository.deleteAllPokemons(uuid) } returns expected

        runBlocking {
            assertEquals(expected, pokemonService.deleteAllPokemons(uuid))
        }
    }
}