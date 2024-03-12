package com.kuro9.fileshare.entity

import jakarta.persistence.AttributeConverter
import jakarta.persistence.Converter
import jakarta.persistence.Embeddable
import jakarta.persistence.EmbeddedId
import jakarta.persistence.Entity
import org.springframework.data.annotation.CreatedDate
import java.io.Serializable
import java.time.LocalDateTime

@Entity
class FileAuth (
    @EmbeddedId val id: FileAuthId,
    val type: Type,
    @CreatedDate val createdAt: LocalDateTime
) {
    enum class Type(val value: Int) {
        INCLUDE(1), EXCLUDE(2)
    }
}

@Embeddable
data class FileAuthId (
    val filePath: String,
    val userId: String
) : Serializable

@Converter(autoApply = true)
class OrderStateConverter : AttributeConverter<FileAuth.Type, Int> {
    override fun convertToDatabaseColumn(attribute: FileAuth.Type): Int = attribute.value
    override fun convertToEntityAttribute(dbData: Int): FileAuth.Type =
        FileAuth.Type.entries.first { it.value == dbData }
}