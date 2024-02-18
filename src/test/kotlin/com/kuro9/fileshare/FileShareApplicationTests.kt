package com.kuro9.fileshare

import com.kuro9.fileshare.service.OAuthApiService
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpMethod
import org.springframework.web.reactive.function.client.WebClient

@SpringBootTest
class FileShareApplicationTests {

    @Autowired
    lateinit var oAuthApiService: OAuthApiService

    @Test
    fun tokenTest() {
        val token = "adfadsf"
        val result = oAuthApiService.getUserInfo(token)
        println(result)
    }

    fun getUserInfo(token: String): Map<*, *>? {
        return WebClient.create("https://discord.com/api/users/@me")
            .method(HttpMethod.GET)
            .headers { it.setBearerAuth(token) }
            .retrieve()
            .bodyToMono(Map::class.java)  // Map으로 역직렬화
            .block()
    }
}
