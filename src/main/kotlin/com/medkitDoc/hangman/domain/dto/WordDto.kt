package com.medkitDoc.hangman.domain.dto

import com.fasterxml.jackson.annotation.JsonInclude
import com.medkitDoc.hangman.domain.dto.base.BaseDto

@JsonInclude(JsonInclude.Include.NON_NULL)
data class WordDto(
    val word: String, override val id: Int,
    override val createdAt: String,
) : BaseDto(id, createdAt)
