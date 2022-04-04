package com.medkitDoc.hangman.config.server.errorHandling

import org.springframework.boot.autoconfigure.web.WebProperties
import org.springframework.boot.autoconfigure.web.reactive.error.AbstractErrorWebExceptionHandler
import org.springframework.boot.web.error.ErrorAttributeOptions
import org.springframework.boot.web.reactive.error.ErrorAttributes
import org.springframework.context.ApplicationContext
import org.springframework.core.annotation.Order
import org.springframework.http.MediaType
import org.springframework.http.codec.ServerCodecConfigurer
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.BodyInserters
import org.springframework.web.reactive.function.server.*
import reactor.core.publisher.Mono


@Component
@Order(-2)
class GlobalErrorWebExceptionHandler(
    g: GlobalErrorAttributes?, applicationContext: ApplicationContext?,
    serverCodecConfigurer: ServerCodecConfigurer
) : AbstractErrorWebExceptionHandler(g, WebProperties.Resources(), applicationContext) {

    init {
        super.setMessageWriters(serverCodecConfigurer.writers)
        super.setMessageReaders(serverCodecConfigurer.readers)
    }


    override fun getRoutingFunction(errorAttributes: ErrorAttributes): RouterFunction<ServerResponse> {
        return RouterFunctions.route(
            RequestPredicates.all()
        ) { request: ServerRequest -> renderErrorResponse(request) }
    }

    private fun renderErrorResponse(request: ServerRequest): Mono<ServerResponse> {
        val error = getErrorAttributes(request, ErrorAttributeOptions.defaults())
        return ServerResponse.status(getHttpStatus(error))
            .contentType(MediaType.APPLICATION_JSON)
            .body(BodyInserters.fromValue(error))
    }

    protected fun getHttpStatus(errorAttributes: Map<String?, Any?>): Int {
        return errorAttributes["status"] as Int
    }
}
