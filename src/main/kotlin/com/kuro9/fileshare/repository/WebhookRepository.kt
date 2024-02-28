package com.kuro9.fileshare.repository

import com.kuro9.fileshare.entity.ErrorWebhook
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface WebhookRepository : JpaRepository<ErrorWebhook, Long>