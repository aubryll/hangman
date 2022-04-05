package com.freeman.hangman.config.security

import org.springframework.context.annotation.Bean
import org.springframework.core.convert.converter.Converter
import org.springframework.http.HttpStatus
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.authentication.AbstractAuthenticationToken
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.core.AuthenticationException
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.security.web.server.SecurityWebFilterChain
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.reactive.CorsConfigurationSource
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono
import java.util.stream.Collectors
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter
import org.springframework.security.oauth2.server.resource.authentication.ReactiveJwtAuthenticationConverterAdapter


class WebSecurityConfig {

    @Bean
    fun SecurityWebFilterChain(
        http: ServerHttpSecurity
    ): SecurityWebFilterChain? {
        http.exceptionHandling()
            .authenticationEntryPoint { swe: ServerWebExchange, e: AuthenticationException? ->
                Mono.fromRunnable { swe.response.statusCode = HttpStatus.UNAUTHORIZED }
            }
            .accessDeniedHandler { swe: ServerWebExchange, e: AccessDeniedException? ->
                Mono.fromRunnable { swe.response.statusCode = HttpStatus.FORBIDDEN }
            }.and()
            .csrf().disable()
            .formLogin().disable()
            .httpBasic().disable()
            .authorizeExchange()
            .anyExchange().authenticated()
            .and()
            .oauth2ResourceServer()
            .jwt()
            .jwtAuthenticationConverter(grantedAuthoritiesExtractor())
        return http
            .cors()
            .configurationSource(createCorsConfigSource())
            .and().build()
    }

    private fun grantedAuthoritiesExtractor(): Converter<Jwt?, Mono<AbstractAuthenticationToken?>?>? {
        val extractor = JwtAuthenticationConverter()
        extractor.setJwtGrantedAuthoritiesConverter { jwt ->
            val resource: Map<String, Any> = jwt.getClaimAsMap("realm_access")
            val roles =
                resource["roles"] as List<String>?
            roles!!.stream().map { role: String -> SimpleGrantedAuthority(role)
            }.collect(Collectors.toList()).toSet()
        }
        return ReactiveJwtAuthenticationConverterAdapter(extractor)
    }

    private fun createCorsConfigSource(): CorsConfigurationSource? {
        val source = UrlBasedCorsConfigurationSource()
        val config = CorsConfiguration()
        config.addAllowedOrigin("http://localhost:3000")
        config.allowedMethods = java.util.List.of("*")
        config.allowedHeaders = java.util.List.of("*")
        source.registerCorsConfiguration("/**", config)
        return source
    }

}