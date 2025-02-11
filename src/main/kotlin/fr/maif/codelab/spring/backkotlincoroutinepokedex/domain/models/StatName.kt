package fr.maif.codelab.spring.backkotlincoroutinepokedex.domain.models

import java.util.Arrays

enum class StatName(val statName: String) {
    HP("hp"),
    ATTACK("attack"),
    DEFENSE("defense"),
    SPECIAL_ATTACK("special-attack"),
    SPECIAL_DEFENSE("special-defense"),
    SPEED("speed"),
    ACCURACY("accuracy"),
    EVASION("evasion"),
    UNKNOWN("unknown");

    companion object {
        fun mapStringToStatName(value: String): StatName =
            entries.firstOrNull { it.statName.equals(value, ignoreCase = true) }
                ?: UNKNOWN
    }
}