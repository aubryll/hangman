package com.freeman.hangman.controller.word

import com.freeman.hangman.controller.base.BaseControllerImpl
import com.freeman.hangman.domain.dto.WordDto
import com.freeman.hangman.domain.model.Word
import com.freeman.hangman.service.word.IWordService
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping(value = ["\${com.freeman.url}/words"])
class WordControllerImpl(
    service: IWordService,
) : BaseControllerImpl<Word, WordDto, IWordService>(), IWordController {

    private val service: IWordService

    init {
        this.service = service
    }

    override fun getService(): IWordService {
        return service
    }

}