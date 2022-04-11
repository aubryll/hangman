package com.freeman.hangman

import com.freeman.hangman.controller.AuthControllerTest
import com.freeman.hangman.controller.WordControllerImplTest
import com.freeman.hangman.service.MatchServiceImplTest
import com.freeman.hangman.service.UserServiceImplTest
import com.freeman.hangman.service.WordServiceImplTest
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest(classes = [MatchServiceImplTest::class, UserServiceImplTest::class, WordServiceImplTest::class, AuthControllerTest::class, WordControllerImplTest::class])
class HangmanApplicationTests {

    @Test
    fun contextLoads() {
    }

}
