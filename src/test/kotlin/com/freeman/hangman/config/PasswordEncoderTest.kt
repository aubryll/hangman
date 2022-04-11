package com.freeman.hangman.config

import org.springframework.security.crypto.password.PasswordEncoder

class PasswordEncoderTest: PasswordEncoder{

    override fun encode(rawPassword: CharSequence): String {
        return rawPassword.toString()
    }

    override fun matches(rawPassword: CharSequence, encodedPassword: String): Boolean {
        println("Matches called")
        return rawPassword.toString() == encodedPassword
    }
}