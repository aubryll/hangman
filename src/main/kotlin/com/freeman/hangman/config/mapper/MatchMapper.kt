package com.freeman.hangman.config.mapper

import com.freeman.hangman.config.mapper.base.GenericMapper
import com.freeman.hangman.domain.dto.MatchDto
import com.freeman.hangman.domain.model.Match
import org.mapstruct.Mapper

@Mapper(componentModel = "spring")
interface MatchMapper: GenericMapper<Match,MatchDto>