package fr.maif.codelab.spring.backkotlincoroutinepokedex.web.dto

data class PokemonDto(
    val id: Int,
    val idLabel: String,
    val name: String,
    val imageUrl: String
)
