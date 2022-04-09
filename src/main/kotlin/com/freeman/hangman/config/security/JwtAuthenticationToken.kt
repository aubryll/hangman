package com.freeman.hangman.config.security

import org.springframework.security.authentication.AbstractAuthenticationToken
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.UserDetails

class JwtAuthenticationToken(
    private val token: String,
    private val userDetails: UserDetails?,
    authority: Collection<GrantedAuthority>
) :
    AbstractAuthenticationToken(authority) {

    init {
        super.setAuthenticated(true)
    }

    override fun getCredentials(): Any = token

    override fun getPrincipal(): Any = userDetails!!


}