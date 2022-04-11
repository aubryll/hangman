package com.freeman.hangman.controller

import com.freeman.hangman.config.TestConfig
import com.freeman.hangman.controller.user.AuthController
import com.freeman.hangman.controller.user.UserControllerImpl
import com.freeman.hangman.domain.dto.UserDto
import com.freeman.hangman.domain.model.User
import com.freeman.hangman.repository.MatchRepository
import com.freeman.hangman.repository.UserRepository
import com.freeman.hangman.repository.WordRepository
import com.freeman.hangman.service.user.UserServiceImpl
import com.freeman.hangman.util.Utils.Companion.any
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.Mockito
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.context.annotation.Import
import org.springframework.data.domain.PageRequest
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.web.reactive.function.BodyInserters
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.util.function.Tuples
import java.time.LocalDateTime

@ExtendWith(SpringExtension::class)
@WebFluxTest(UserControllerImpl::class)
@Import(UserServiceImpl::class, TestConfig::class)
class UserControllerImplTest {

    @MockBean
    lateinit var userRepository: UserRepository

    @Autowired
    lateinit var webClient: WebTestClient

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

    private fun buildUserDto(): UserDto {
        return UserDto(TEST_USER_ID, TEST_EMAIL, "Test User","pwd", CURRENT_DATE_TIME.toString())
    }


    @Test
    @WithMockUser(username = "testuser@gmail.com", authorities = ["ROLE_USER", "ROLE_ADMIN"], password = "pwd")
    fun givenUserDto_expectedCreateUser(){
        val userDto = UserDto(TEST_USER_ID, TEST_EMAIL, "Test User","pwd", CURRENT_DATE_TIME.toString())
        val user = User(TEST_USER_ID, "Test User", TEST_EMAIL, "pwd", null, CURRENT_DATE_TIME)
        Mockito.`when`(userRepository.findByEmail(userDto.email)).thenReturn(Mono.empty())
        Mockito.`when`(userRepository.save(any(User::class.java))).thenReturn(Mono.just(user))

        webClient.mutateWith(SecurityMockServerConfigurers.csrf()).post()
            .uri("/freeman-hangman/users/create")
            .contentType(MediaType.APPLICATION_JSON)
            .body(BodyInserters.fromValue(userDto))
            .exchange()
            .expectStatus()
            .isCreated

        Mockito.verify(userRepository, Mockito.times(1)).save(user)
    }

    @Test
    @WithMockUser(username = "testuser@gmail.com", authorities = ["ROLE_USER", "ROLE_ADMIN"], password = "pwd")
    fun givenUserDto_expectedUpdateUser(){
        val userDto = buildUserDto()
        val user = buildUser()

        Mockito.`when`(userRepository.save(any(User::class.java))).thenReturn(Mono.just(user))
        Mockito.`when`(userRepository.findById(TEST_USER_ID)).thenReturn(Mono.just(user))

        webClient.mutateWith(SecurityMockServerConfigurers.csrf()).put()
            .uri("/freeman-hangman/users/update")
            .contentType(MediaType.APPLICATION_JSON)
            .body(BodyInserters.fromValue(userDto))
            .exchange()
            .expectStatus()
            .isOk

        Mockito.verify(userRepository, Mockito.times(1)).save(user)
    }

    @Test
    @WithMockUser(username = "testuser@gmail.com", authorities = ["ROLE_USER", "ROLE_ADMIN"], password = "pwd")
    fun givenUserID_expectedFetchUser(){
        Mockito.`when`(userRepository.findById(TEST_USER_ID)).thenReturn(Mono.just(buildUser()))

        webClient.mutateWith(SecurityMockServerConfigurers.csrf()).get()
            .uri("/freeman-hangman/users/{id}", TEST_USER_ID)
            .header(HttpHeaders.ACCEPT, "application/json")
            .exchange()
            .expectStatus()
            .isOk

        Mockito.verify(userRepository, Mockito.times(1)).findById(TEST_USER_ID)
    }


    @Test
    @WithMockUser(username = "testuser@gmail.com", authorities = ["ROLE_USER", "ROLE_ADMIN"], password = "pwd")
    fun givenPageAndSize_expectedFetchUsers(){
        val users = arrayOf(buildUser())
        val pageable = PageRequest.of(0, 1)
        val tup = Tuples.of(Mono.just(1L), Flux.fromIterable(users.asIterable()))
        Mockito.`when`(userRepository.findAll(pageable)).thenReturn(tup)

        webClient.mutateWith(SecurityMockServerConfigurers.csrf()).get()
            .uri("/freeman-hangman/users/{page}/{size}", 0, 1)
            .header(HttpHeaders.ACCEPT, "application/json")
            .exchange()
            .expectStatus()
            .isOk

        Mockito.verify(userRepository, Mockito.times(1)).findAll(pageable)
    }


}