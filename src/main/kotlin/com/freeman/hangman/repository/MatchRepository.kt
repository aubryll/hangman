package com.freeman.hangman.repository

import com.freeman.hangman.domain.model.Match
import com.freeman.hangman.repository.base.BaseRepository
import org.springframework.stereotype.Repository

@Repository
interface MatchRepository: BaseRepository<Match>