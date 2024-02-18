package com.kuro9.fileshare.controller

import com.kuro9.fileshare.config.AppConfig
import com.kuro9.fileshare.service.OAuthApiService
import jakarta.servlet.http.Cookie
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.servlet.ModelAndView
import java.net.URI

@Controller
@RequestMapping("oauth")
class OAuthController(
    val appConfig: AppConfig,
    val oAuthApiService: OAuthApiService
) {
    private val logger: Logger = LoggerFactory.getLogger(this::class.java)

    @GetMapping("login")
    fun login(): ResponseEntity<*> {
        val header = HttpHeaders().apply {
            location = URI.create(appConfig.oauth.requestUrl)
        }
        return ResponseEntity<Any>(header, HttpStatus.MOVED_PERMANENTLY)
    }

    @GetMapping("/redirect")
    @PostMapping("/redirect")
    fun redirect(@RequestParam code: String, request: HttpServletRequest ,response: HttpServletResponse): ModelAndView {
        val mav = ModelAndView("error/StandardErrorPage")
        logger.info("code={}", code)
        val result = oAuthApiService.getToken(code) ?: return mav.apply {
            addObject("title", "OAuth 인증 실패")
            addObject("subTitle", "OAuth 인증에 실패했습니다.")
            addObject("description", "OAuth 인증에 실패했습니다.")
        }

        logger.info("result={}", result)
        val cookie = Cookie("access_token", result.access_token)
        cookie.domain = "localhost"
        cookie.path = "/"
        cookie.maxAge = if (result.expires_in >= Int.MAX_VALUE.toLong()) Int.MAX_VALUE else result.expires_in.toInt()
        cookie.secure = true
        response.addCookie(cookie)

        return mav.apply {
            viewName = "redirect:/share/files"
        }
    }
}