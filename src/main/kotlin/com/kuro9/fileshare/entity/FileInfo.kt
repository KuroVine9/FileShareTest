package com.kuro9.fileshare.entity

import com.kuro9.fileshare.repository.FileInfoRepository
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.Id
import org.springframework.data.annotation.CreatedDate
import java.io.File
import java.time.LocalDateTime

@Entity
class FileInfo(
    @Id val fullPath: String, // /{userId}/path/to/anything/obj.txt
    val parentPath: String,   // /{userId}/path/to/anything
    val fileName: String,     // obj.txt
    val fileSize: Long,
    val isDir: Boolean,
    val ownerId: String,
    @CreatedDate val createdAt: LocalDateTime
) {
    companion object {
        fun toFileInfo(file: File, ownerId: String): FileInfo {
            val fullPath = file.canonicalPath.split("Share")[1]
            val parentPath = fullPath.substring(0 until fullPath.indexOfLast { it == '/' })
            val fileName = file.name
            val fileSize = file.length()
            val isDir = file.isDirectory

            return FileInfo(
                fullPath, parentPath, fileName, fileSize, isDir, ownerId, LocalDateTime.now()
            )
        }
    }
}