package com.freeman.hangman.domain.model.base

import org.springframework.data.annotation.Id
import java.time.LocalDateTime

open class BaseModel(
    @Id open val id: Int,
    open val updatedAt: LocalDateTime,
    open val createdAt: LocalDateTime
)