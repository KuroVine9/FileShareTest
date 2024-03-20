package com.kuro9.fileshare.entity.json

data class OAuthRequestVo(
    val client_id: String,
    val client_secret: String,
    val grant_type: String,
    val code: String,
    val redirect_uri: String,
    val scope: String
)
