package com.freeman.hangman.domain.dto.base

import com.fasterxml.jackson.annotation.JsonProperty

open class BaseDto(open val id: Int, @JsonProperty(access = JsonProperty.Access.READ_ONLY) open val createdAt: String)