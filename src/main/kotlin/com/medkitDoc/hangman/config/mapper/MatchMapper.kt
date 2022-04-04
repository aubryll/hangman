package com.medkitDoc.hangman.config.mapper

import com.medkitDoc.hangman.config.mapper.base.GenericMapper
import com.medkitDoc.hangman.domain.dto.MatchDto
import com.medkitDoc.hangman.domain.model.Match
import org.mapstruct.Mapper

@Mapper(componentModel = "spring")
interface MatchMapper: GenericMapper<Match,MatchDto>