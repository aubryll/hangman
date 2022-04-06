package com.freeman.hangman.service.user

import com.freeman.hangman.config.mapper.UserMapper
import com.freeman.hangman.domain.dto.UserDto
import com.freeman.hangman.domain.model.User
import com.freeman.hangman.repository.UserRepository
import com.freeman.hangman.service.base.BaseServiceImpl
import org.mapstruct.factory.Mappers
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class UserServiceImpl(
    mapper: UserMapper = Mappers.getMapper(UserMapper::class.java),
) : BaseServiceImpl<User, UserDto, UserRepository, UserMapper>(mapper), IUserService {

    @Autowired
    lateinit var repo: UserRepository

    override fun getRepository(): UserRepository {
        return repo
    }

    override fun findByUsername(username: String): Mono<User> = repo.findByUsername(username)

    override fun copy(original: User, update: User): User {
        return update.copy(updatedAt = original.updatedAt, createdAt = original.createdAt)
    }


}