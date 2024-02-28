package com.kuro9.fileshare.entity

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import org.springframework.data.annotation.CreatedDate
import java.time.LocalDateTime

@Entity
class ErrorWebhook(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    val endpoint: String,
    val createdDiscordId: String,
    @CreatedDate val createdAt: LocalDateTime = LocalDateTime.now()
) {
    override fun toString(): String {
        return "ErrorWebhook(id=$id, createdDiscordId='$createdDiscordId', createdAt=$createdAt)"
    }
}