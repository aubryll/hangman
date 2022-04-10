package com.freeman.hangman.service

import com.freeman.hangman.domain.dto.*
import com.freeman.hangman.domain.model.Match
import com.freeman.hangman.domain.model.Word
import com.freeman.hangman.repository.WordRepository
import com.freeman.hangman.service.word.WordServiceImpl
import com.freeman.hangman.util.Utils
import com.freeman.hangman.util.Utils.Companion.any
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import org.springframework.data.domain.PageRequest
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
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
class WordServiceImplTest {

    @InjectMocks
    lateinit var wordService: WordServiceImpl

    @Mock
    lateinit var repo: WordRepository

    private val CURRENT_DATE_TIME = LocalDateTime.now()
    private val TEST_WORD_ID = 1

    @BeforeEach
    fun setUp(){
        MockitoAnnotations.openMocks(this)
    }

    @AfterEach
    fun tearDown(){

    }

    fun buildWord(): Word{
        return Word("test word", TEST_WORD_ID, CURRENT_DATE_TIME, CURRENT_DATE_TIME)
    }

    fun buildWordDto(): WordDto{
        return WordDto("test word", TEST_WORD_ID, CURRENT_DATE_TIME.toString())
    }

    @Test
    fun givenRepository_expectedInitializedServiceRepository() {
        val repository = wordService.getRepository()
        Assertions.assertThat(repository).isNotNull
    }

    @Test
    fun givenWordDto_expectModelCreated() {
        val expected = buildWordDto()
        val userMono = wordService.createModel(expected)
        StepVerifier.create(userMono)
            .consumeNextWith { newUser->
                Assertions.assertThat(newUser.id).isEqualTo(expected.id)
                Assertions.assertThat(newUser.word).isEqualTo(expected.word)}
            .verifyComplete()
    }

    @Test
    fun givenMatched_expectedWord() {
        val word = buildWord()
        Mockito.`when`(repo.findById(TEST_WORD_ID)).thenReturn(Mono.just(word))
        val wordMono = wordService.fetch(TEST_WORD_ID)
        StepVerifier.create(wordMono)
            .consumeNextWith { newWord -> Assertions.assertThat((newWord.body?.payload as WordDto).id).isEqualTo(TEST_WORD_ID) }
            .verifyComplete()
    }

    @Test
    fun givenMatched_expectedUserNotFound() {
        Mockito.`when`(repo.findById(TEST_WORD_ID)).thenReturn(Mono.empty())
        val wordMono = wordService.fetch(TEST_WORD_ID)
        StepVerifier.create(wordMono)
            .consumeNextWith { newWord -> Assertions.assertThat(newWord.statusCode).isEqualTo(HttpStatus.NOT_FOUND) }
            .verifyComplete()
    }

    @Test
    fun givenMatched_expectedPaginatedWords() {
        val words = arrayOf(buildWord())
        val pageable = PageRequest.of(0, 1)
        val tup = Tuples.of(Mono.just(1L), Flux.fromIterable(words.asIterable()))
        Mockito.`when`(repo.findAll(pageable)).thenReturn(tup)
        val wordsMono = wordService.fetch(pageable)
        StepVerifier.create(wordsMono)
            .consumeNextWith { t ->
                Assertions.assertThat(((t.body?.payload as APIPaginatedResponse).elements.size))
                    .isEqualTo(words.size)
            }
            .verifyComplete()
    }

    @Test
    fun givenWordDto_notEqualToWord() {
        val wordDto = buildWordDto()
        val word = buildWord()
        Assertions.assertThat(wordDto).isNotEqualTo(word)
    }

    @Test
    fun givenUser_expectDtoCreated() {
        val word = buildWord()
        val wordDto = wordService.toDto(word)
        Assertions.assertThat(wordDto).isNotNull
    }

    @Test
    fun givenWordDto_expectedUpdateModelCreated(){
        val wordDto = buildWordDto()
        val expected = buildWord()
        val wordMono = wordService.createUpdateModel(wordDto)
        StepVerifier.create(wordMono)
            .consumeNextWith { newWord->
                Assertions.assertThat(newWord.id).isEqualTo(expected.id)
                Assertions.assertThat(newWord.word).isEqualTo(expected.word)}
            .verifyComplete()
    }


    @Test
    fun givenWord_expectedAlignCreatedAtAndUpdateAtDates() {
        val wordToUpdate = buildWord().copy(
            createdAt = LocalDateTime.now().minusDays(1),
            updatedAt = LocalDateTime.now().minusMinutes(1)
        )
        val expected = buildWord()
        val newUser = wordService.copy(expected, wordToUpdate)
        Assertions.assertThat(newUser).isEqualTo(expected)
    }

    @Test
    fun givenWordDto_expectedCreateWord(){
        val expected = buildWordDto()
        `when`(wordService.findUniqueWord(buildWord().id)).thenReturn(Mono.just(buildWord()))
        `when`(repo.save(any(Word::class.java))).thenReturn(Mono.just(buildWord()))
        val wordMono = wordService.create(expected)
        StepVerifier.create(wordMono)
            .consumeNextWith { t ->
                val newWord = t.body?.payload as WordDto
                Assertions.assertThat(newWord.id).isEqualTo(expected.id)
                Assertions.assertThat(newWord.word).isEqualTo(expected.word)
            }
    }

    @Test
    fun givenWordDto_expectedUpdateWord() {
        val expected = buildWordDto()
        `when`(repo.save(any(Word::class.java))).thenReturn(Mono.just(buildWord()))
        `when`(repo.findById(TEST_WORD_ID)).thenReturn(Mono.just(buildWord()))
        val matchMono = wordService.update(expected)
        StepVerifier.create(matchMono)
            .consumeNextWith { t ->
                val newWord = t.body?.payload as WordDto
                Assertions.assertThat(newWord.id).isEqualTo(expected.id)
                Assertions.assertThat(newWord.word).isEqualTo(expected.word)
            }
            .verifyComplete()
    }

}