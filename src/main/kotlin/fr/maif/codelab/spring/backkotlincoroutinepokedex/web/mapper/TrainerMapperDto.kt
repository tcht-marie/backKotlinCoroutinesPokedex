package fr.maif.codelab.spring.backkotlincoroutinepokedex.web.mapper

import fr.maif.codelab.spring.backkotlincoroutinepokedex.domain.models.Trainer
import fr.maif.codelab.spring.backkotlincoroutinepokedex.web.dto.TrainerDto
import org.springframework.stereotype.Component

@Component
class TrainerMapperDto {
    fun mapTrainerToTrainerDto(trainer: Trainer) : TrainerDto {
        return TrainerDto(trainer.id, trainer.username, trainer.password)
    }
}