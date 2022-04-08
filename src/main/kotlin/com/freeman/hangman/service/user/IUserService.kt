package com.freeman.hangman.service.user

import com.freeman.hangman.config.mapper.UserMapper
import com.freeman.hangman.domain.dto.UserDto
import com.freeman.hangman.domain.model.User
import com.freeman.hangman.service.base.IBaseService
import org.springframework.security.core.userdetails.ReactiveUserDetailsService


interface IUserService : IBaseService<User, UserDto>, ReactiveUserDetailsService