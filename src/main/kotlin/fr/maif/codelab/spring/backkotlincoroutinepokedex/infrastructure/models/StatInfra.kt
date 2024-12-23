package fr.maif.codelab.spring.backkotlincoroutinepokedex.infrastructure.models

import com.fasterxml.jackson.annotation.JsonProperty

data class StatInfra(
    @JsonProperty("base_stat") val baseStat: Int,
    val stat: SubStatInfra
)
