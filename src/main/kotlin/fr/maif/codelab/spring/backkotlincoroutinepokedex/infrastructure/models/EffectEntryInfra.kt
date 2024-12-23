package fr.maif.codelab.spring.backkotlincoroutinepokedex.infrastructure.models

import com.fasterxml.jackson.annotation.JsonProperty

data class EffectEntryInfra(@JsonProperty("short_effect") val shortEffect: String)
