package fr.maif.codelab.spring.backkotlincoroutinepokedex.domain.models

data class ItemDetails(
    val spriteUrl: String,
    val name: String,
    val id: Int,
    val effect: String,
    val category: String
)
