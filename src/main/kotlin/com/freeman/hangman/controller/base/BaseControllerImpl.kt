package com.freeman.hangman.controller.base

import com.freeman.hangman.domain.dto.APIResponse
import com.freeman.hangman.domain.dto.base.BaseDto
import com.freeman.hangman.domain.model.base.BaseModel
import com.freeman.hangman.service.base.IBaseService
import org.springframework.data.domain.PageRequest
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Mono
import java.security.Principal
import javax.validation.Valid


abstract class BaseControllerImpl<T : BaseModel, V : BaseDto, K : IBaseService<T, V>> : IBaseController<V> {

    abstract fun getService(): K

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(value = ["/create"])
    override fun create(@RequestBody @Valid v: V): Mono<ResponseEntity<APIResponse>> {
        return getService().create(v)
    }

    @ResponseStatus(HttpStatus.OK)
    @PutMapping(value = ["/update"])
    override fun update(@RequestBody @Valid v: V): Mono<ResponseEntity<APIResponse>> {
        return getService().update(v)
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping(value = ["/{id}"])
    override fun fetch(@PathVariable id: Int): Mono<ResponseEntity<APIResponse>> {
        return getService().fetch(id)
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping(value = ["/{page}/{size}"])
    override fun fetch(@PathVariable page: Int, @PathVariable size: Int): Mono<ResponseEntity<APIResponse>> {
        return getService().fetch(PageRequest.of(page, size))
    }
}