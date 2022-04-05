package com.freeman.hangman.service.match

import com.freeman.hangman.domain.dto.MatchDto
import com.freeman.hangman.domain.model.Match
import com.freeman.hangman.service.base.IBaseService

interface IMatchService : IBaseService<Match, MatchDto>