package com.freeman.hangman.config.mapper

import com.freeman.hangman.config.mapper.base.GenericMapper
import com.freeman.hangman.domain.dto.MatchDto
import com.freeman.hangman.domain.model.Match
import org.mapstruct.Mapper
import org.mapstruct.ReportingPolicy

@Mapper(componentModel = "spring", unmappedSourcePolicy = ReportingPolicy.IGNORE)
interface MatchMapper : GenericMapper<Match, MatchDto>