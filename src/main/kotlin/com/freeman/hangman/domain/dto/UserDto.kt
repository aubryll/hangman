package com.freeman.hangman.domain.dto

import com.fasterxml.jackson.annotation.JsonProperty
import com.freeman.hangman.domain.dto.base.BaseDto
import javax.validation.constraints.NotBlank


data class UserDto(
    override val id: Int,
    val email: @NotBlank String,
    val name: @NotBlank String,
    val password: @NotBlank String,
    override val createdAt: String?,
) : BaseDto(id, createdAt)