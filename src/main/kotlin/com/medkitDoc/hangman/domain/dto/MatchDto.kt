package com.medkitDoc.hangman.domain.dto

import com.fasterxml.jackson.annotation.JsonInclude
import com.medkitDoc.hangman.domain.Status
import com.medkitDoc.hangman.domain.dto.base.BaseDto

@JsonInclude(JsonInclude.Include.NON_NULL)
data class MatchDto(
    val wordId: Int,
    val userInputWord: String,
    val chancesLeft: Int,
    val score: Int,
    val status: Status,
    override val id: Int,
    override val createdAt: String,
) : BaseDto(id, createdAt)