package com.freeman.hangman.controller

import com.freeman.hangman.controller.word.WordControllerImpl
import com.freeman.hangman.domain.dto.APIResponse
import com.freeman.hangman.domain.dto.WordDto
import com.freeman.hangman.domain.model.Word
import com.freeman.hangman.repository.MatchRepository
import com.freeman.hangman.repository.UserRepository
import com.freeman.hangman.repository.WordRepository
import com.freeman.hangman.service.word.WordServiceImpl
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

@ExtendWith(SpringExtension::class)
@WebFluxTest(WordControllerImpl::class)
@Import(WordServiceImpl::class)
class WordControllerImplTest {

    @Autowired
    lateinit var webClient: WebTestClient

    @MockBean
    lateinit var wordService: WordServiceImpl

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
    fun givenID_expectedWord(){
        val wordDto = WordDto(word = "test word", id = 0, createdAt = CURRENT_DATE_TIME.toString())
        val word = Word("test word", 0, CURRENT_DATE_TIME, CURRENT_DATE_TIME)
        `when`(wordRepository.save(word)).thenReturn(Mono.just(word))

        webClient.mutateWith(csrf()).post()
            .uri("/freeman-hangman/words/create")
            .contentType(MediaType.APPLICATION_JSON)
            .body(BodyInserters.fromValue(wordDto))
            .exchange()
            .expectStatus()
            .isCreated

        verify(wordRepository, times(1)).save(word)
    }

}