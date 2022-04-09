package com.freeman.hangman.service.match

import com.freeman.hangman.config.mapper.MatchMapper
import com.freeman.hangman.config.security.JwtAuthenticationToken
import com.freeman.hangman.domain.Status
import com.freeman.hangman.domain.dto.APIPaginatedResponse
import com.freeman.hangman.domain.dto.APIResponse
import com.freeman.hangman.domain.dto.MatchDto
import com.freeman.hangman.domain.dto.WordDto
import com.freeman.hangman.domain.model.Match
import com.freeman.hangman.domain.model.User
import com.freeman.hangman.repository.MatchRepository
import com.freeman.hangman.service.base.BaseServiceImpl
import com.freeman.hangman.service.word.IWordService
import org.mapstruct.factory.Mappers
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.domain.Pageable
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.context.ReactiveSecurityContextHolder
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import reactor.core.scheduler.Schedulers
import java.security.Principal
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

    override fun fetch(pageable: Pageable): Mono<ResponseEntity<APIResponse>> {
        val authentication = ReactiveSecurityContextHolder.getContext().map { t -> t.authentication.principal as User }
        val tup = repo.findAll(pageable)
        return tup.t2.publishOn(Schedulers.boundedElastic())
            .collectList()
            .flatMap { elements ->
                tup.t1.zipWith(authentication).flatMap { t ->
                    Mono.just(
                        ResponseEntity.status(HttpStatus.OK).body(
                            APIResponse(
                                status = HttpStatus.OK, payload = APIPaginatedResponse(
                                    totalElements = t.t1,
                                    elements = genericMapper.toDto(elements.filter { z -> z.userId == z.userId}),
                                    pageNumber = pageable.pageNumber,
                                    pageSize = pageable.pageSize
                                )
                            )
                        )
                    )
                }
            }.switchIfEmpty(Mono.defer { notFoundResponse() })
    }

    override fun createModel(v: MatchDto): Mono<Match> {
        val authentication = ReactiveSecurityContextHolder.getContext().map { t -> t.authentication.principal as User }
        return Mono.zip(super.createModel(v), wordService.findUniqueWord(v.userId), authentication)
            .flatMap { t ->
                val match = t.t1
                val word = t.t2
                Mono.just(
                    match.copy(
                        userId = t.t3.id,
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


    private fun validateAnswer(match: Match, word: WordDto, letter: Char?): Match {

        if (letter != null && !match.userEnteredInputs!!.contains(letter.lowercaseChar()) && match.status == Status.PLAYING) {
            val guessedLetter = letter.lowercaseChar()
            //Append new char to userInput
            val newUserInput = "${match.userEnteredInputs}${guessedLetter}"
            //Calculate remaining chances
            val remainingChances = match.chancesLeft?.minus(
                (if (word.word.lowercase(Locale.getDefault()).contains(guessedLetter)) 0 else 1)
            )
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
                updatedMatch.copy(status = isWon, score = if (isWon == Status.WON) Integer.valueOf(defaultScore) else 0)

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
