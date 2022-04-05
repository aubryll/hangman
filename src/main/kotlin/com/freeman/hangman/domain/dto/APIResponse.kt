package com.freeman.hangman.domain.dto

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import org.springframework.http.HttpStatus

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
data class APIResponse(
    val status: HttpStatus,
    val payload: Any? = null,
    val message: String? = null,
    val errors: List<*>? = null,
    val metadata: Any? = null
)