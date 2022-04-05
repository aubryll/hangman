package com.freeman.hangman.service.base

import com.freeman.hangman.domain.dto.APIResponse
import com.freeman.hangman.domain.dto.base.BaseDto
import com.freeman.hangman.domain.model.base.BaseModel
import org.springframework.data.domain.Pageable
import org.springframework.http.ResponseEntity
import reactor.core.publisher.Mono

interface IBaseService<T : BaseModel, V : BaseDto> {

    fun createModel(v: V): Mono<T>
    fun create(v: V): Mono<ResponseEntity<APIResponse>>
    fun update(v: V): Mono<ResponseEntity<APIResponse>>
    fun fetch(id: Int): Mono<ResponseEntity<APIResponse>>
    fun fetch(pageable: Pageable): Mono<ResponseEntity<APIResponse>>
    fun copy(original: T, update: T): T

}