package com.medkitDoc.hangman.domain.model

import com.medkitDoc.hangman.domain.model.base.BaseModel
import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDateTime

@Table("words")
data class Word(
    val word: String,
    val hint: String,
    override val id: Int,
    override val updatedAt: LocalDateTime,
    override val createdAt: LocalDateTime
) : BaseModel(id, updatedAt, createdAt)