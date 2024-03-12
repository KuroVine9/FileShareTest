package com.kuro9.fileshare.controller

import com.kuro9.fileshare.annotation.GetSession
import com.kuro9.fileshare.entity.Session
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import java.io.File

@Controller
@RequestMapping("files/user")
class UserDirectoryController {
    private val logger = LoggerFactory.getLogger(this::class.java)
    private val rootPath = System.getProperty("user.home") + "/Share"
    @GetMapping
    fun userPage(@GetSession user: Session) {
        val userId = user.discordId
        val userHome = File(rootPath, userId)

        if (!userHome.exists()) {
            logger.info("not exist user home")
            userHome.mkdir()
        }



    }
}