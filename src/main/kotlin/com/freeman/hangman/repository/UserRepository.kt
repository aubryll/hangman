package com.freeman.hangman.repository

import com.freeman.hangman.domain.model.User
import com.freeman.hangman.repository.base.BaseRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Mono

@Repository
interface UserRepository : BaseRepository<User> {
    fun findByUsername(username: String): Mono<User>
}