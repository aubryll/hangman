package com.freeman.hangman.service.user

import com.freeman.hangman.domain.dto.UserDto
import com.freeman.hangman.domain.model.User
import com.freeman.hangman.service.base.IBaseService
import reactor.core.publisher.Mono

interface IUserService : IBaseService<User, UserDto> {
    fun findByUsername(username: String): Mono<User>
}