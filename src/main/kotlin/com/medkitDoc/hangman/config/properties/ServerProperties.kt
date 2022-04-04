package com.medkitDoc.hangman.config.properties

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration

@Configuration
@ConfigurationProperties(prefix = "com.medkit-doc.server")
class ServerProperties {
    lateinit var url: String
    lateinit var port: String
}