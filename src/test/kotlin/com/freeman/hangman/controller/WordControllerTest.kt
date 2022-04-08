package com.freeman.hangman.controller

import com.freeman.hangman.controller.word.WordControllerImpl
import com.freeman.hangman.domain.dto.APIResponse
import com.freeman.hangman.domain.dto.WordDto
import com.freeman.hangman.domain.model.Word
import com.freeman.hangman.service.word.IWordService
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mockito
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.reactive.server.WebTestClient
import reactor.core.publisher.Mono
import java.time.LocalDateTime


@WebFluxTest(WordControllerImpl::class)
@ExtendWith(SpringExtension::class)
class WordControllerTest {

    @Autowired
    lateinit var webTestClient: WebTestClient
    @MockBean
    lateinit var wordService: IWordService


    private fun wordBuilder(): Word {
        return Word("word", 1, LocalDateTime.now(), LocalDateTime.now())
    }



    private fun wordDtoBuilder(): WordDto {
        return WordDto("word", 1, createdAt = LocalDateTime.now().toString())
    }

    @Test
    fun createTest(){
        val wordDto = wordDtoBuilder()
       Mockito.`when`(wordService.create(wordDto)).thenReturn(Mono.just(ResponseEntity.status(HttpStatus.CREATED).body(
           APIResponse(
               status = HttpStatus.CREATED,
               payload = wordDto
           )
       )))

        webTestClient.post().uri("/freeman-hangman/words/create")
            .body(Mono.just(wordDto), WordDto::class.java)
            .exchange()
            .expectStatus().isCreated
    }


}