package com.kuro9.fileshare.utils

import org.springframework.util.StringUtils
import java.io.File
import java.text.CharacterIterator
import java.text.StringCharacterIterator

fun humanReadableByteCountSI(bytes: Long): String {
    var bytes = bytes
    if (-1000 < bytes && bytes < 1000) {
        return "$bytes B"
    }
    val ci: CharacterIterator = StringCharacterIterator("kMGTPE")
    while (bytes <= -999950 || bytes >= 999950) {
        bytes /= 1000
        ci.next()
    }
    return java.lang.String.format("%.1f %cB", bytes / 1000.0, ci.current())
}

fun pathCheck(path: String): Boolean {
    if (path.contains("..") || path.contains("//")) return false
    val safePath = StringUtils.cleanPath(path)
    return safePath.split("/").filter { it.isNotEmpty() }.all { nameCheck(it) }
}

fun nameCheck(name: String): Boolean {
    if (name.contains("..") || name.contains("/")) return false
    val nameReg = Regex("^[A-Za-z0-9\\-_+=\\[\\]\\s.]{1,64}$")
    return nameReg.matches(name)
}

fun toDbPath(file: File): String {
    val a = StringUtils.cleanPath(file.canonicalPath).split("Share")
    if (a.size < 2 || a[1].isEmpty()) throw IllegalArgumentException("Not valid file")
    return a[1]
}