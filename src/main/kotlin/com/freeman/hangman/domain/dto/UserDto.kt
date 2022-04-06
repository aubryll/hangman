package com.freeman.hangman.domain.dto

import com.freeman.hangman.domain.dto.base.BaseDto
import org.springframework.data.relational.core.mapping.Table

@Table("users")
data class UserDto(
    override val id: Int,
    val username: String,
    val fName: String,
    val lName: String,
    val enabled: Boolean,
    override val createdAt: String?,
) : BaseDto(id, createdAt)