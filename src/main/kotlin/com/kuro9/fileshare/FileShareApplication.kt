package com.kuro9.fileshare

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.runApplication

@ConfigurationPropertiesScan
@SpringBootApplication
class FileShareApplication

fun main(args: Array<String>) {
    runApplication<FileShareApplication>(*args)
}
