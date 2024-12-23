package fr.maif.codelab.spring.backkotlincoroutinepokedex.web.dto

import fr.maif.codelab.spring.backkotlincoroutinepokedex.domain.models.StatName

enum class StatNameDto {
    HP,
    ATK,
    DEF,
    SATK,
    SDEF,
    SPD,
    ACC,
    EVA;

    companion object StaticFun {
        fun mapStatNameToStatNameDto(statName: StatName): StatNameDto? {
            return when(statName) {
                StatName.HP -> HP
                StatName.ATTACK -> ATK
                StatName.DEFENSE -> DEF
                StatName.SPECIAL_ATTACK -> SATK
                StatName.SPECIAL_DEFENSE -> SDEF
                StatName.SPEED -> SPD
                StatName.ACCURACY -> ACC
                StatName.EVASION -> EVA
                else -> null
            }
        }
    }
}
