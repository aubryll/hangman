package com.freeman.hangman.controller.match

import com.freeman.hangman.controller.base.IBaseController
import com.freeman.hangman.domain.dto.APIResponse
import com.freeman.hangman.domain.dto.MatchDto
import org.springframework.http.ResponseEntity
import reactor.core.publisher.Mono

interface IMatchController : IBaseController<MatchDto>