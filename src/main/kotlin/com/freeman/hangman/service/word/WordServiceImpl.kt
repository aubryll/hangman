package com.freeman.hangman.service.word

import com.freeman.hangman.config.mapper.WordMapper
import com.freeman.hangman.domain.dto.WordDto
import com.freeman.hangman.domain.model.Word
import com.freeman.hangman.repository.WordRepository
import com.freeman.hangman.service.base.BaseServiceImpl
import org.mapstruct.factory.Mappers
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class WordServiceImpl(
    mapper: WordMapper = Mappers.getMapper(WordMapper::class.java),
) : BaseServiceImpl<Word, WordDto, WordRepository, WordMapper>(mapper), IWordService {

    @Autowired
    lateinit var repo: WordRepository

    override fun getRepository(): WordRepository {
        return repo
    }

    override fun copy(original: Word, update: Word): Word {
        return update.copy(updatedAt = original.updatedAt, createdAt = original.createdAt)
    }


}