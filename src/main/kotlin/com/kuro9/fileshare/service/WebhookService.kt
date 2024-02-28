package com.kuro9.fileshare.service

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.kuro9.fileshare.repository.WebhookRepository
import jakarta.servlet.http.HttpServletRequest
import org.slf4j.LoggerFactory
import org.springframework.http.RequestEntity
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate

@Service
class WebhookService(
    private val webhookRepo: WebhookRepository
) {
    private val logger = LoggerFactory.getLogger(this::class.java)
    fun sendWebhook(e: Throwable, request: HttpServletRequest) {
        runCatching {
            val jsonString = toJsonString(e, request)
            logger.info(jsonString)
            webhookRepo.findAll().forEach {
                val req = RequestEntity.post(it.endpoint)
                    .header("Content-Type", "application/json")
                    .body(jsonString)

                val res = RestTemplate().exchange(req, String::class.java)
                logger.info("Webhook Response: {}", res.statusCode)
            }
        }.onFailure { logger.error("Webhook Error", it) }
    }

    companion object ErrorMapper {
        private val mapper = ObjectMapper().apply { registerModule(JavaTimeModule()) }
        fun toJsonString(e: Throwable, request: HttpServletRequest): String {
            val requestUrl = request.requestURL.toString()
            val method = e.stackTrace[0].methodName
            val className = e.stackTrace[0].className
            return mapper.writeValueAsString(
                mapOf(
                    "username" to "ExceptionAlert",
                    "content" to "Exception Occured:\n${className}",
                    "embeds" to listOf(
                        mapOf(
                            "title" to "Request URL:",
                            "description" to requestUrl,
                            "color" to 16711680,
                            "fields" to listOf(
                                mapOf(
                                    "name" to "Message:",
                                    "value" to e.localizedMessage,
                                ),
                                mapOf(
                                    "name" to "Exception Occurred:",
                                    "value" to className,
                                ),
                                mapOf(
                                    "name" to "Method:",
                                    "value" to method,
                                ),
                                mapOf(
                                    "name" to "Line:",
                                    "value" to e.stackTrace[0].lineNumber,
                                ),
                                mapOf(
                                    "name" to "Exception:",
                                    "value" to e.javaClass.name,
                                ),
                                mapOf(
                                    "name" to "Stack Trace:",
                                    "value" to "```${e.stackTrace.joinToString("\n").substring(0..1000)}```",
                                ),
                            )
                        )
                    )
                )
            )
        }
    }
}