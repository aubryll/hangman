package com.freeman.hangman.service.word

import com.freeman.hangman.domain.dto.WordDto
import com.freeman.hangman.domain.model.Word
import com.freeman.hangman.service.base.IBaseService

interface IWordService: IBaseService<Word, WordDto>