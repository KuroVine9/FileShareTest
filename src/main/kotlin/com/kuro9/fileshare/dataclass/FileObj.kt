package com.kuro9.fileshare.dataclass

data class FileObj(
    val name: String,
    val size: Long,
    val isDir: Boolean
)
