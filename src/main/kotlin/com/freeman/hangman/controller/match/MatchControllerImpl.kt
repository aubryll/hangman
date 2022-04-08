package com.freeman.hangman.controller.match

import com.freeman.hangman.controller.base.BaseControllerImpl
import com.freeman.hangman.domain.dto.APIResponse
import com.freeman.hangman.domain.dto.MatchDto
import com.freeman.hangman.domain.model.Match
import com.freeman.hangman.service.match.IMatchService
import org.springframework.context.annotation.Lazy
import org.springframework.data.domain.PageRequest
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono

@RestController
@RequestMapping(value = ["\${com.freeman.url}/matches"])
class MatchControllerImpl(
    @Lazy service: IMatchService,
) : BaseControllerImpl<Match, MatchDto, IMatchService>(), IMatchController {

    private val service: IMatchService

    init {
        this.service = service
    }

    override fun getService(): IMatchService {
        return service
    }

    @GetMapping(value = ["{userId}/{page}/{size}"])
    override fun fetch(
        @PathVariable userId: Int,
        @PathVariable page: Int,
        @PathVariable size: Int
    ): Mono<ResponseEntity<APIResponse>> {
        return getService().fetch(userId, PageRequest.of(page, size))
    }
}