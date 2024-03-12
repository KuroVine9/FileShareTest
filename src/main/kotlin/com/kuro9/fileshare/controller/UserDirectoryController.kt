package com.kuro9.fileshare.controller

import com.kuro9.fileshare.annotation.GetSession
import com.kuro9.fileshare.entity.FileInfo
import com.kuro9.fileshare.entity.Session
import com.kuro9.fileshare.entity.vo.FileObj
import com.kuro9.fileshare.service.FileManageService
import jakarta.transaction.Transactional
import org.slf4j.LoggerFactory
import org.springframework.core.io.UrlResource
import org.springframework.http.ContentDisposition
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.util.StringUtils
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import org.springframework.web.servlet.ModelAndView
import java.io.File

@Controller
@RequestMapping("files/user")
class UserDirectoryController(
    private val fileService: FileManageService
) {
    private val logger = LoggerFactory.getLogger(this::class.java)
    private val rootPath = System.getProperty("user.home") + "/Share"

    @GetMapping
    @Transactional
    fun userPage(
        @GetSession user: Session,
        @RequestParam(required = false, defaultValue = "/") path: String
    ): ModelAndView {
        val page = ModelAndView("userHome").apply {
            addObject("userName", user.username)
        }

        val userId = user.discordId
        val userHome = File(rootPath, userId)

        if (!userHome.exists()) {
            logger.info("not exist user home")
            userHome.mkdir()

            return page.addObject("fileInfoList", emptyList<FileObj>())
        }

        var pathStr = StringUtils.cleanPath(path)
        if (!pathStr.startsWith("/")) pathStr = "/$pathStr"
        val fileList = fileService.getFileList("/$userId$pathStr")
        page.addObject("fileInfoList", fileList.map { FileObj.toFileObj(it) })

        return page
    }
 
    /**
     * ex)userId = 112233
     * path := /path/to/upload
     * file -> fileName = "test.jpg"
     * fullPath = /112233/path/to/upload/test.jpg
     */
    @PutMapping("upload")
    @ResponseBody
    @Transactional
    fun uploadFile(
        @GetSession user: Session,
        @RequestParam("path") path: String,
        @RequestParam("file") file: MultipartFile
    ): ResponseEntity<*> {
        val fileName = file.name
        logger.info("fileName={} fileOrigName={}", file.name, file.originalFilename)
        val nameReg = Regex("^[A-Za-z0-9\\-_+=\\[\\]]{1,64}$")
        val matchResult = nameReg.matches(fileName)
        if (!matchResult) return ResponseEntity<String>("FILENAME_ERR", HttpStatus.BAD_REQUEST)

        if (!pathCheck(path)) return ResponseEntity<String>("PATH_ERR", HttpStatus.BAD_REQUEST)

        //TODO 용량체크

        if (file.isEmpty) return ResponseEntity<String>("FILE_EMPTY_ERR", HttpStatus.BAD_REQUEST)
        if (fileService.isFileExist("/${user.discordId}$path", file.name)) {
            return ResponseEntity<String>("DUP_FILE_ERR", HttpStatus.CONFLICT)
        }
        val toUpload = File("$rootPath/${user.discordId}$path", file.name)
        if (!toUpload.parentFile.exists()) return ResponseEntity<String>("PARENT_NOT_EXIST_ERR", HttpStatus.FORBIDDEN)
        file.transferTo(toUpload)
        fileService.saveFile(toUpload, user)

        return ResponseEntity.ok("OK")
    }

    @GetMapping("download")
    @ResponseBody
    fun downloadFile(
        @GetSession user: Session,
        @RequestParam("path") path: String
    ): ResponseEntity<*> {
        if (!fileService.isUserAccessible(StringUtils.cleanPath(path), user))
            return ResponseEntity<Any>(HttpStatus.FORBIDDEN)
        val fileInfo = fileService.getFile(path) ?: return ResponseEntity<Any>(HttpStatus.NOT_FOUND)
        val file = File(rootPath, fileInfo.fullPath)
        if (!file.exists()) return ResponseEntity<Any>(HttpStatus.NOT_FOUND)
        val resource = UrlResource("file:${file.canonicalPath}")
        val header = HttpHeaders().apply {
            contentDisposition = ContentDisposition.builder("attachment").filename(fileInfo.fileName).build()
        }
        return ResponseEntity(resource, header, HttpStatus.OK)
    }


    private fun pathCheck(path: String): Boolean {
        if (!path.startsWith("/")) return false
        if (!path.endsWith("/")) return false
        if (path.contains("..")) return false
        val nameReg = Regex("""^[A-Za-z0-9\-_+=]{1,20}""")
        return path.split("/").all { nameReg.matches(it) }
    }
}