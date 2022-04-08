package com.freeman.hangman.service.user

import com.freeman.hangman.config.mapper.UserMapper
import com.freeman.hangman.domain.dto.APIResponse
import com.freeman.hangman.domain.dto.UserDto
import com.freeman.hangman.domain.model.User
import com.freeman.hangman.repository.UserRepository
import com.freeman.hangman.service.base.BaseServiceImpl
import org.mapstruct.factory.Mappers
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class UserServiceImpl(
    mapper: UserMapper = Mappers.getMapper(UserMapper::class.java),
) :
    BaseServiceImpl<User, UserDto, UserRepository, UserMapper>(mapper), IUserService {

    @Autowired
    lateinit var userRepository: UserRepository

    @Autowired
    lateinit var passwordEncoder: PasswordEncoder


    override fun findByUsername(username: String): Mono<UserDetails> =
        userRepository.findByEmail(username).map { t -> User.build(t) }

    override fun getRepository(): UserRepository = userRepository

    override fun copy(original: User, update: User): User {
        return update.copy(updatedAt = original.updatedAt, createdAt = original.createdAt)
    }

    override fun create(v: UserDto): Mono<ResponseEntity<APIResponse>> {
        return userRepository.findByEmail(v.email)
            .map {
                ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(APIResponse(status = HttpStatus.BAD_REQUEST, message = "User already exists"))
            }
            .switchIfEmpty(Mono.defer { super.create(v.copy(password = passwordEncoder.encode(v.password))) })
    }
}