package com.freeman.hangman.config.security

import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.authentication.ReactiveAuthenticationManager
import org.springframework.security.core.Authentication
import org.springframework.security.core.userdetails.ReactiveUserDetailsService
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono

@Component
class JwtAuthenticationManager(
    val jwtUtil: JWTUtil,
    val userDetailsService: ReactiveUserDetailsService
) : ReactiveAuthenticationManager {

    override fun authenticate(authentication: Authentication): Mono<Authentication?>? {
        val token = authentication.credentials as String
        val username: String? = try {
            if (!jwtUtil.verify(token)) throw Exception()
            jwtUtil.getSubject(token)
        } catch (e: Exception) {
            return Mono.error(BadCredentialsException("invalid credentials"))
        }
        return userDetailsService.findByUsername(username)
            .switchIfEmpty(Mono.error(BadCredentialsException("invalid credentials")))
            .map { userDetails: UserDetails ->
                JwtAuthenticationToken(
                    token,
                    userDetails,
                )
            }
    }
}
