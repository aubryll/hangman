package com.freeman.hangman.config.server.errorHandling

import org.springframework.boot.web.error.ErrorAttributeOptions
import org.springframework.boot.web.reactive.error.DefaultErrorAttributes
import org.springframework.validation.FieldError
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.support.WebExchangeBindException
import org.springframework.web.reactive.function.server.ServerRequest
import java.util.function.Consumer


@ControllerAdvice
class GlobalErrorAttributes : DefaultErrorAttributes() {

    override fun getErrorAttributes(request: ServerRequest, options: ErrorAttributeOptions): MutableMap<String, Any> {
        val map = super.getErrorAttributes(request, options)
        val ex = getError(request)

        if (ex is WebExchangeBindException) {
            ex.bindingResult.fieldErrors.forEach(Consumer { error: FieldError ->
                if (map.containsKey(
                        error.field
                    )
                ) map[error.field] =
                    String.format("%s, %s", map[error.field], error.defaultMessage) else map[error.field] =
                    error.defaultMessage
            })
        } else {
            map["exception"] = ex.javaClass.simpleName
            map["message"] = ex.message
        }
        return map
    }

}