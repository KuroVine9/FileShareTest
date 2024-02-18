package com.kuro9.fileshare.dataclass

data class OAuthResultVo(
    val access_token: String,
    val token_type: String,
    val expires_in: Long,
    val refresh_token: String,
    val scope: String
)
