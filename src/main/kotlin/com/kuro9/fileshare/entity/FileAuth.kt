package com.kuro9.fileshare.entity

import jakarta.persistence.*
import org.springframework.data.annotation.CreatedDate
import java.io.Serializable
import java.time.LocalDateTime

@Entity
class FileAuth(
    @EmbeddedId val id: FileAuthId,
    val type: Type,
    @CreatedDate val createdAt: LocalDateTime
) {
    enum class Type(val value: Int) {
        READ(1), WRITE(2), RW(3), NONE(0);

        fun isReadable() = this in listOf(READ, RW)
        fun isWritable() = this in listOf(WRITE, RW)
        fun isFullAccess() = this == RW
    }
}

@Embeddable
data class FileAuthId(
    val filePath: String,
    val userId: String
) : Serializable

@Converter(autoApply = true)
class OrderStateConverter : AttributeConverter<FileAuth.Type, Int> {
    override fun convertToDatabaseColumn(attribute: FileAuth.Type): Int = attribute.value
    override fun convertToEntityAttribute(dbData: Int): FileAuth.Type =
        FileAuth.Type.entries.first { it.value == dbData }
}