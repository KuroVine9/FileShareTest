package com.kuro9.fileshare.dataclass

import lombok.Data
import org.springframework.data.annotation.Id
import org.springframework.data.redis.core.RedisHash
import org.springframework.data.redis.core.TimeToLive

@Data
@RedisHash(value = "oauth_result")
data class OAuthResultVo(
    @Id val access_token: String,
    val token_type: String,
    @TimeToLive val expires_in: Long,
    val scope: String
)
