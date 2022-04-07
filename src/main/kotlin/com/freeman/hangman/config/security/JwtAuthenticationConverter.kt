package com.freeman.hangman.config.security

import org.springframework.http.HttpHeaders
import org.springframework.security.core.Authentication
import org.springframework.security.web.server.authentication.ServerAuthenticationConverter
import org.springframework.stereotype.Component
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono


@Component
class JwtAuthenticationConverter : ServerAuthenticationConverter {

    override fun convert(exchange: ServerWebExchange): Mono<Authentication> {
        val authorization = exchange.request
            .headers.getFirst(HttpHeaders.AUTHORIZATION)!!
        return if (!authorization.startsWith("Bearer ")) {
            Mono.empty()
        } else return Mono.just(JwtAuthenticationToken(token = authorization.substring(7)))
    }
}
