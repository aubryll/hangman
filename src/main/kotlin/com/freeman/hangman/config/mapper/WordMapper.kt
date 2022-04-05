package com.freeman.hangman.config.mapper

import com.freeman.hangman.config.mapper.base.GenericMapper
import com.freeman.hangman.domain.dto.WordDto
import com.freeman.hangman.domain.model.Word
import org.mapstruct.Mapper

@Mapper(componentModel = "spring")
interface WordMapper: GenericMapper<Word,WordDto>