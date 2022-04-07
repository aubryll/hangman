package com.freeman.hangman.config.security

import com.freeman.hangman.config.properties.ServerProperties
import org.springframework.context.annotation.Bean
import org.springframework.http.HttpStatus
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity
import org.springframework.security.config.web.server.SecurityWebFiltersOrder
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.core.AuthenticationException
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.web.server.SecurityWebFilterChain
import org.springframework.security.web.server.authentication.AuthenticationWebFilter
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.reactive.CorsConfigurationSource
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono


@EnableWebFluxSecurity
@EnableReactiveMethodSecurity
class WebSecurityConfig(val authWebFilter: AuthenticationWebFilter) {

    @Bean
    fun chain(
        http: ServerHttpSecurity
    ): SecurityWebFilterChain? {
        http.exceptionHandling()
            .authenticationEntryPoint { swe: ServerWebExchange, _: AuthenticationException? ->
                Mono.fromRunnable { swe.response.statusCode = HttpStatus.UNAUTHORIZED }
            }
            .accessDeniedHandler { swe: ServerWebExchange, _: AccessDeniedException? ->
                Mono.fromRunnable { swe.response.statusCode = HttpStatus.FORBIDDEN }
            }.and()
            .csrf().disable()
            .formLogin().disable()
            .authorizeExchange()
            .pathMatchers("/freeman-hangman/users/**").permitAll()
            .pathMatchers("/freeman-hangman/auth/**").permitAll()
            .and()
            //.addFilterAfter(authWebFilter, SecurityWebFiltersOrder.AUTHENTICATION)
        return http
            .cors()
            .configurationSource(createCorsConfigSource())
            .and().build()
    }


    private fun createCorsConfigSource(): CorsConfigurationSource? {
        val source = UrlBasedCorsConfigurationSource()
        val config = CorsConfiguration()
        config.addAllowedOrigin("http://localhost:3000")
        config.allowedMethods = listOf("*")
        config.allowedHeaders = listOf("*")
        source.registerCorsConfiguration("/**", config)
        return source
    }

}