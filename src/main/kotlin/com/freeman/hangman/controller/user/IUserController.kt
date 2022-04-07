package com.freeman.hangman.controller.user

import com.freeman.hangman.controller.base.IBaseController
import com.freeman.hangman.domain.dto.APIResponse
import com.freeman.hangman.domain.dto.UserDto
import org.springframework.http.ResponseEntity
import reactor.core.publisher.Mono

interface IUserController : IBaseController<UserDto>