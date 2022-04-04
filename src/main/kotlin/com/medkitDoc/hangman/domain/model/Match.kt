package com.medkitDoc.hangman.domain.model

import com.medkitDoc.hangman.domain.model.base.BaseModel
import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDateTime

@Table("matches")
class Match(
    val userId: String,
    val wordId: Int,
    val userInputWord: String,
    val chancesLeft: Int,
    val score: Int,
    val status: Status,
    override val id: Int,
    override val updatedAt: LocalDateTime,
    override val createdAt: LocalDateTime
) : BaseModel(id, updatedAt, createdAt) {
    enum class Status {
        WON, PLAYING, LOST
    }
}