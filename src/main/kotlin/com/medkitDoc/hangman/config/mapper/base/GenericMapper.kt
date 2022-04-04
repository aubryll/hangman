package com.medkitDoc.hangman.config.mapper.base

interface GenericMapper<S, T> {

    fun toDto(source: S): T
    fun toModel(target: T): S
    fun toDto(sourceList: List<S>): List<T>
    fun toModel(targetList: List<T>): List<S>
}