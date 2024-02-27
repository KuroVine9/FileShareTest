package com.kuro9.fileshare.service

import com.kuro9.fileshare.entity.Session
import com.kuro9.fileshare.entity.json.DiscordUserVo
import com.kuro9.fileshare.entity.json.OAuthResultVo
import com.kuro9.fileshare.exception.NotAuthorizedException
import com.kuro9.fileshare.exception.TokenNotValidException
import com.kuro9.fileshare.repository.SessionRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.util.*

@Service
class SessionService(
    private val sessionRepo: SessionRepository,
    private val oAuth: OAuthApiService
) {
    private val log = LoggerFactory.getLogger(this::class.java)


    fun createSession(oauthCode: String): Session {
        val authResult = oAuth.getToken(oauthCode) ?: throw NotAuthorizedException("Not Logged In")
        val discordUserInfo =
            oAuth.getUserInfo(authResult.access_token) ?: throw TokenNotValidException("Token is not valid")

        return sessionRepo.save(sessionMapper(authResult, discordUserInfo))
    }


    fun getSession(sessionId: String): Session {
        val session = sessionRepo.findById(sessionId).orElseThrow {
            NotAuthorizedException("Not Logged In-not found")
        }

        log.info("check create time")
        if (session.createdAt.plusSeconds(604800L).isBefore(LocalDateTime.now())) {
            sessionRepo.delete(session)
            throw NotAuthorizedException("Not Logged In")
        }
        return session
    }

    fun sessionMapper(authResult: OAuthResultVo, discordUserInfo: DiscordUserVo): Session = Session(
        sessionId = UUID.randomUUID().toString(),
        token = authResult.access_token,
        discordId = discordUserInfo.id,
        username = discordUserInfo.username,
        discriminator = discordUserInfo.discriminator,
        createdAt = LocalDateTime.now()
    )
}