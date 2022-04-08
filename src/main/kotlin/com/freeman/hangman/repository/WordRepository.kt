package com.freeman.hangman.repository

import com.freeman.hangman.domain.model.Word
import com.freeman.hangman.repository.base.BaseRepository
import org.springframework.data.r2dbc.repository.Query
import org.springframework.stereotype.Repository
import reactor.core.publisher.Mono

@Repository
interface WordRepository : BaseRepository<Word> {

    @Query(
        "SELECT *\n" +
                "FROM   hangman.words,\n" +
                "       (SELECT w.id,\n" +
                "               w.word,\n" +
                "               w.updated_at,\n" +
                "               w.created_at\n" +
                "        FROM   hangman.words w\n" +
                "        WHERE  w.id NOT IN (SELECT m.word_id\n" +
                "                            FROM   hangman.matches m\n" +
                "                            WHERE  m.user_id = :userId)\n" +
                "        ORDER  BY Rand()\n" +
                "        LIMIT  1) r\n" +
                "WHERE  words.id = r.id "
    )
    fun findUniqueWord(userId: Int): Mono<Word>

}