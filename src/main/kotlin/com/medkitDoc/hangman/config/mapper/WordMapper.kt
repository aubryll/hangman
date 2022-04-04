package com.medkitDoc.hangman.config.mapper

import com.medkitDoc.hangman.config.mapper.base.GenericMapper
import com.medkitDoc.hangman.domain.dto.WordDto
import com.medkitDoc.hangman.domain.model.Word
import org.mapstruct.Mapper

@Mapper(componentModel = "spring")
interface WordMapper: GenericMapper<Word,WordDto>