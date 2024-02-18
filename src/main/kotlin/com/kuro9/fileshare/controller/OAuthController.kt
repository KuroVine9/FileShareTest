package com.kuro9.fileshare.controller

import com.kuro9.fileshare.dataclass.OAuthResultVo
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam

@Controller
@RequestMapping("oauth")
class OAuthController {
    private val logger: Logger = LoggerFactory.getLogger(this::class.java)

    @GetMapping("login")
    fun login() {
        val url = "https://discord.com/api/oauth2/authorize?client_id=1208595092487540736&response_type=code&redirect_uri=http%3A%2F%2Flocalhost%3A3000%2Foauth%2Fredirect&scope=identify+email+guilds"

    }

    @GetMapping("/redirect")
    @PostMapping("/redirect")
    fun redirect(@RequestParam code: String): ResponseEntity<String> {
        logger.info("code={}", code)
        return ResponseEntity.ok().build<String>()
    }
}