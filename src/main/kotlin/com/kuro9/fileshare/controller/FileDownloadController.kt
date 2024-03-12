package com.kuro9.fileshare.controller

import com.kuro9.fileshare.annotation.GetSession
import com.kuro9.fileshare.entity.Session
import com.kuro9.fileshare.entity.vo.FileObj
import com.kuro9.fileshare.exception.NotAuthorizedException
import com.kuro9.fileshare.utils.humanReadableByteCountSI
import jakarta.servlet.http.HttpServletResponse
import lombok.RequiredArgsConstructor
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Controller
import org.springframework.util.FileCopyUtils
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.reactive.function.client.WebClientResponseException
import org.springframework.web.servlet.ModelAndView
import java.io.File
import java.io.FileInputStream
import java.time.LocalDateTime


@Controller
@RequestMapping("share/")
@RequiredArgsConstructor
class FileDownloadController {

    private val logger: Logger = LoggerFactory.getLogger(this::class.java)
    private val shareFolderPath = System.getProperty("user.home") + "/Share"


    @GetMapping("files")
    fun filePage(
        @RequestParam(defaultValue = "") dir: String,
        @GetSession user: Session
    ): ModelAndView {
        val page = ModelAndView("FilePage")
        if (dir.contains("..")) {
            throw IllegalArgumentException("Invalid directory")
        }
        page.addObject("userName", user.username)

        val fileList = File(shareFolderPath, dir).listFiles()
            ?.sortedWith(compareBy({ !it.isDirectory }, { it.name }))
            ?.map {
                FileObj(
                    it.name,
                    it.length(),
                    humanReadableByteCountSI(it.length()),
                    it.isDirectory,
                    LocalDateTime.now()
                )
            } ?: emptyList()
        page.addObject("fileList", fileList)
            .addObject("directory", dir)

        return page
    }

    @GetMapping("download")
    fun downloadFile(
        @RequestParam fileName: String,
        @GetSession user: Session,
        response: HttpServletResponse
    ) {
        if (fileName.contains("..")) {
            throw IllegalArgumentException("Invalid directory")
        }

        logger.info("user={} download file={}", user.username, fileName)
        val file = File(shareFolderPath, fileName)
        val downloadFileName = file.name

        with(response) {
            contentType = "application/download"
            setContentLength(file.length().toInt())
            setHeader("Content-disposition", "attachment;filename=\"$downloadFileName\"")
        }
        val os = response.outputStream

        val fis = FileInputStream(file)
        FileCopyUtils.copy(fis, os)
        fis.close()
        os.close()
    }

    @ExceptionHandler(WebClientResponseException::class)
    fun onHttpException(e: WebClientResponseException): ModelAndView {
        logger.error("error", e)
        return ModelAndView("error/StandardErrorPage").apply {
            addObject("title", "Discord API 오류")
            addObject("subTitle", "Discord Api 호출에 실패했습니다.")
            addObject("description", "HTTP Status: ${e.statusCode} ${e.statusText}")
        }
    }

    @ExceptionHandler(NotAuthorizedException::class)
    fun notAuthorized(e: NotAuthorizedException) = ModelAndView("redirect:/oauth/login")
}