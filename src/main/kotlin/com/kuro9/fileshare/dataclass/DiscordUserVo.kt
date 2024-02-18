package com.kuro9.fileshare.dataclass

import lombok.Data

@Data
data class DiscordUserVo(
    val id: String,
    val username: String,
    val discriminator: String,
)