package com.medkitDoc.hangman

import com.medkitDoc.hangman.repository.base.BaseRepositoryImpl
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories

@SpringBootApplication
@EnableR2dbcRepositories(repositoryBaseClass = BaseRepositoryImpl::class)
class HangmanApplication

fun main(args: Array<String>) {
	runApplication<HangmanApplication>(*args)
}
