package com.medkitDoc.hangman.repository

import com.medkitDoc.hangman.domain.model.Word
import com.medkitDoc.hangman.repository.base.BaseRepository
import org.springframework.stereotype.Repository

@Repository
interface WordRepository: BaseRepository<Word>