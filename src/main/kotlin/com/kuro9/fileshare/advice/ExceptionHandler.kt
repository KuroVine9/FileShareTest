package com.kuro9.fileshare.advice

import com.kuro9.fileshare.service.WebhookService
import jakarta.servlet.http.HttpServletRequest
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.servlet.ModelAndView
import java.io.IOException

@ControllerAdvice
class ExceptionHandler(
    private val webhookService: WebhookService
) {
    private val logger = LoggerFactory.getLogger(this::class.java)
    @ExceptionHandler(IllegalArgumentException::class)
    fun http400(e: IllegalArgumentException): ModelAndView {
        val modelAndView = ModelAndView("error/StandardErrorPage").apply {
            addObject("title", HttpStatus.BAD_REQUEST.reasonPhrase)
            addObject("subTitle", "입력된 파라미터 값이 잘못되었습니다.")
            addObject("description", "정확한 값을 입력해 주십시오.")
        }
        return modelAndView
    }

    @ExceptionHandler(IOException::class)
    fun ioExcpetion(e: IOException): ModelAndView {
        val modelAndView = ModelAndView("error/StandardErrorPage").apply {
            addObject("title", HttpStatus.NOT_FOUND.reasonPhrase)
            addObject("subTitle", "파일을 읽는 데 문제가 발생했습니다.")
            addObject("description", "올바른 값을 입력하거나 잠시 뒤 시도해 주십시오.")
        }
        return modelAndView
    }

    @ExceptionHandler(Throwable::class)
    fun unknownException(e: Throwable, request: HttpServletRequest): ResponseEntity<Any> {
        logger.error("Error Occurred: ", e)
        webhookService.sendWebhook(e, request)
        
        if (e is Error) throw e
        return ResponseEntity.internalServerError().build()
    }
}