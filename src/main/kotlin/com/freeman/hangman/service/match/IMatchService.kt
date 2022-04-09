package com.freeman.hangman.service.match

import com.freeman.hangman.domain.dto.APIResponse
import com.freeman.hangman.domain.dto.MatchDto
import com.freeman.hangman.domain.model.Match
import com.freeman.hangman.service.base.IBaseService
import org.springframework.data.domain.Pageable
import org.springframework.http.ResponseEntity
import reactor.core.publisher.Mono

interface IMatchService : IBaseService<Match, MatchDto>