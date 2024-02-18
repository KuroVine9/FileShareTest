package com.kuro9.fileshare.config

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "app")
data class AppConfig(
    val general: GeneralConfig,
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

    data class GeneralConfig(
        val domain: String
    )
}