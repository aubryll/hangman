package com.freeman.hangman.service.base

import com.freeman.hangman.config.mapper.base.GenericMapper
import com.freeman.hangman.domain.dto.APIPaginatedResponse
import com.freeman.hangman.domain.dto.APIResponse
import com.freeman.hangman.domain.dto.base.BaseDto
import com.freeman.hangman.domain.model.base.BaseModel
import com.freeman.hangman.repository.base.BaseRepository
import org.springframework.data.domain.Pageable
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.transaction.annotation.Transactional
import reactor.core.publisher.Mono
import reactor.core.scheduler.Schedulers

@Transactional
abstract class BaseServiceImpl<T : BaseModel, V : BaseDto, E : BaseRepository<T>, M : GenericMapper<T, V>>(
    genericMapper: M
) : IBaseService<T, V> {

    final val genericMapper: GenericMapper<T, V>
    abstract fun getRepository(): E

    init {
        this.genericMapper = genericMapper
    }

    override fun toDto(v: T): V = genericMapper.toDto(v)

    override fun createModel(v: V): Mono<T> {
        val t = genericMapper.toModel(v)
        return Mono.just(t)
    }

    override fun createUpdateModel(v: V): Mono<T> {
        val t = genericMapper.toModel(v)
        return Mono.just(t)
    }


    override fun create(v: V): Mono<ResponseEntity<APIResponse>> {
        return createModel(v)
            .publishOn(Schedulers.boundedElastic())
            .flatMap { t -> getRepository().save(t) }
            .flatMap { t ->
                Mono.just(
                    ResponseEntity.status(HttpStatus.CREATED)
                        .body(APIResponse(status = HttpStatus.CREATED, payload = genericMapper.toDto(t)))
                )
            }.switchIfEmpty(Mono.defer { errorResponse() })

    }

    override fun update(v: V): Mono<ResponseEntity<APIResponse>> {
        return getRepository().findById(v.id!!)
            .flatMap { t ->
                Mono.zip(Mono.just(t), createUpdateModel(v))
            }
            .flatMap { t ->
                val original = t.t1
                val update = t.t2
                Mono.just(copy(original, update))
            }.publishOn(Schedulers.boundedElastic())
            .flatMap { t ->
                getRepository().save(t)
            }
            .flatMap { t ->
                Mono.just(
                    ResponseEntity.status(HttpStatus.OK)
                        .body(APIResponse(status = HttpStatus.OK, payload = genericMapper.toDto(t)))
                )
            }
            .switchIfEmpty(Mono.defer { errorResponse() })
    }

    override fun fetch(id: Int): Mono<ResponseEntity<APIResponse>> {
        return getRepository().findById(id)
            .publishOn(Schedulers.boundedElastic())
            .flatMap { t ->
                Mono.just(
                    ResponseEntity.status(HttpStatus.OK).body(
                        APIResponse(status = HttpStatus.OK, payload = genericMapper.toDto(t))
                    )
                )
            }
            .switchIfEmpty(Mono.defer { notFoundResponse() })
    }

    override fun fetch(pageable: Pageable): Mono<ResponseEntity<APIResponse>> {
        val tup = getRepository().findAll(pageable)
        return tup.t2.publishOn(Schedulers.boundedElastic())
            .collectList()
            .flatMap { elements ->
                tup.t1.flatMap { totalCount ->
                    Mono.just(
                        ResponseEntity.status(HttpStatus.OK).body(
                            APIResponse(
                                status = HttpStatus.OK, payload = APIPaginatedResponse(
                                    totalElements = totalCount,
                                    elements = genericMapper.toDto(elements),
                                    pageNumber = pageable.pageNumber,
                                    pageSize = pageable.pageSize
                                )
                            )
                        )
                    )
                }
            }.switchIfEmpty(Mono.defer { notFoundResponse() })
    }

    fun errorResponse(): Mono<ResponseEntity<APIResponse>> {
        return Mono.just(
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(APIResponse(status = HttpStatus.INTERNAL_SERVER_ERROR, message = "Unknown error occurred"))
        )
    }

    fun notFoundResponse(): Mono<ResponseEntity<APIResponse>> {
        return Mono.just(
            ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(APIResponse(status = HttpStatus.NOT_FOUND, message = "No results found"))
        )
    }
}