package com.freeman.hangman.domain.dto

data class AuthResponse(
    val id: Int,
    val email: String,
    val accessToken: String
)