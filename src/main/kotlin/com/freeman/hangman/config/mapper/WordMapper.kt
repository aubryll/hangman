package com.freeman.hangman.config.mapper

import com.freeman.hangman.config.mapper.base.GenericMapper
import com.freeman.hangman.domain.dto.MatchDto
import com.freeman.hangman.domain.dto.WordDto
import com.freeman.hangman.domain.model.Match
import com.freeman.hangman.domain.model.Word
import org.mapstruct.Mapper
import org.mapstruct.ReportingPolicy

@Mapper(componentModel = "spring", unmappedSourcePolicy = ReportingPolicy.IGNORE)
interface WordMapper: GenericMapper<Word, WordDto>