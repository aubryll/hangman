package com.freeman.hangman.repository.base

import com.freeman.hangman.domain.model.base.BaseModel
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.data.r2dbc.convert.R2dbcConverter
import org.springframework.data.r2dbc.core.R2dbcEntityOperations
import org.springframework.data.r2dbc.repository.support.SimpleR2dbcRepository
import org.springframework.data.relational.core.query.CriteriaDefinition
import org.springframework.data.relational.core.query.Query
import org.springframework.data.relational.repository.query.RelationalEntityInformation
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.util.function.Tuple2
import reactor.util.function.Tuples

class BaseRepositoryImpl<T : BaseModel>(
    private val entity: RelationalEntityInformation<T, Int>,
    private val entityOperations: R2dbcEntityOperations,
    converter: R2dbcConverter?
) :
    SimpleR2dbcRepository<T, Int>(entity, entityOperations, converter!!), BaseRepository<T> {


    override fun findAll(pageable: Pageable): Tuple2<Mono<Long>, Flux<T>> {
        val query = Query
            .query(CriteriaDefinition.empty())
        val totalCount = entityOperations
            .select(query, entity.javaType)
            .count()
        val finalQuery = query.with(pageable).sort(Sort.by("created_at").descending())
        val resultSet = entityOperations.select(finalQuery, entity.javaType)
        return Tuples.of(totalCount, resultSet)
    }

}