package com.kuro9.fileshare.config

import org.springframework.boot.context.properties.ConfigurationProperties
import java.net.URLEncoder

@ConfigurationProperties(prefix = "app")
data class AppConfig(
    val general: GeneralConfig,
    val oauth: OAuthConfig,
    val webhook: WebhookConfig
) {
    data class OAuthConfig(
        val clientId: String,
        val clientSecret: String,
        val grantType: String,
        val redirectUri: String,
        val scope: String
    ) {
        private var _requestUri: String? = null
        val requestUri: String
            get() =
                _requestUri ?: "https://discord.com/api/oauth2/authorize?client_id=$clientId&redirect_uri=${
                    URLEncoder.encode(
                        redirectUri, "UTF-8"
                    )
                }&response_type=code&scope=$scope".also { _requestUri = it }


    }

    data class GeneralConfig(
        val domain: String
    )

    data class WebhookConfig(
        val endPoints: List<String>
    )
}