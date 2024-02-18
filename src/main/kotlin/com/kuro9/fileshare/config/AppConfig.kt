package com.kuro9.fileshare.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.context.properties.bind.ConstructorBinding
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@ConfigurationProperties(prefix = "app")
data class AppConfig(
    val oauth: OAuthConfig
) {
    data class OAuthConfig(
        val requestUrl: String,
        val clientId: String,
        val clientSecret: String,
        val grantType: String,
        val redirectUri: String,
        val scope: String
    )
}