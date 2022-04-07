package com.freeman.hangman.domain.dto

import javax.validation.constraints.NotBlank

data class LoginRequest(
    var email: @NotBlank String,
    var password: @NotBlank String
)