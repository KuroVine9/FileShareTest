package com.kuro9.fileshare.entity

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.Id
import org.hibernate.annotations.GenericGenerator
import org.springframework.data.annotation.CreatedDate
import java.time.LocalDateTime
import java.util.*

@Entity
class Session(
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "org.hibernate.id.UUIDGenerator")
    @Id val sessionId: String,

    val token: String,

    val discordId: String,
    val username: String,
    val discriminator: String,

    @CreatedDate val createdAt: LocalDateTime
) {
    constructor(
        token: String,
        discordId: String,
        username: String,
        discriminator: String
    ) : this(
        UUID.randomUUID().toString(),
        token,
        discordId,
        username,
        discriminator,
        LocalDateTime.now()
    )

    override fun toString(): String {
        return "Session(sessionId='$sessionId', token='$token', discordId='$discordId', username='$username', discriminator='$discriminator', createdAt=$createdAt)"
    }
}