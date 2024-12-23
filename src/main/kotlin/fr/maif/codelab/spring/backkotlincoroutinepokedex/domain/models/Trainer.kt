package fr.maif.codelab.spring.backkotlincoroutinepokedex.domain.models

import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import java.util.*

data class Trainer(val id: UUID, private val username: String, private val password: String): UserDetails {
    override fun getAuthorities(): MutableCollection<out GrantedAuthority> = mutableListOf()

    override fun getPassword(): String = password

    override fun getUsername(): String = username
}
