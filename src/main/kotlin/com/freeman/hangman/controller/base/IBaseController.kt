package com.freeman.hangman.controller.base

import com.freeman.hangman.domain.dto.APIResponse
import com.freeman.hangman.domain.dto.base.BaseDto
import org.springframework.http.ResponseEntity
import reactor.core.publisher.Mono

interface IBaseController<V: BaseDto> {
    fun create(v: V): Mono<ResponseEntity<APIResponse>>
    fun update(v: V): Mono<ResponseEntity<APIResponse>>
    fun fetch(id: Int): Mono<ResponseEntity<APIResponse>>
    fun fetch(page: Int, size: Int): Mono<ResponseEntity<APIResponse>>
}