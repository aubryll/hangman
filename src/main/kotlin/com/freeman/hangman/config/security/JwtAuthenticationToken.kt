package com.freeman.hangman.config.security

import org.springframework.security.authentication.AbstractAuthenticationToken
import org.springframework.security.core.authority.AuthorityUtils
import org.springframework.security.core.userdetails.UserDetails

class JwtAuthenticationToken(private val token: String, private val userDetails: UserDetails?) :
    AbstractAuthenticationToken(AuthorityUtils.NO_AUTHORITIES) {
    constructor(token: String) : this(token, null)

    override fun getCredentials(): Any = token

    override fun getPrincipal(): Any = userDetails!!


}