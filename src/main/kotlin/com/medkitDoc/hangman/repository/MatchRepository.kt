package com.medkitDoc.hangman.repository

import com.medkitDoc.hangman.domain.model.Match
import com.medkitDoc.hangman.repository.base.BaseRepository
import org.springframework.stereotype.Repository

@Repository
interface MatchRepository: BaseRepository<Match>