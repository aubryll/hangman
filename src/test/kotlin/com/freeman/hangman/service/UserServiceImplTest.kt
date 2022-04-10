package com.freeman.hangman.service

import com.freeman.hangman.domain.dto.APIPaginatedResponse
import com.freeman.hangman.domain.dto.MatchDto
import com.freeman.hangman.domain.dto.UserDto
import com.freeman.hangman.domain.model.User
import com.freeman.hangman.repository.UserRepository
import com.freeman.hangman.service.user.UserServiceImpl
import com.freeman.hangman.util.Utils.Companion.any
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import org.springframework.data.domain.PageRequest
import org.springframework.http.HttpStatus
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.test.context.support.ReactorContextTestExecutionListener
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.TestExecutionListeners
import org.springframework.test.context.junit.jupiter.SpringExtension
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.test.StepVerifier
import reactor.util.function.Tuples
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
    private val TEST_EMAIL_NOT_EXIST = "testusernotexist@gmail.com"


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
        assertThat(repository).isNotNull
    }

    @Test
    fun givenUserDto_expectModelCreated() {
        val expected = buildUserDto()
        val userMono = userServiceImpl.createModel(expected)
        StepVerifier.create(userMono)
            .consumeNextWith { newUser->
                assertThat(newUser.id).isEqualTo(expected.id)
                assertThat(newUser.email).isEqualTo(expected.email)
                assertThat(newUser.name).isEqualTo(expected.name)}
            .verifyComplete()
    }

    @Test
    fun givenMatched_expectedUser() {
        val user = buildUser()
        `when`(repo.findById(TEST_USER_ID)).thenReturn(Mono.just(user))
        val userMono = userServiceImpl.fetch(TEST_USER_ID)
        StepVerifier.create(userMono)
            .consumeNextWith { newUser -> assertThat((newUser.body?.payload as UserDto).id).isEqualTo(TEST_USER_ID) }
            .verifyComplete()
    }

    @Test
    fun givenMatched_expectedUserNotFound() {
        `when`(repo.findById(TEST_USER_ID)).thenReturn(Mono.empty())
        val userMono = userServiceImpl.fetch(TEST_USER_ID)
        StepVerifier.create(userMono)
            .consumeNextWith { newUser -> assertThat(newUser.statusCode).isEqualTo(HttpStatus.NOT_FOUND) }
            .verifyComplete()
    }

    @Test
    fun givenMatched_expectedPaginatedUsers() {
        val users = arrayOf(buildUser())
        val pageable = PageRequest.of(0, 1)
        val tup = Tuples.of(Mono.just(1L), Flux.fromIterable(users.asIterable()))
        `when`(repo.findAll(pageable)).thenReturn(tup)
        val usersMono = userServiceImpl.fetch(pageable)
        StepVerifier.create(usersMono)
            .consumeNextWith { t ->
                assertThat(((t.body?.payload as APIPaginatedResponse).elements.size))
                    .isEqualTo(users.size)
            }
            .verifyComplete()
    }



    @Test
    fun givenUserDto_notEqualToUser() {
        val userDto = buildUserDto()
        val user = buildUser()
        assertThat(userDto).isNotEqualTo(user)
    }

    @Test
    fun givenUser_expectDtoCreated() {
        val user = buildUser()
        val userDto = userServiceImpl.toDto(user)
        assertThat(userDto).isNotNull
    }

    @Test
    fun givenUserDto_expectedUpdateModelCreated(){
        val userDto = buildUserDto()
        val expected = buildUser()
        val userMono = userServiceImpl.createUpdateModel(userDto)
        StepVerifier.create(userMono)
            .consumeNextWith { newUser->
                assertThat(newUser.id).isEqualTo(expected.id)
                assertThat(newUser.email).isEqualTo(expected.email)
                assertThat(newUser.name).isEqualTo(expected.name)}
            .verifyComplete()
    }




    @Test
    fun givenUser_expectedAlignCreatedAtAndUpdateAtDates() {
        val userToUpdate = buildUser().copy(
            createdAt = LocalDateTime.now().minusDays(1),
            updatedAt = LocalDateTime.now().minusMinutes(1)
        )
        val expected = buildUser()
        val newUser = userServiceImpl.copy(expected, userToUpdate)
        assertThat(newUser).isEqualTo(expected)
    }

    @Test
    fun  givenEmail_expectedUser(){
        val expected = buildUser()
        `when`(repo.findByEmail(TEST_EMAIL)).thenReturn(Mono.just(expected))
        val matchMono = userServiceImpl.findByUsername(TEST_EMAIL)
        StepVerifier.create(matchMono)
            .consumeNextWith { newUser -> assertThat(newUser).isEqualTo(expected) }
            .verifyComplete()
    }

    @Test
    fun givenExistEmail_expectedUserAlreadyExits(){
        val userDto = buildUserDto()
        val user = buildUser()
        `when`(repo.findByEmail(userDto.email)).thenReturn(Mono.just(user))
        val userMono = userServiceImpl.create(userDto)
        StepVerifier
            .create(userMono)
            .consumeNextWith { res -> assertThat(res.statusCode).isEqualTo(HttpStatus.BAD_REQUEST)}
            .verifyComplete()

    }

    private fun userAssert(userDto: UserDto, expected: UserDto){
        assertThat(userDto.id).isEqualTo(expected.id)
        assertThat(userDto.email).isEqualTo(expected.email)
        assertThat(userDto.name).isEqualTo(expected.name)
    }

    @Test
    fun givenUserDto_expectedCreateUser(){
        val userDto = buildUserDto().copy(email = TEST_EMAIL_NOT_EXIST)
        val user = buildUser().copy(password = "encryptedPassword")
        val expected = userServiceImpl.toDto(user)
        `when`(repo.findByEmail(TEST_EMAIL_NOT_EXIST)).thenReturn(Mono.empty())
        `when`(repo.save(any(User::class.java))).thenReturn(Mono.just(user))
        `when`(passwordEncoder.encode(userDto.password)).thenReturn(user.password)

        val userMono = userServiceImpl.create(userDto)
        StepVerifier
            .create(userMono)
            .consumeNextWith { res ->
                val newUser = res.body?.payload as UserDto
                userAssert(newUser, expected)
            }
            .verifyComplete()
    }

    @Test
    fun givenUserDto_expectedUpdateUser() {
        val userDto = buildUserDto()
        `when`(repo.save(any(User::class.java))).thenReturn(Mono.just(buildUser()))
        `when`(repo.findById(TEST_USER_ID)).thenReturn(Mono.just(buildUser()))

        val matchMono = userServiceImpl.update(userDto)

        StepVerifier.create(matchMono)
            .consumeNextWith { t ->
                val newUser = t.body?.payload as UserDto
                userAssert(newUser, userDto)
            }
            .verifyComplete()
    }

}