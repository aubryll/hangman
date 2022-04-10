package com.freeman.hangman

import com.freeman.hangman.repository.base.BaseRepositoryImpl
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories

@SpringBootTest
@EnableR2dbcRepositories(repositoryBaseClass = BaseRepositoryImpl::class)
class HangmanApplicationTests {

    @Test
    fun contextLoads() {
    }

}
