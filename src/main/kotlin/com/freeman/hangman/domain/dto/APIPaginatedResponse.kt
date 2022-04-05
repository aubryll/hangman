package com.freeman.hangman.domain.dto

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
data class APIPaginatedResponse(
    val pageNumber: Int,
    val pageSize: Int,
    val totalElements: Long,
    val elements: List<*>
) {

    companion object {
        const val FIRST_PAGE: Int = 1
        const val DEFAULT_PAGE_SIZE = 20
    }

    @JsonProperty
    fun totalPages(): Int {
        return if (pageSize > 0) ((totalElements - 1) / pageSize + 1).toInt() else 0
    }

    @JsonProperty
    fun firstPage(): Boolean {
        return pageNumber == FIRST_PAGE
    }

    @JsonProperty
    fun lastPage(): Boolean {
        return (pageNumber + 1) * pageSize >= totalElements
    }


}