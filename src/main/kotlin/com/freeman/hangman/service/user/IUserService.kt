package com.freeman.hangman.service.user

import com.freeman.hangman.domain.dto.APIResponse
import com.freeman.hangman.domain.dto.UserDto
import com.freeman.hangman.domain.model.User
import com.freeman.hangman.service.base.IBaseService
import org.springframework.http.ResponseEntity
import org.springframework.security.core.userdetails.ReactiveUserDetailsService
import reactor.core.publisher.Mono

interface IUserService : IBaseService<User, UserDto>, ReactiveUserDetailsService