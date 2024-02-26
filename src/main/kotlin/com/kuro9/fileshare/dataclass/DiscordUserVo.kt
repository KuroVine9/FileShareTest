package com.kuro9.fileshare.dataclass

import lombok.Data
import org.springframework.data.annotation.Id
import org.springframework.data.redis.core.RedisHash
import java.util.*

@Data
@RedisHash(value = "discord_user", timeToLive = 604800)
data class DiscordUserVo(
    val id: String,
    val username: String,
    val discriminator: String,

    @Id val sessionId: String = "kuro" + UUID.randomUUID().toString()
) : java.io.Serializable