package com.freeman.hangman.controller.base

import com.freeman.hangman.domain.dto.APIResponse
import com.freeman.hangman.domain.dto.base.BaseDto
import com.freeman.hangman.domain.model.base.BaseModel
import com.freeman.hangman.service.base.IBaseService
import org.springframework.data.domain.PageRequest
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import reactor.core.publisher.Mono


abstract class BaseControllerImpl<T: BaseModel, V: BaseDto, K: IBaseService<T, V>>: IBaseController<V> {

    abstract fun getService(): K

    @PostMapping(value = ["/create"])
    override fun create(v: V): Mono<ResponseEntity<APIResponse>> {
        return getService().create(v)
    }

    @PutMapping(value = ["/update"])
    override fun update(v: V): Mono<ResponseEntity<APIResponse>> {
        return getService().update(v)
    }

    @GetMapping(value = ["/{id}"])
    override fun fetch(@PathVariable id: Int): Mono<ResponseEntity<APIResponse>> {
        return getService().fetch(id)
    }

    @GetMapping(value = ["/{page}/{size}"])
    override fun fetch(@PathVariable page: Int, @PathVariable size: Int): Mono<ResponseEntity<APIResponse>> {
        return getService().fetch(PageRequest.of(page, size))
    }
}