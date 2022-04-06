package com.freeman.hangman.domain.model

import com.freeman.hangman.domain.model.base.BaseModel
import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDateTime

@Table("users")
data class User(
    override val id: Int,
    val username: String,
    val fName: String,
    val lName: String,
    val password: String,
    val enabled: Boolean,
    val roles: String,
    override val updatedAt: LocalDateTime?,
    override val createdAt: LocalDateTime?
) : BaseModel(id, updatedAt, createdAt)