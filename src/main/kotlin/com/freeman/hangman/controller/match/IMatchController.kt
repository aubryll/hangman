package com.freeman.hangman.controller.match

import com.freeman.hangman.controller.base.IBaseController
import com.freeman.hangman.domain.dto.APIResponse
import com.freeman.hangman.domain.dto.MatchDto
import org.springframework.data.domain.Pageable
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PathVariable
import reactor.core.publisher.Mono

interface IMatchController : IBaseController<MatchDto>{
    fun fetch(userId: Int, page: Int, size: Int): Mono<ResponseEntity<APIResponse>>
}