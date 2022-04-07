package com.freeman.hangman.controller.user

import com.freeman.hangman.config.security.JWTUtil
import com.freeman.hangman.domain.dto.APIResponse
import com.freeman.hangman.domain.dto.LoginRequest
import com.freeman.hangman.service.user.IUserService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.authentication.ReactiveAuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.ReactiveSecurityContextHolder
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Mono

@RestController
@RequestMapping(value = ["\${com.freeman.url}/auth"])
class AuthController {

    @Autowired
    lateinit var authenticationManager: ReactiveAuthenticationManager

    @Autowired
    lateinit var jwtUtil: JWTUtil

    @Autowired
    lateinit var userService: IUserService


    @PostMapping("/signin")
    fun authenticateUser(@RequestBody loginRequest: LoginRequest): Mono<ResponseEntity<APIResponse>> {
        userService.findByUsername(loginRequest.email)

    }


}