package com.kuro9.fileshare.controller

import com.kuro9.fileshare.annotation.GetSession
import com.kuro9.fileshare.config.AppConfig
import com.kuro9.fileshare.entity.FileAuth
import com.kuro9.fileshare.entity.Session
import com.kuro9.fileshare.entity.vo.FileObj
import com.kuro9.fileshare.entity.vo.MkdirRequest
import com.kuro9.fileshare.service.FileManageService
import com.kuro9.fileshare.utils.nameCheck
import com.kuro9.fileshare.utils.pathCheck
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
        @RequestParam(required = false) path: String?
    ): ModelAndView {
        var pathStr = StringUtils.cleanPath(path ?: "/${user.discordId}")
        if (pathStr.contains("..")) pathStr = "/${user.discordId}"
        if (!pathStr.startsWith("/")) pathStr = "/${user.discordId}"
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
            fileService.saveFile(userHome, user)

            return page.addObject("fileInfoList", emptyList<FileObj>())
        }

        val fileList = fileService.getFileList(pathStr)
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
    @Transactional(rollbackOn = [Exception::class])
    fun uploadFile(
        @GetSession user: Session,
        @RequestParam("path") path: String,
        @RequestParam("payload") file: MultipartFile?
    ): ResponseEntity<String> {
        if (file == null) return ResponseEntity<String>("PARAM_ERR", HttpStatus.BAD_REQUEST)

        var fileName = file.originalFilename ?: return ResponseEntity<String>("FILE_NAME_ERR", HttpStatus.BAD_REQUEST)
        if (fileName.contains("/")) fileName = fileName.split("/").last()
        logger.info("fileName={} fileOrigName={}", file.name, file.originalFilename)
        if (!nameCheck(fileName)) return ResponseEntity<String>("FILENAME_ERR", HttpStatus.BAD_REQUEST)
        if (!pathCheck(path)) return ResponseEntity<String>("PATH_ERR", HttpStatus.BAD_REQUEST)

        //TODO 용량체크
        if (!fileService.checkUserAccessibility(StringUtils.cleanPath(path), user, FileAuth.Type.WRITE))
            return ResponseEntity("NO_PERMISSION", HttpStatus.FORBIDDEN)

        if (file.isEmpty) return ResponseEntity<String>("FILE_EMPTY_ERR", HttpStatus.BAD_REQUEST)
        if (fileService.isFileExist(path, fileName)) {
            return ResponseEntity<String>("DUP_FILE_ERR", HttpStatus.CONFLICT)
        }
        val toUpload = File("$rootPath/$path", fileName)
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
    @Transactional
    fun mkdir(
        @GetSession user: Session,
        body: MkdirRequest
    ): ResponseEntity<String> {
        if (!fileService.checkUserAccessibility(StringUtils.cleanPath(body.path), user, FileAuth.Type.WRITE))
            return ResponseEntity(HttpStatus.FORBIDDEN)

        if (!pathCheck(body.path) || !nameCheck(body.dirName)) return ResponseEntity(HttpStatus.BAD_REQUEST)
        // TODO
        val file = File(rootPath, body.path)
        if (!file.exists()) return ResponseEntity(HttpStatus.NOT_FOUND)
        val newDir = File(file.path, body.dirName)

        if (newDir.exists()) return ResponseEntity(HttpStatus.CONFLICT)


        kotlin.runCatching { newDir.mkdir() }.onFailure {
            logger.error("exception on make dir", it)
            return ResponseEntity(HttpStatus.CONFLICT)
        }
        fileService.saveFile(newDir, user)


        return ResponseEntity.ok(null)
    }
}