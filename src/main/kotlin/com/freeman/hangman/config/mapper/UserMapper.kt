package com.freeman.hangman.config.mapper

import com.freeman.hangman.config.mapper.base.GenericMapper
import com.freeman.hangman.domain.dto.UserDto
import com.freeman.hangman.domain.model.User
import org.mapstruct.Mapper
import org.mapstruct.ReportingPolicy

@Mapper(componentModel = "spring", unmappedSourcePolicy = ReportingPolicy.IGNORE)
interface UserMapper : GenericMapper<User, UserDto>