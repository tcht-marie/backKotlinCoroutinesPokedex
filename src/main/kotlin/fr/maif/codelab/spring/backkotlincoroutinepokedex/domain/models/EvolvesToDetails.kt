package fr.maif.codelab.spring.backkotlincoroutinepokedex.domain.models

data class EvolvesToDetails(val pokemon: Pokemon, val evolvesToDetails: List<EvolvesToDetails>)
