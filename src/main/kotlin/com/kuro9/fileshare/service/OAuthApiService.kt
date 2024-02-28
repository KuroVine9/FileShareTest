package com.kuro9.fileshare.service

import com.kuro9.fileshare.config.AppConfig
import com.kuro9.fileshare.entity.json.DiscordUserVo
import com.kuro9.fileshare.entity.json.OAuthResultVo
import lombok.RequiredArgsConstructor
import org.slf4j.LoggerFactory
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.BodyInserters
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.WebClientResponseException
import reactor.core.publisher.Mono

@Service
@RequiredArgsConstructor
class OAuthApiService(val appConfig: AppConfig) {

    private val logger = LoggerFactory.getLogger(this::class.java)
    fun getToken(code: String): OAuthResultVo? {
        return WebClient.create("https://discord.com/api/oauth2/token")
            .method(HttpMethod.POST)
            .header("Content-Type", "application/x-www-form-urlencoded")
            .body(
                BodyInserters.fromFormData("client_id", appConfig.oauth.clientId)
                    .with("client_secret", appConfig.oauth.clientSecret)
                    .with("grant_type", "client_credentials")
                    .with("code", code)
                    .with("redirect_uri", appConfig.oauth.redirectUri)
                    .with("scope", appConfig.oauth.scope)
            )
            .retrieve()
            .bodyToMono(OAuthResultVo::class.java)
            .block()

    }

    fun getUserInfo(token: String): DiscordUserVo? {
        logger.info("token={}", token)
        return WebClient.create("https://discord.com/api/users/@me")
            .method(HttpMethod.GET)
            .headers { it.setBearerAuth(token) }
            .retrieve()
            .bodyToMono(DiscordUserVo::class.java)
            .onErrorResume { error ->
                if (error is WebClientResponseException && error.statusCode == HttpStatus.UNAUTHORIZED) {
                    Mono.empty()
                } else {
                    Mono.error(error)
                }
            }
            .block()
    }
}