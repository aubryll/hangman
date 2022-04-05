package com.freeman.hangman.service.word

import com.freeman.hangman.config.mapper.base.GenericMapperService
import com.freeman.hangman.domain.dto.WordDto
import com.freeman.hangman.domain.model.Word
import com.freeman.hangman.repository.WordRepository
import com.freeman.hangman.service.base.BaseServiceImpl
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Lazy
import org.springframework.stereotype.Service

@Service
class WordServiceImpl @Autowired constructor(
    @Lazy mapperService: GenericMapperService,
    @Lazy repository: WordRepository,
): BaseServiceImpl<Word, WordDto, WordRepository>(mapperService), IWordService {

    private val repository: WordRepository

    init {
        this.repository = repository
    }

    override fun getRepository(): WordRepository {
        return repository
    }
}