package com.kuro9.fileshare.entity.vo

import com.kuro9.fileshare.entity.FileInfo
import com.kuro9.fileshare.utils.humanReadableByteCountSI
import java.time.LocalDateTime

data class FileObj(
    val name: String,
    val size: Long,
    val sizeStr: String,
    val isDir: Boolean,
    val createdAt: LocalDateTime
) {
    companion object {
        fun toFileObj(fileInfo: FileInfo): FileObj {
            return FileObj(
                fileInfo.fileName,
                fileInfo.fileSize,
                humanReadableByteCountSI(fileInfo.fileSize),
                fileInfo.isDir,
                fileInfo.createdAt
            )
        }
    }
}
