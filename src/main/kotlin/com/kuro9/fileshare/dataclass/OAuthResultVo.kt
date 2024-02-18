package com.kuro9.fileshare.dataclass

import lombok.Data

@Data
data class OAuthResultVo(
    val access_token: String,
    val token_type: String,
    val expires_in: Long,
    val scope: String
)
