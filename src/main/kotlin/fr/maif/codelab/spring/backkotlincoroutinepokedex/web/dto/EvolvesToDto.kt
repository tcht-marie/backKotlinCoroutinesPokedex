package fr.maif.codelab.spring.backkotlincoroutinepokedex.web.dto

data class EvolvesToDto(val evolvesTo: List<EvolvesToDto>, val pokemon: PokemonDto)
