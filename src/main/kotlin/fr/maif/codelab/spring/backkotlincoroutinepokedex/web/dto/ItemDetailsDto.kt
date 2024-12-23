package fr.maif.codelab.spring.backkotlincoroutinepokedex.web.dto

data class ItemDetailsDto(
    val spriteUrl: String,
    val name: String,
    val id: Int,
    val effect: String,
    val category: String
)
