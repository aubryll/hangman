package com.freeman.hangman.controller.match

import com.freeman.hangman.controller.base.BaseControllerImpl
import com.freeman.hangman.domain.dto.MatchDto
import com.freeman.hangman.domain.model.Match
import com.freeman.hangman.service.match.IMatchService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Lazy
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping(value = ["\${com.freeman.url}/matches"])
class MatchControllerImpl @Autowired constructor(
    @Lazy service: IMatchService,
): BaseControllerImpl<Match, MatchDto, IMatchService>(), IMatchController {

    private val service: IMatchService

    init {
        this.service = service
    }
    override fun getService(): IMatchService {
        return service
    }

}