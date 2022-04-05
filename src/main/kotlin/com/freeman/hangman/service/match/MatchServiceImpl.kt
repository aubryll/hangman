package com.freeman.hangman.service.match

import com.freeman.hangman.config.mapper.MatchMapper
import com.freeman.hangman.domain.dto.MatchDto
import com.freeman.hangman.domain.model.Match
import com.freeman.hangman.repository.MatchRepository
import com.freeman.hangman.service.base.BaseServiceImpl
import org.mapstruct.factory.Mappers
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class MatchServiceImpl (
    mapper: MatchMapper = Mappers.getMapper(MatchMapper::class.java),
): BaseServiceImpl<Match, MatchDto, MatchRepository, MatchMapper>(mapper), IMatchService {

    @Autowired
    lateinit var repo: MatchRepository

    override fun getRepository(): MatchRepository {
        return repo
    }

    override fun copy(original: Match, update: Match): Match {
        return update.copy(updatedAt = original.updatedAt, createdAt = original.createdAt)
    }
}