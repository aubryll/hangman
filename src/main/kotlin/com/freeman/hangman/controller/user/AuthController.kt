package com.freeman.hangman.controller.user

import com.freeman.hangman.config.mapper.UserMapper
import com.freeman.hangman.config.security.JWTUtil
import com.freeman.hangman.domain.dto.APIResponse
import com.freeman.hangman.domain.dto.LoginRequest
import com.freeman.hangman.domain.model.User
import com.freeman.hangman.service.user.IUserService
import org.mapstruct.factory.Mappers
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.authentication.ReactiveAuthenticationManager
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Mono

@RestController
@RequestMapping(value = ["\${com.freeman.url}/auth"])
class AuthController(val mapper: UserMapper = Mappers.getMapper(UserMapper::class.java)) {

    @Autowired
    lateinit var authenticationManager: ReactiveAuthenticationManager

    @Autowired
    lateinit var jwtUtil: JWTUtil

    @Autowired
    lateinit var userService: IUserService

    @Autowired
    lateinit var passwordEncoder: PasswordEncoder


    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/signin")
    fun authenticateUser(@RequestBody loginRequest: LoginRequest): Mono<ResponseEntity<APIResponse>> {
        return userService.findByUsername(loginRequest.email)
            .map { t ->
                val isValid = passwordEncoder.matches(loginRequest.password, t.password)
                if (isValid) ResponseEntity.status(HttpStatus.OK)
                    .body(
                        APIResponse(
                            status = HttpStatus.OK,
                            payload = jwtUtil.encode(mapper.toDto(t as User))
                        )
                    )
                else
                    incorrectCredentials()

            }.switchIfEmpty(Mono.defer { Mono.just(incorrectCredentials()) })
    }
}

private fun incorrectCredentials(): ResponseEntity<APIResponse> {
    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .body(APIResponse(status = HttpStatus.BAD_REQUEST, message = "Incorrect credentials"))
}
