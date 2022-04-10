package com.freeman.hangman.util

import org.mockito.Mockito

class Utils {

    companion object {
        fun <T> any(type: Class<T>): T = Mockito.any(type)
    }

}