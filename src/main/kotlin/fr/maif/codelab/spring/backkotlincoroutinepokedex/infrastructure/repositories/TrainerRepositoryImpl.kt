package fr.maif.codelab.spring.backkotlincoroutinepokedex.infrastructure.repositories

import fr.maif.codelab.spring.backkotlincoroutinepokedex.domain.models.Trainer
import fr.maif.codelab.spring.backkotlincoroutinepokedex.domain.repositories.TrainerRepository
import org.springframework.r2dbc.core.DatabaseClient
import org.springframework.stereotype.Repository
import reactor.core.publisher.Mono
import java.util.*

@Repository
class TrainerRepositoryImpl(
    private val databaseClient: DatabaseClient,
) : TrainerRepository {

    // TODO : passer les méthodes en coroutines ?? problème dans le TrainerController (.authenticate attends un mono)
    override fun register(username: String, password: String): Mono<Trainer> =
        databaseClient.sql("INSERT INTO trainers(username, password) VALUES(:username, :password)")
            .bind("username", username)
            .bind("password", password)
            // fait la requête
            .fetch()
            .rowsUpdated()
            .flatMap { logIn(username) }

    override fun logIn(username: String): Mono<Trainer> {
        return databaseClient.sql("SELECT * FROM trainers WHERE username = :username")
            .bind("username", username)
            .map { row ->
                Trainer(
                    UUID.fromString(row.get("id").toString()),
                    row.get("username").toString(),
                    row.get("password").toString()
                )
            // prend le premier OU on peut utiliser .one()
            }.first()
    }
}