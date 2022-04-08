package com.freeman.hangman.controller.user

import com.freeman.hangman.controller.base.BaseControllerImpl
import com.freeman.hangman.domain.dto.UserDto
import com.freeman.hangman.domain.model.User
import com.freeman.hangman.service.user.IUserService
import org.springframework.context.annotation.Lazy
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

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