package com.freeman.hangman.domain.dto

import com.fasterxml.jackson.annotation.JsonInclude
import com.freeman.hangman.domain.dto.base.BaseDto
import javax.validation.constraints.NotBlank

@JsonInclude(JsonInclude.Include.NON_NULL)
data class WordDto(
    @NotBlank(message = "Name is required") val word: String,
    override val id: Int?,
    override val createdAt: String?,
) : BaseDto(id, createdAt)
