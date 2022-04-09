package com.freeman.hangman.config.security


import com.freeman.hangman.domain.dto.UserDto
import io.jsonwebtoken.Claims
import io.jsonwebtoken.JwtParser
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Component
import java.time.ZonedDateTime
import java.util.*
import javax.crypto.SecretKey

@Component
class JWTUtil(private val secretKey: SecretKey = Keys.secretKeyFor(SignatureAlgorithm.HS256)) {


    @Value("\${com.freeman.token.access-expire}")
    lateinit var accessTokenExpire: String

    @Value("\${com.freeman.token.refresh-expire}")
    lateinit var refreshTokenExpire: String

    @Bean
    fun passwordEncoder(): BCryptPasswordEncoder? {
        return BCryptPasswordEncoder()
    }

    fun encode(userDto: UserDto): String {
        val accessExpInSecs: Long = accessTokenExpire.toLong()
        val currentTime = ZonedDateTime.now()
        return Jwts.builder()
            .setSubject(userDto.email)
            .setExpiration(
                Date.from(currentTime.plusSeconds(accessExpInSecs).toInstant())
            )
            .signWith(secretKey)
            .compact()

    }

    fun getParser(): JwtParser {
        return Jwts.parserBuilder().setSigningKey(secretKey).build()
    }

    fun getAllClaims(jws: String?): Claims {
        return getParser().parseClaimsJws(jws).body
    }

    fun getSubject(jws: String?): String? {
        return getAllClaims(jws).subject
    }

    fun getId(jws: String?): String? {
        return getAllClaims(jws).id
    }

    fun verify(jws: String?): Boolean {
        return !getAllClaims(jws).expiration.before(Date())
    }

}