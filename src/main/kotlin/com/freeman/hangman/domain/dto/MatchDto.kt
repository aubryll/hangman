package com.freeman.hangman.domain.dto

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.freeman.hangman.domain.Status
import com.freeman.hangman.domain.dto.base.BaseDto
import javax.validation.constraints.NotBlank

@JsonInclude(JsonInclude.Include.NON_NULL)
data class MatchDto(
    val userId: @NotBlank(message = "userid is required") String,
    @JsonProperty(access = JsonProperty.Access.READ_ONLY) val wordId: Int? = 0,
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY) val guessedLetter: Char?,
    val userEnteredInputs: String?,
    @JsonProperty(access = JsonProperty.Access.READ_ONLY) val chancesLeft: Int?,
    @JsonProperty(access = JsonProperty.Access.READ_ONLY) val score: Int? = 0,
    @JsonProperty(access = JsonProperty.Access.READ_ONLY) val status: Status?,
    override val id: Int?,
    override val createdAt: String?,
) : BaseDto(id, createdAt)