package com.freeman.hangman.controller

import com.freeman.hangman.controller.match.MatchControllerImpl
import com.freeman.hangman.domain.Status
import com.freeman.hangman.domain.dto.APIResponse
import com.freeman.hangman.domain.dto.MatchDto
import com.freeman.hangman.domain.dto.WordDto
import com.freeman.hangman.domain.model.Match
import com.freeman.hangman.domain.model.User
import com.freeman.hangman.repository.MatchRepository
import com.freeman.hangman.repository.UserRepository
import com.freeman.hangman.repository.WordRepository
import com.freeman.hangman.service.match.MatchServiceImpl
import com.freeman.hangman.service.user.UserServiceImpl
import com.freeman.hangman.service.word.WordServiceImpl
import com.freeman.hangman.util.Utils.Companion.any
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.context.annotation.Import
import org.springframework.data.domain.PageRequest
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.security.authentication.ReactiveAuthenticationManager
import org.springframework.security.core.Authentication
import org.springframework.security.test.context.TestSecurityContextHolder
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.csrf
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.web.reactive.function.BodyInserters
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.util.function.Tuples
import java.time.LocalDateTime

@ExtendWith(SpringExtension::class)
@WebFluxTest(MatchControllerImpl::class)
@Import(MatchServiceImpl::class)
class MatchControllerImplTest {

    @Autowired
    lateinit var webClient: WebTestClient

    @MockBean
    lateinit var matchRepository: MatchRepository

    @MockBean
    lateinit var wordRepository: WordRepository

    @MockBean
    lateinit var authenticationManager: ReactiveAuthenticationManager

    @MockBean
    lateinit var userService: UserServiceImpl

    @MockBean
    lateinit var userRepository: UserRepository

    @MockBean
    lateinit var wordService: WordServiceImpl

    @Mock
    lateinit var authentication: Authentication

    private val TEST_MATCH_ID: Int = 89
    private val TEST_USER_ID = 1
    private val TEST_WORD_ID: Int = 2

    private val CURRENT_DATE_TIME = LocalDateTime.now()

    private fun buildMatch(): Match {
        return Match(
            TEST_USER_ID,
            TEST_WORD_ID,
            "A",
            5,
            5,
            Status.PLAYING,
            TEST_MATCH_ID,
            CURRENT_DATE_TIME,
            CURRENT_DATE_TIME
        )
    }

    private fun buildUser(): User {
        return User(1, "Test user", "test@gmail.com", "pwd", CURRENT_DATE_TIME, CURRENT_DATE_TIME)
    }

    private fun buildMatchDto(): MatchDto {
        return MatchDto(
            TEST_USER_ID,
            TEST_WORD_ID,
            'A',
            "A",
            5,
            5,
            Status.PLAYING,
            TEST_MATCH_ID,
            CURRENT_DATE_TIME.toString()
        )
    }

    private fun buildWordDto(): WordDto {
        return WordDto("Test word", 1, CURRENT_DATE_TIME.toString())
    }


    @BeforeEach
    fun setup() {
        MockitoAnnotations.openMocks(this)
        `when`(authentication.principal).thenReturn(buildUser())
        `when`(authentication.details).thenReturn(buildUser())
        TestSecurityContextHolder.setAuthentication(authentication)
    }


    @Test
    @WithMockUser(username = "testuser@gmail.com", authorities = ["ROLE_USER", "ROLE_ADMIN"], password = "pwd")
    fun givenMatchDTO_expectedCreateMatch() {
        val match = Match(
            TEST_USER_ID,
            TEST_WORD_ID,
            "A",
            5,
            5,
            Status.PLAYING,
            0,
            null,
            CURRENT_DATE_TIME
        )

        `when`(matchRepository.save(any(Match::class.java))).thenReturn(Mono.just(match))

        webClient.mutateWith(csrf()).post()
            .uri("/freeman-hangman/matches/create")
            .contentType(MediaType.APPLICATION_JSON)
            .body(BodyInserters.fromValue {})
            .exchange()
            .expectStatus()
            .isCreated

        Mockito.verify(matchRepository, Mockito.times(1)).save(match)
    }

    @Test
    @WithMockUser(username = "testuser@gmail.com", authorities = ["ROLE_USER", "ROLE_ADMIN"], password = "pwd")
    fun givenMatchDTO_expectedUpdateMatch() {
        val match = buildMatch()
        val matchDto = buildMatchDto()
        val wordRes = ResponseEntity.status(HttpStatus.OK).body(
            APIResponse(status = HttpStatus.OK, payload = buildWordDto())
        )

        `when`(matchRepository.save(any(Match::class.java))).thenReturn(Mono.just(match))
        `when`(matchRepository.findById(TEST_MATCH_ID)).thenReturn(Mono.just(match))
        `when`(wordService.fetch(TEST_WORD_ID)).thenReturn(Mono.just(wordRes))

        webClient.mutateWith(csrf()).put()
            .uri("/freeman-hangman/matches/update")
            .contentType(MediaType.APPLICATION_JSON)
            .body(BodyInserters.fromValue(matchDto))
            .exchange()
            .expectStatus()
            .isOk

        Mockito.verify(matchRepository, Mockito.times(1)).save(match)
    }


    @Test
    @WithMockUser(username = "testuser@gmail.com", authorities = ["ROLE_USER", "ROLE_ADMIN"], password = "pwd")
    fun givenWordID_expectedFetchMatch() {
        `when`(matchRepository.findById(TEST_MATCH_ID)).thenReturn(Mono.just(buildMatch()))

        webClient.mutateWith(csrf()).get()
            .uri("/freeman-hangman/matches/{id}", TEST_MATCH_ID)
            .header(HttpHeaders.ACCEPT, "application/json")
            .exchange()
            .expectStatus()
            .isOk

        Mockito.verify(matchRepository, Mockito.times(1)).findById(TEST_MATCH_ID)
    }

    @Test
    @WithMockUser(username = "testuser@gmail.com", authorities = ["ROLE_USER", "ROLE_ADMIN"], password = "pwd")
    fun givenPageAndSize_expectedFetchMatches() {
        val matches = arrayOf(buildMatch())
        val pageable = PageRequest.of(0, 1)
        val tup = Tuples.of(Mono.just(1L), Flux.fromIterable(matches.asIterable()))
        `when`(matchRepository.findAll(pageable)).thenReturn(tup)

        webClient.mutateWith(csrf()).get()
            .uri("/freeman-hangman/matches/{page}/{size}", 0, 1)
            .header(HttpHeaders.ACCEPT, "application/json")
            .exchange()
            .expectStatus()
            .isOk

        Mockito.verify(matchRepository, Mockito.times(1)).findAll(pageable)
    }

}