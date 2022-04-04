package com.medkitDoc.hangman.config.server.customizer

import com.medkitDoc.hangman.config.properties.ServerProperties
import org.springframework.boot.web.embedded.netty.NettyReactiveWebServerFactory
import org.springframework.boot.web.embedded.netty.NettyServerCustomizer
import org.springframework.boot.web.server.WebServerFactoryCustomizer
import org.springframework.stereotype.Component
import reactor.netty.http.server.HttpServer

@Component
class NettyWebServerCustomizer(
    private val properties: ServerProperties
): WebServerFactoryCustomizer<NettyReactiveWebServerFactory> {

    override fun customize(factory: NettyReactiveWebServerFactory) {
        factory.addServerCustomizers(PortCustomizer())
    }


    inner class PortCustomizer: NettyServerCustomizer{
        override fun apply(httpServer: HttpServer): HttpServer {
            return httpServer.port(Integer.parseInt(properties.port))
                .wiretap(true)
        }

    }
}