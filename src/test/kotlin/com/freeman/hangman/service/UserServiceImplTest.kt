package com.freeman.hangman.service

import com.freeman.hangman.domain.dto.MatchDto
import com.freeman.hangman.domain.dto.UserDto
import com.freeman.hangman.domain.model.User
import com.freeman.hangman.repository.UserRepository
import com.freeman.hangman.service.user.UserServiceImpl
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.test.context.support.ReactorContextTestExecutionListener
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.TestExecutionListeners
import org.springframework.test.context.junit.jupiter.SpringExtension
import reactor.core.publisher.Mono
import reactor.test.StepVerifier
import java.time.LocalDateTime

@ExtendWith(SpringExtension::class)
@ContextConfiguration
@TestExecutionListeners(ReactorContextTestExecutionListener::class)
class UserServiceImplTest {


    @InjectMocks
    lateinit var userServiceImpl: UserServiceImpl

    @Mock
    lateinit var repo: UserRepository

    @Mock
    lateinit var passwordEncoder: PasswordEncoder

    private val CURRENT_DATE_TIME = LocalDateTime.now()
    private val TEST_USER_ID = 1
    private val TEST_EMAIL = "testuser@gmail.com"


    private fun buildUser(): User {
        return User(TEST_USER_ID, "Test User", TEST_EMAIL, "pwd", CURRENT_DATE_TIME, CURRENT_DATE_TIME)
    }

    private fun buildUserDto(): UserDto{
        return UserDto(TEST_USER_ID, TEST_EMAIL, "Test User","pwd", CURRENT_DATE_TIME.toString())
    }


    @BeforeEach
    fun setUp(){
        MockitoAnnotations.openMocks(this)
    }


    @AfterEach
    fun tearDown(){

    }

    @Test
    fun givenRepository_expectedInitializedServiceRepository() {
        val repository = userServiceImpl.getRepository()
        Assertions.assertThat(repository).isNotNull
    }

    @Test
    fun  givenEmail_expectedUser(){
        val expected = buildUser()
        Mockito.`when`(repo.findByEmail(TEST_EMAIL)).thenReturn(Mono.just(expected))
        val matchMono = userServiceImpl.findByUsername(TEST_EMAIL)
        StepVerifier.create(matchMono)
            .consumeNextWith { newUser -> Assertions.assertThat(newUser).isEqualTo(expected) }
            .verifyComplete()
    }

}