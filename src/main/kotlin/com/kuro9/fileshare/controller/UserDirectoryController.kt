package com.kuro9.fileshare.controller

import com.kuro9.fileshare.annotation.GetSession
import com.kuro9.fileshare.config.AppConfig
import com.kuro9.fileshare.entity.FileAuth
import com.kuro9.fileshare.entity.Session
import com.kuro9.fileshare.entity.vo.FileObj
import com.kuro9.fileshare.entity.vo.MkdirRequest
import com.kuro9.fileshare.service.FileManageService
import jakarta.annotation.PostConstruct
import jakarta.transaction.Transactional
import org.slf4j.LoggerFactory
import org.springframework.boot.SpringApplication
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
import kotlin.system.exitProcess

@Controller
@RequestMapping("files/user")
class UserDirectoryController(
    private val fileService: FileManageService,
    private val appConfig: AppConfig,
    private val context: org.springframework.context.ApplicationContext
) {
    private val logger = LoggerFactory.getLogger(this::class.java)
    private val rootPath = appConfig.general.shareFolderPath + "/Share"

    @PostConstruct
    fun checkRootPath() {
        val root = File(appConfig.general.shareFolderPath)
        if (!root.exists()) {
            logger.error("{} is not exist path,", appConfig.general.shareFolderPath)
            exitProcess(SpringApplication.exit(context))
        }

        val share = File(rootPath)
        if (!share.exists()) {
            logger.info("Maybe First Run or Share folder deleted? creating /Share...")
            share.mkdir()
        }
    }

    @GetMapping
    @Transactional
    fun userPage(
        @GetSession user: Session,
        @RequestParam(required = false, defaultValue = "/") path: String
    ): ModelAndView {
        var pathStr = StringUtils.cleanPath(path)
        if (path.contains("..")) pathStr = "/"
        if (!pathStr.startsWith("/")) pathStr = "/$pathStr"
        if (pathStr.endsWith("/")) pathStr = pathStr.dropLast(1)

        val page = ModelAndView("UserHome").apply {
            addObject("userName", user.username)
            addObject("nowPath", pathStr)
        }

        val userId = user.discordId
        val userHome = File(rootPath, userId)

        if (!userHome.exists()) {
            logger.info("not exist user home")
            userHome.mkdir()

            return page.addObject("fileInfoList", emptyList<FileObj>())
        }

        val fileList = fileService.getFileList("/$userId$pathStr")
        page.addObject("fileInfoList", fileList.map { FileObj.toFileObj(it) })

        return page
    }

    /**
     * ex: userId = 112233
     * path := /path/to/upload
     * file -> fileName = "test.jpg"
     * fullPath = /112233/path/to/upload/test.jpg
     */
    @PostMapping("upload")
    @ResponseBody
    @Transactional
    fun uploadFile(
        @GetSession user: Session,
        @RequestParam("path") path: String,
        @RequestParam("payload") file: MultipartFile?
    ): ResponseEntity<*> {
        if (file == null) return ResponseEntity<String>("PARAM_ERR", HttpStatus.BAD_REQUEST)

        var fileName = file.originalFilename ?: return ResponseEntity<String>("FILE_NAME_ERR", HttpStatus.BAD_REQUEST)
        if (fileName.contains("/")) fileName = fileName.split("/").last()
        logger.info("fileName={} fileOrigName={}", file.name, file.originalFilename)
        val nameReg = Regex("^[A-Za-z0-9\\-_+=\\[\\]\\s.]{1,64}$")
        val matchResult = nameReg.matches(fileName)
        if (!matchResult) return ResponseEntity<String>("FILENAME_ERR", HttpStatus.BAD_REQUEST)

        if (!pathCheck(path)) return ResponseEntity<String>("PATH_ERR", HttpStatus.BAD_REQUEST)

        //TODO 용량체크

        if (file.isEmpty) return ResponseEntity<String>("FILE_EMPTY_ERR", HttpStatus.BAD_REQUEST)
        if (fileService.isFileExist("/${user.discordId}$path", fileName)) {
            return ResponseEntity<String>("DUP_FILE_ERR", HttpStatus.CONFLICT)
        }
        val toUpload = File("$rootPath/${user.discordId}$path", fileName)
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
        if (!fileService.checkUserAccessibility(StringUtils.cleanPath(path), user, FileAuth.Type.READ))
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

    @PostMapping("mkdir")
    @ResponseBody
    fun mkdir(
        @GetSession user: Session,
        @RequestBody body: MkdirRequest
    ): ResponseEntity<String> {
        if (!fileService.checkUserAccessibility(StringUtils.cleanPath(body.path), user, FileAuth.Type.WRITE))
            return ResponseEntity(HttpStatus.FORBIDDEN)

        if (!pathCheck(body.path) || !nameCheck(body.dirName)) return ResponseEntity(HttpStatus.BAD_REQUEST)
        // TODO
        val file = File(rootPath, body.path)
        if (!file.exists()) return ResponseEntity(HttpStatus.NOT_FOUND)

    }


    private fun pathCheck(path: String): Boolean {
        if (path.contains("..") || path.contains("//")) return false
        val safePath = StringUtils.cleanPath(path)
        return safePath.split("/").filter { it.isNotEmpty() }.all { nameCheck(it) }
    }

    private fun nameCheck(name: String): Boolean {
        if (name.contains("..") || name.contains("/")) return false
        val nameReg = Regex("^[A-Za-z0-9\\-_+=\\[\\]\\s.]{1,64}$")
        return nameReg.matches(name)
    }
}