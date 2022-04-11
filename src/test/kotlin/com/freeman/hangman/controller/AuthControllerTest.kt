package com.freeman.hangman.controller

import com.freeman.hangman.config.TestConfig
import com.freeman.hangman.config.security.JWTUtil
import com.freeman.hangman.controller.user.AuthController
import com.freeman.hangman.controller.word.WordControllerImpl
import com.freeman.hangman.domain.dto.LoginRequest
import com.freeman.hangman.domain.dto.WordDto
import com.freeman.hangman.domain.model.User
import com.freeman.hangman.domain.model.Word
import com.freeman.hangman.repository.MatchRepository
import com.freeman.hangman.repository.UserRepository
import com.freeman.hangman.repository.WordRepository
import com.freeman.hangman.service.user.UserServiceImpl
import com.freeman.hangman.service.word.WordServiceImpl
import com.freeman.hangman.util.Utils
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.context.annotation.Import
import org.springframework.http.MediaType
import org.springframework.security.authentication.ReactiveAuthenticationManager
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.csrf
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.web.reactive.function.BodyInserters
import reactor.core.publisher.Mono
import java.time.LocalDateTime

@ExtendWith(SpringExtension::class)
@WebFluxTest(AuthController::class)
@Import(UserServiceImpl::class, TestConfig::class)
class AuthControllerTest {

    @Autowired
    lateinit var webClient: WebTestClient

    @MockBean
    lateinit var userRepository: UserRepository

    @MockBean
    lateinit var authenticationManager: ReactiveAuthenticationManager

    @MockBean
    lateinit var jwtUtil: JWTUtil


    @MockBean
    lateinit var matchRepository: MatchRepository

    @MockBean
    lateinit var wordRepository: WordRepository


    private val CURRENT_DATE_TIME = LocalDateTime.now()
    private val TEST_USER_ID = 1
    private val TEST_EMAIL = "testuser@gmail.com"

    private fun buildUser(): User {
        return User(TEST_USER_ID, "Test User", TEST_EMAIL, "pwd", CURRENT_DATE_TIME, CURRENT_DATE_TIME)
    }

    private fun buildLoginRequest(): LoginRequest{
        return LoginRequest(TEST_EMAIL, "pwd")
    }


    @Test
    @WithMockUser(username = "testuser@gmail.com", authorities = ["ROLE_USER", "ROLE_ADMIN"], password = "pwd")
    fun givenLoginRequest_expectedAuthenticateUser(){
        val loginRequest = buildLoginRequest()
        val user = buildUser()
        `when`(userRepository.findByEmail(loginRequest.email)).thenReturn(Mono.just(user))

        webClient.mutateWith(csrf()).post()
            .uri("/freeman-hangman/auth/signin", loginRequest)
            .contentType(MediaType.APPLICATION_JSON)
            .body(BodyInserters.fromValue(loginRequest))
            .exchange()
            .expectStatus()
            .isOk

        Mockito.verify(userRepository, Mockito.times(1)).findByEmail(loginRequest.email)
    }

}