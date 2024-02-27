package com.kuro9.fileshare.controller

import com.kuro9.fileshare.config.AppConfig
import com.kuro9.fileshare.service.OAuthApiService
import com.kuro9.fileshare.service.SessionService
import jakarta.servlet.http.Cookie
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
    val oAuthApiService: OAuthApiService,
    val sessionService: SessionService
) {
    private val logger: Logger = LoggerFactory.getLogger(this::class.java)

    @GetMapping("login")
    fun login(): ResponseEntity<*> {
        logger.info("redirect to {}", appConfig.oauth.requestUri)
        val header = HttpHeaders().apply {
            location = URI.create(appConfig.oauth.requestUri)
        }
        return ResponseEntity<Any>(header, HttpStatus.MOVED_PERMANENTLY)
    }

    @GetMapping("/redirect")
    @PostMapping("/redirect")
    fun redirect(@RequestParam code: String, response: HttpServletResponse): ModelAndView {
        val mav = ModelAndView("error/StandardErrorPage")
        logger.info("code={}", code)
        val result = runCatching { sessionService.createSession(code) }
            .getOrElse {
                logger.error("discord auth failed", it)
                return mav.apply {
                    addObject("title", "OAuth 인증 실패")
                    addObject("subTitle", "OAuth 인증에 실패했습니다.")
                    addObject("description", "OAuth 인증에 실패했습니다.")
                }
            }

        logger.info("result={}", result)
        response.addHeader("Authorization", result.sessionId)
        val cookie = Cookie("auth_code", result.sessionId)
        cookie.domain = appConfig.general.domain
        cookie.path = "/"
        cookie.maxAge = 604800
        cookie.secure = true
        response.addCookie(cookie)
        return mav.apply {
            viewName = "redirect:/share/files"
        }
    }
}