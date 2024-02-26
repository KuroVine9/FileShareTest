package com.kuro9.fileshare.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.cache.annotation.EnableCaching
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory

@Configuration
@EnableCaching
class RedisConfig {
    @Value("\${spring.data.redis.host}")
    lateinit var host: String

    @Value("\${spring.data.redis.port}")
    var port: Int = -1

    @Bean
    fun redisConnectionFactory(): LettuceConnectionFactory {
        return LettuceConnectionFactory(host, port)
    }
}