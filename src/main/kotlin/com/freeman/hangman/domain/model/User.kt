package com.freeman.hangman.domain.model

import com.freeman.hangman.domain.model.base.BaseModel
import org.springframework.data.relational.core.mapping.Table
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import java.time.LocalDateTime

@Table("users")
data class User(
    override val id: Int,
     val name: String,
     val email: String,
    private val password: String,
    override val updatedAt: LocalDateTime?,
    override val createdAt: LocalDateTime?
) : UserDetails,
    BaseModel(id, updatedAt, createdAt) {

    override fun getAuthorities(): Collection<GrantedAuthority> {
        return ArrayList()
    }

    override fun getPassword(): String {
        return password
    }

    override fun getUsername(): String {
        return email
    }

    override fun isAccountNonExpired(): Boolean {
        return true
    }

    override fun isAccountNonLocked(): Boolean {
        return true
    }

    override fun isCredentialsNonExpired(): Boolean {
        return true
    }

    override fun isEnabled(): Boolean {
        return true
    }

    companion object {
        private const val serialVersionUID = 1L

        @JvmStatic
        fun build(user: User): User {
            return User(
                id = user.id,
                email = user.email,
                name = user.name,
                password = user.password,
                updatedAt = user.updatedAt,
                createdAt = user.createdAt
            )
        }
    }
}