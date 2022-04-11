package com.freeman.hangman.controller

import com.freeman.hangman.controller.word.WordControllerImpl
import com.freeman.hangman.domain.dto.APIResponse
import com.freeman.hangman.domain.dto.WordDto
import com.freeman.hangman.domain.model.Word
import com.freeman.hangman.repository.MatchRepository
import com.freeman.hangman.repository.UserRepository
import com.freeman.hangman.repository.WordRepository
import com.freeman.hangman.service.word.WordServiceImpl
import com.freeman.hangman.util.Utils
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mockito
import org.mockito.Mockito.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.context.annotation.Import
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.csrf
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.web.reactive.function.BodyInserters
import reactor.core.publisher.Mono
import java.time.LocalDateTime
import com.freeman.hangman.util.Utils.Companion.any
import org.springframework.data.domain.PageRequest
import org.springframework.http.HttpHeaders
import reactor.core.publisher.Flux
import reactor.util.function.Tuple2
import reactor.util.function.Tuples

@ExtendWith(SpringExtension::class)
@WebFluxTest(WordControllerImpl::class)
@Import(WordServiceImpl::class)
class WordControllerImplTest {

    @Autowired
    lateinit var webClient: WebTestClient


    @MockBean
    lateinit var wordRepository: WordRepository

    @MockBean
    lateinit var matchRepository: MatchRepository

    @MockBean
    lateinit var userRepository: UserRepository

    private val CURRENT_DATE_TIME = LocalDateTime.now()
    private val TEST_WORD_ID = 1


    private fun buildWord(): Word {
        return Word("test word", TEST_WORD_ID, CURRENT_DATE_TIME, CURRENT_DATE_TIME)
    }

    private fun buildWordDto(): WordDto {
        return WordDto("test word", TEST_WORD_ID, CURRENT_DATE_TIME.toString())
    }

    @Test
    @WithMockUser(username = "testuser@gmail.com", authorities = ["ROLE_USER", "ROLE_ADMIN"], password = "pwd")
    fun givenWordDTO_expectedCreateWord(){
        val wordDto = WordDto(word = "test word", id = 0, createdAt = CURRENT_DATE_TIME.toString())
        val word = Word("test word", 0, null, CURRENT_DATE_TIME)
        `when`(wordRepository.save(any(Word::class.java))).thenReturn(Mono.just(word))

        webClient.mutateWith(csrf()).post()
            .uri("/freeman-hangman/words/create")
            .contentType(MediaType.APPLICATION_JSON)
            .body(BodyInserters.fromValue(wordDto))
            .exchange()
            .expectStatus()
            .isCreated

        verify(wordRepository, times(1)).save(word)
    }

    @Test
    @WithMockUser(username = "testuser@gmail.com", authorities = ["ROLE_USER", "ROLE_ADMIN"], password = "pwd")
    fun givenWordDTO_expectedUpdateWord(){
        val wordDto = WordDto(word = "test word", id = TEST_WORD_ID, createdAt = CURRENT_DATE_TIME.toString())
        val word = Word("test word", TEST_WORD_ID, CURRENT_DATE_TIME, CURRENT_DATE_TIME)
        `when`(wordRepository.save(any(Word::class.java))).thenReturn(Mono.just(word))
        `when`(wordRepository.findById(TEST_WORD_ID)).thenReturn(Mono.just(buildWord()))

        webClient.mutateWith(csrf()).put()
            .uri("/freeman-hangman/words/update")
            .contentType(MediaType.APPLICATION_JSON)
            .body(BodyInserters.fromValue(wordDto))
            .exchange()
            .expectStatus()
            .isOk

        verify(wordRepository, times(1)).save(word)
    }

    @Test
    @WithMockUser(username = "testuser@gmail.com", authorities = ["ROLE_USER", "ROLE_ADMIN"], password = "pwd")
    fun givenWordID_expectedFetchWord(){
        `when`(wordRepository.findById(TEST_WORD_ID)).thenReturn(Mono.just(buildWord()))

        webClient.mutateWith(csrf()).get()
            .uri("/freeman-hangman/words/{id}", TEST_WORD_ID)
            .header(HttpHeaders.ACCEPT, "application/json")
            .exchange()
            .expectStatus()
            .isOk

        verify(wordRepository, times(1)).findById(TEST_WORD_ID)
    }

    @Test
    @WithMockUser(username = "testuser@gmail.com", authorities = ["ROLE_USER", "ROLE_ADMIN"], password = "pwd")
    fun givenWordPage_expectedFetchWords(){
        val words = arrayOf(buildWord())
        val pageable = PageRequest.of(0, 1)
        val tup = Tuples.of(Mono.just(1L), Flux.fromIterable(words.asIterable()))
        `when`(wordRepository.findAll(pageable)).thenReturn(tup)

        webClient.mutateWith(csrf()).get()
            .uri("/freeman-hangman/words/{page}/{size}", 0, 1)
            .header(HttpHeaders.ACCEPT, "application/json")
            .exchange()
            .expectStatus()
            .isOk

        verify(wordRepository, times(1)).findAll(pageable)
    }

}