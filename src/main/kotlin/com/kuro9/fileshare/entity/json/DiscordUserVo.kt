package com.kuro9.fileshare.entity.json

import lombok.Data

@Data
data class DiscordUserVo(
    val id: String,
    val username: String,
    val discriminator: String
) : java.io.Serializable