package com.freeman.hangman.config.security

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.authentication.ReactiveAuthenticationManager
import org.springframework.security.authentication.UserDetailsRepositoryReactiveAuthenticationManager
import org.springframework.security.core.AuthenticationException
import org.springframework.security.core.userdetails.ReactiveUserDetailsService
import org.springframework.security.web.server.WebFilterExchange
import org.springframework.security.web.server.authentication.AuthenticationWebFilter
import org.springframework.security.web.server.authentication.WebFilterChainServerAuthenticationSuccessHandler
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatchers
import reactor.core.publisher.Mono


@Configuration
class JwtFilterConfig(
    private val userDetailsService: ReactiveUserDetailsService,
    val jwtReactiveJwtAuthenticationManager: JwtAuthenticationManager,
    val jwtAuthenticationConverter: JwtAuthenticationConverter
) {

    @Bean
    fun jwtAuthenticationWebFilter(): AuthenticationWebFilter {
        val filter = AuthenticationWebFilter(jwtReactiveJwtAuthenticationManager)
        filter.setServerAuthenticationConverter(jwtAuthenticationConverter)
        filter.setAuthenticationSuccessHandler(WebFilterChainServerAuthenticationSuccessHandler())
        filter.setAuthenticationFailureHandler { _: WebFilterExchange?, _: AuthenticationException? ->
            Mono.error(
                BadCredentialsException("Wrong authentication token")
            )
        }
        filter.setRequiresAuthenticationMatcher(ServerWebExchangeMatchers.pathMatchers("/resource/**"))
        return filter
    }

    @Bean
    @Primary
    fun userDetailsRepositoryReactiveAuthenticationManager(): ReactiveAuthenticationManager {
        return UserDetailsRepositoryReactiveAuthenticationManager(userDetailsService)
    }

}
