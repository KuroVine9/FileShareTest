package com.kuro9.fileshare.entity

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.Id
import org.springframework.data.annotation.CreatedDate
import java.io.File
import java.time.LocalDateTime

@Entity
class FileInfo (
    @Id val fullPath: String,
    val parentPath: String,
    val ownerId: String,
    @CreatedDate val createdAt: LocalDateTime
)