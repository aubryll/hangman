package com.freeman.hangman.service.match

import com.freeman.hangman.config.mapper.MatchMapper
import com.freeman.hangman.domain.Status
import com.freeman.hangman.domain.dto.APIResponse
import com.freeman.hangman.domain.dto.MatchDto
import com.freeman.hangman.domain.dto.WordDto
import com.freeman.hangman.domain.model.Match
import com.freeman.hangman.repository.MatchRepository
import com.freeman.hangman.service.base.BaseServiceImpl
import com.freeman.hangman.service.word.IWordService
import org.mapstruct.factory.Mappers
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import java.util.*

@Service
class MatchServiceImpl(
    mapper: MatchMapper = Mappers.getMapper(MatchMapper::class.java),
) : BaseServiceImpl<Match, MatchDto, MatchRepository, MatchMapper>(mapper), IMatchService {

    @Autowired
    lateinit var repo: MatchRepository

    @Value("\${com.freeman.default-score}")
    lateinit var defaultScore: String

    @Value("\${com.freeman.default-chances}")
    lateinit var defaultChances: String

    @Autowired
    lateinit var wordService: IWordService

    override fun getRepository(): MatchRepository {
        return repo
    }

    override fun createModel(v: MatchDto): Mono<Match> {
        return Mono.zip(super.createModel(v), wordService.findUniqueWord(v.userId))
            .flatMap { t ->
                val match = t.t1
                val word = t.t2
                Mono.just(
                    match.copy(
                        wordId = word.id,
                        chancesLeft = Integer.valueOf(defaultChances),
                        userEnteredInputs = ""
                    )
                )
            }
    }

    private fun processAnswer(matchDto: MatchDto): Mono<MatchDto> {
        return repo.findById(matchDto.id!!).flatMap { t ->
            Mono.zip(wordService.fetch(t.wordId), Mono.just(t))
        }.map { t ->
            val word = t.t1.body?.payload as WordDto
            val match = t.t2
            validateAnswer(match, word, matchDto.guessedLetter)
        }.map { t -> genericMapper.toDto(t) }.switchIfEmpty(Mono.defer { Mono.empty() })
    }


    private fun validateAnswer(match: Match, word: WordDto, guessedLetter: Char?): Match {
        if (guessedLetter != null && !match.userEnteredInputs!!.contains(guessedLetter) && match.status == Status.PLAYING) {
            //Append new char to userInput
            val newUserInput = "${match.userEnteredInputs}${guessedLetter}"
            //Calculate remaining chances
            val remainingChances = match.chancesLeft?.minus((if (word.word.contains(guessedLetter)) 0 else 1))
            //If user has 0 chances they have lost
            val newStatus = if (remainingChances == 0) Status.LOST else Status.PLAYING
            val updatedMatch =
                match.copy(status = newStatus, chancesLeft = remainingChances, userEnteredInputs = newUserInput)
            return if (newStatus == Status.PLAYING) {
                val wordSoFar =
                    String(word.word.toCharArray().map { char ->
                        if (newUserInput.lowercase(Locale.getDefault()).contains(
                                char.lowercaseChar()
                            )
                        ) char else '_'
                    }
                        .toCharArray())
                val isWon = if (wordSoFar == word.word) Status.WON else Status.PLAYING
                updatedMatch.copy(status = isWon, score = Integer.valueOf(defaultScore))

            } else {
                updatedMatch
            }
        }

        return match
    }


    override fun update(v: MatchDto): Mono<ResponseEntity<APIResponse>> {
        return processAnswer(v)
            .flatMap { t ->
                super.update(t)
            }
            .switchIfEmpty(Mono.defer { notFoundResponse() })
    }

    override fun copy(original: Match, update: Match): Match {
        return update.copy(updatedAt = original.updatedAt, createdAt = original.createdAt)
    }
}
