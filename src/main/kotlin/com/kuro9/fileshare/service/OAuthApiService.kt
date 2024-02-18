package com.kuro9.fileshare.service

import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient

@Service
class OAuthApiService {

    fun get() {
        WebClient.create("")
            .post()
            .body()
    }
}