package fr.maif.codelab.spring.backkotlincoroutinepokedex.web.controllers

import fr.maif.codelab.spring.backkotlincoroutinepokedex.domain.models.Trainer
import fr.maif.codelab.spring.backkotlincoroutinepokedex.domain.services.PokemonService
import fr.maif.codelab.spring.backkotlincoroutinepokedex.web.dto.*
import fr.maif.codelab.spring.backkotlincoroutinepokedex.web.mapper.PokemonMapperDto
import org.springframework.http.HttpStatusCode
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/pokemons")
class PokemonController(
    private val pokemonService: PokemonService,
    private val pokemonMapperDto: PokemonMapperDto
) {

    @GetMapping("", produces = [MediaType.APPLICATION_JSON_VALUE])
    suspend fun findPokemonsByPage(
        @RequestParam limit: Int,
        @RequestParam offset: Int
    ): ResponseEntity<List<PokemonDto>> =
        pokemonService.getPokemonsByPage(limit, offset).fold(
            { ResponseEntity.internalServerError().build() },
            { list ->
                val pokemons = list.map { pokemonMapperDto.mapPokemonToPokemonDto(it) }
                ResponseEntity.ok().body(pokemons)
            }
        )

    @GetMapping("/search", produces = [MediaType.APPLICATION_JSON_VALUE])
    suspend fun searchPokemoxByName(
        @RequestParam limit: Int,
        @RequestParam offset: Int,
        @RequestParam query: String
    ): ResponseEntity<List<PokemonDto>> =
        pokemonService.getPokedexByName(limit, offset, query).fold(
            { ResponseEntity.internalServerError().build() },
            { list ->
                val pokemons = list.map { pokemonMapperDto.mapPokemonToPokemonDto(it) }
                ResponseEntity.ok().body(pokemons)
            }
        )

    @GetMapping("/pokemon/{id}", produces = [MediaType.APPLICATION_JSON_VALUE])
    suspend fun findPokemonById(@PathVariable id: Int): ResponseEntity<CompletePokemonDto> =
        pokemonService.getPokemonById(id).fold(
            { ResponseEntity.internalServerError().build() },
            {
                ResponseEntity.ok().body(it?.let { completePokemon ->
                    pokemonMapperDto.mapCompletePokemonToCompletePokemonDto(
                        completePokemon
                    )
                })
            }
        )

    @GetMapping("/versions", produces = [MediaType.APPLICATION_JSON_VALUE])
    suspend fun findVersions(): ResponseEntity<List<VersionDto>> =
        pokemonService.getVersions().fold(
            {
                ResponseEntity.internalServerError().build()
            },
            { list ->
                val versions = list.map { pokemonMapperDto.mapVersionToVersionDto(it) }
                ResponseEntity.ok().body(versions)
            }
        )

    @GetMapping("/items", produces = [MediaType.APPLICATION_JSON_VALUE])
    suspend fun findItemDetailsByPage(
        @RequestParam limit: Int,
        @RequestParam offset: Int
    ): ResponseEntity<List<ItemDetailsDto>> =
        pokemonService.getItemsByPage(limit, offset).fold(
            { ResponseEntity.internalServerError().build() },
            { item ->
                val itemDetails = item.map { pokemonMapperDto.mapItemDetailsToItemDetailsDto(it) }
                ResponseEntity.ok().body(itemDetails)
            }
        )

    @GetMapping("/moves", produces = [MediaType.APPLICATION_JSON_VALUE])
    suspend fun findMoveDetailsByPage(
        @RequestParam limit: Int,
        @RequestParam offset: Int
    ): ResponseEntity<List<MoveDetailsDto>> =
        pokemonService.getMovesByPage(limit, offset).fold(
            { ResponseEntity.internalServerError().build() },
            { moves ->
                val moveDetails = moves.map { pokemonMapperDto.mapMoveDetailsToMoveDetailsDto(it) }
                ResponseEntity.ok().body(moveDetails)
            }
        )

    @GetMapping("/pokedex/me", produces = [MediaType.APPLICATION_JSON_VALUE])
    suspend fun findTrainerPokedex(authentication: Authentication): ResponseEntity<List<PokemonDto>> {
        val trainer = authentication.principal as Trainer
        return pokemonService.getTrainerPokedex(trainer.id).fold(
            { ResponseEntity.internalServerError().build() },
            { list ->
                val pokemons = list.map { pokemonMapperDto.mapPokemonToPokemonDto(it) }
                ResponseEntity.ok().body(pokemons)
            }
        )
    }

    @PostMapping("/pokedex/{pokemonId}/me", produces = [MediaType.APPLICATION_JSON_VALUE])
    suspend fun addPokemonToPokedex(
        @PathVariable pokemonId: Int,
        authentication: Authentication
    ): ResponseEntity<PokemonDto> {
        val trainer = authentication.principal as Trainer
        return pokemonService.addPokemon(pokemonId, trainer.id).fold(
            { ResponseEntity.internalServerError().build() },
            { ResponseEntity.ok().body(pokemonMapperDto.mapPokemonToPokemonDto(it)) }
        )
    }

    @DeleteMapping("/pokedex/{pokemonId}/me", produces = [MediaType.APPLICATION_JSON_VALUE])
    suspend fun deletePokemon(
        @PathVariable pokemonId: Int,
        authentication: Authentication
    ): ResponseEntity<PokemonDto> {
        val trainer = authentication.principal as Trainer
        return pokemonService.deletePokemon(pokemonId, trainer.id).fold(
            { ResponseEntity.internalServerError().build() },
            { ResponseEntity.ok().body(pokemonMapperDto.mapPokemonToPokemonDto(it)) }
        )
    }

    @DeleteMapping("/pokedex/me")
    suspend fun deleteAllPokemon(authentication: Authentication): ResponseEntity<Unit> {
        val trainer = authentication.principal as Trainer
        return pokemonService.deleteAllPokemons(trainer.id).fold(
            { ResponseEntity.internalServerError().build() },
            { ResponseEntity.status(HttpStatusCode.valueOf(200)).build() }
        )
    }
}