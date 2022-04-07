package com.freeman.hangman.controller.user

import com.freeman.hangman.config.security.JWTUtil
import com.freeman.hangman.config.security.JwtAuthenticationManager
import com.freeman.hangman.controller.base.BaseControllerImpl
import com.freeman.hangman.domain.dto.APIResponse
import com.freeman.hangman.domain.dto.UserDto
import com.freeman.hangman.domain.model.User
import com.freeman.hangman.service.user.IUserService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Lazy
import org.springframework.http.ResponseEntity
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono
import java.util.stream.Collectors

@RestController
@RequestMapping(value = ["\${com.freeman.url}/users"])
class UserControllerImpl(
    @Lazy service: IUserService,
) : BaseControllerImpl<User, UserDto, IUserService>(), IUserController {

    private val service: IUserService

    init {
        this.service = service
    }

    override fun getService(): IUserService {
        return service
    }

}