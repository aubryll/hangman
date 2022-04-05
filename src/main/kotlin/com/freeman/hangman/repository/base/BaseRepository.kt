package com.freeman.hangman.repository.base

import com.freeman.hangman.domain.model.base.BaseModel
import org.springframework.data.domain.Pageable
import org.springframework.data.r2dbc.repository.R2dbcRepository
import org.springframework.data.repository.NoRepositoryBean
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.util.function.Tuple2

@NoRepositoryBean
interface BaseRepository<T : BaseModel> : R2dbcRepository<T, Int> {

    fun findAll(pageable: Pageable): Tuple2<Mono<Long>, Flux<T>>

}