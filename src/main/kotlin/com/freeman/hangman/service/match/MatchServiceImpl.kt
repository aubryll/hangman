package com.freeman.hangman.service.match

import com.freeman.hangman.config.mapper.base.GenericMapperService
import com.freeman.hangman.domain.dto.MatchDto
import com.freeman.hangman.domain.model.Match
import com.freeman.hangman.repository.MatchRepository
import com.freeman.hangman.service.base.BaseServiceImpl
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Lazy
import org.springframework.stereotype.Service

@Service
class MatchServiceImpl @Autowired constructor(
    @Lazy mapperService: GenericMapperService,
    @Lazy appContext: MatchRepository,
): BaseServiceImpl<Match, MatchDto, MatchRepository>(mapperService), IMatchService {

    private val repository: MatchRepository

    init {
        this.repository = appContext
    }

    override fun getRepository(): MatchRepository {
        return repository
    }
}