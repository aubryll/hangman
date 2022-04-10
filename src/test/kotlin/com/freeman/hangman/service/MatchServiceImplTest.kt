package com.freeman.hangman.service

import com.freeman.hangman.domain.Status
import com.freeman.hangman.domain.dto.APIPaginatedResponse
import com.freeman.hangman.domain.dto.APIResponse
import com.freeman.hangman.domain.dto.MatchDto
import com.freeman.hangman.domain.dto.WordDto
import com.freeman.hangman.domain.model.Match
import com.freeman.hangman.domain.model.User
import com.freeman.hangman.domain.model.Word
import com.freeman.hangman.repository.MatchRepository
import com.freeman.hangman.service.match.MatchServiceImpl
import com.freeman.hangman.service.word.WordServiceImpl
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
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.security.test.context.TestSecurityContextHolder
import org.springframework.security.test.context.support.ReactorContextTestExecutionListener
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.TestExecutionListeners
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.util.ReflectionTestUtils
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.test.StepVerifier
import reactor.util.function.Tuples
import java.time.LocalDateTime


@ExtendWith(SpringExtension::class)
@ContextConfiguration
@TestExecutionListeners(ReactorContextTestExecutionListener::class)
class MatchServiceImplTest {

    @InjectMocks
    lateinit var matchServiceImpl: MatchServiceImpl

    @Mock
    lateinit var repo: MatchRepository

    @Mock
    lateinit var wordService: WordServiceImpl

    @Mock
    lateinit var authentication: Authentication

    private val TEST_MATCH_ID: Int = 89

    private val TEST_WORD_ID: Int = 2

    private val CURRENT_DATE_TIME = LocalDateTime.now()

    private fun buildMatch(): Match {
        return Match(
            1,
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


    //Commons
    private fun buildMatchDto(): MatchDto {
        return MatchDto(1, TEST_WORD_ID, 'A', "A", 5, 5, Status.PLAYING, TEST_MATCH_ID, CURRENT_DATE_TIME.toString())
    }

    private fun buildUser(): User {
        return User(1, "Test user", "test@gmail.com", "pwd", CURRENT_DATE_TIME, CURRENT_DATE_TIME)
    }

    private fun buildWord(): Word {
        return Word("Test word", 1, CURRENT_DATE_TIME, CURRENT_DATE_TIME)
    }

    private fun buildWordDto(): WordDto {
        return WordDto("Test word", 1, CURRENT_DATE_TIME.toString())
    }

    private fun matchAsserts(match: MatchDto, expected: MatchDto) {
        assertThat(match.id).isEqualTo(expected.id)
        assertThat(match.userId).isEqualTo(expected.userId)
        assertThat(match.wordId).isEqualTo(expected.wordId)
        assertThat(match.guessedLetter).isNotEqualTo(expected.guessedLetter)
        assertThat(match.userEnteredInputs).isEqualTo(expected.userEnteredInputs)
        assertThat(match.chancesLeft).isEqualTo(expected.chancesLeft)
        assertThat(match.score).isEqualTo(expected.score)
        assertThat(match.status).isEqualTo(expected.status)
    }

    //Tests
    @BeforeEach
    fun setup() {
        MockitoAnnotations.openMocks(this)
        `when`(authentication.principal).thenReturn(buildUser())
        `when`(authentication.details).thenReturn(buildUser())
        TestSecurityContextHolder.setAuthentication(authentication)
        ReflectionTestUtils.setField(matchServiceImpl, "defaultScore", "6")
        ReflectionTestUtils.setField(matchServiceImpl, "defaultChances", "6")
    }

    @AfterEach
    fun teardown() {

    }


    @Test
    fun givenRepository_expectedInitializedServiceRepository() {
        val repository = matchServiceImpl.getRepository()
        assertThat(repository).isNotNull
    }

    @Test
    fun givenMatchDto_expectModelCreated() {
        val matchDto = buildMatchDto()
        val match = matchServiceImpl.createModel(matchDto)
        assertThat(match).isNotNull
    }

    @Test
    fun givenMatchDto_expectedUpdateModelCreated(){
        val matchDto = buildMatchDto()
        val expected = buildMatch()
        val matchMono = matchServiceImpl.createUpdateModel(matchDto)
        StepVerifier.create(matchMono)
            .consumeNextWith { newMatch ->
                assertThat(newMatch.id).isEqualTo(expected.id)
                assertThat(newMatch.userId).isEqualTo(expected.userId)
                assertThat(newMatch.wordId).isEqualTo(expected.wordId)
                assertThat(newMatch.userEnteredInputs).isEqualTo(expected.userEnteredInputs)
                assertThat(newMatch.chancesLeft).isEqualTo(expected.chancesLeft)
                assertThat(newMatch.score).isEqualTo(expected.score)
                assertThat(newMatch.status).isEqualTo(expected.status)
                assertThat(newMatch.updatedAt).isNotEqualTo(expected.updatedAt)
                assertThat(newMatch.createdAt).isEqualTo(expected.createdAt)}
            .verifyComplete()
    }


    @Test
    fun givenMatch_expectDtoCreated() {
        val match = buildMatch()
        val matchDto = matchServiceImpl.toDto(match)
        assertThat(matchDto).isNotNull
    }

    @Test
    fun givenMatchDto_notEqualToMatch() {
        val matchDto = buildMatchDto()
        val match = buildMatch()
        assertThat(matchDto).isNotEqualTo(match)
    }


    @Test
    fun givenMatched_expectedMatch() {
        val match = buildMatch()
        `when`(repo.findById(TEST_MATCH_ID)).thenReturn(Mono.just(match))
        val matchMono = matchServiceImpl.fetch(TEST_MATCH_ID)
        StepVerifier.create(matchMono)
            .consumeNextWith { newMatch -> assertThat((newMatch.body?.payload as MatchDto).id).isEqualTo(TEST_MATCH_ID) }
            .verifyComplete()
    }


    @Test
    fun givenMatched_expectedMatchNotFound() {
        `when`(repo.findById(TEST_MATCH_ID)).thenReturn(Mono.empty())
        val matchMono = matchServiceImpl.fetch(TEST_MATCH_ID)
        StepVerifier.create(matchMono)
            .consumeNextWith { newMatch -> assertThat(newMatch.statusCode).isEqualTo(HttpStatus.NOT_FOUND) }
            .verifyComplete()
    }


    @Test
    fun givenMatched_expectedSignedInUserPaginatedMatches() {
        val matches = arrayOf(buildMatch())
        val pageable = PageRequest.of(0, 1)
        val tup = Tuples.of(Mono.just(1L), Flux.fromIterable(matches.asIterable()))
        `when`(repo.findAll(pageable)).thenReturn(tup)
        val matchesMono = matchServiceImpl.fetch(pageable)
        StepVerifier.create(matchesMono)
            .consumeNextWith { t ->
                assertThat((((t.body?.payload as APIPaginatedResponse).elements).first() as MatchDto).userId)
                    .isEqualTo((authentication.principal as User).id)
            }
            .verifyComplete()
    }

    @Test
    fun givenMatched_expectedPaginatedMatches() {
        val matches = arrayOf(buildMatch())
        val pageable = PageRequest.of(0, 1)
        val tup = Tuples.of(Mono.just(1L), Flux.fromIterable(matches.asIterable()))
        `when`(repo.findAll(pageable)).thenReturn(tup)
        val matchesMono = matchServiceImpl.fetch(pageable)
        StepVerifier.create(matchesMono)
            .consumeNextWith { t ->
                assertThat(((t.body?.payload as APIPaginatedResponse).elements.size))
                    .isEqualTo(matches.size)
            }
            .verifyComplete()
    }


    @Test
    fun givenDto_expectedCreateMatch() {
        val matchDto = buildMatchDto()
        `when`(wordService.findUniqueWord(buildUser().id)).thenReturn(Mono.just(buildWord()))
        `when`(repo.save(any(Match::class.java))).thenReturn(Mono.just(buildMatch()))
        val matchMono = matchServiceImpl.create(matchDto)
        StepVerifier.create(matchMono)
            .consumeNextWith { t ->
                val res = t.body?.payload as MatchDto
                matchAsserts(res, matchDto)
            }
            .verifyComplete()
    }


    @Test
    fun givenMatchDto_expectedUpdateMatch() {
        val matchDto = buildMatchDto()
        `when`(repo.save(any(Match::class.java))).thenReturn(Mono.just(buildMatch()))
        `when`(repo.findById(TEST_MATCH_ID)).thenReturn(Mono.just(buildMatch()))

        val wordRes = ResponseEntity.status(HttpStatus.OK).body(
            APIResponse(status = HttpStatus.OK, payload = buildWordDto())
        )

        `when`(wordService.fetch(TEST_WORD_ID)).thenReturn(Mono.just(wordRes))
        val matchMono = matchServiceImpl.update(matchDto)

        StepVerifier.create(matchMono)
            .consumeNextWith { t ->
                val res = t.body?.payload as MatchDto
                matchAsserts(res, matchDto)
            }
            .verifyComplete()
    }


    @Test
    fun givenMatch_expectedAlignCreatedAtAndUpdateAtDates() {
        val matchToUpdate = buildMatch().copy(
            createdAt = LocalDateTime.now().minusDays(1),
            updatedAt = LocalDateTime.now().minusMinutes(1)
        )
        val expected = buildMatch()
        val newMatch = matchServiceImpl.copy(expected, matchToUpdate)
        assertThat(newMatch).isEqualTo(expected)
    }


}