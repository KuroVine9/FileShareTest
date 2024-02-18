package com.kuro9.fileshare.controller

import com.kuro9.fileshare.dataclass.FileObj
import com.kuro9.fileshare.service.OAuthApiService
import jakarta.servlet.http.HttpServletResponse
import lombok.RequiredArgsConstructor
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Controller
import org.springframework.util.FileCopyUtils
import org.springframework.web.bind.annotation.*
import org.springframework.web.reactive.function.client.WebClientResponseException
import org.springframework.web.servlet.ModelAndView
import java.io.File
import java.io.FileInputStream


@Controller
@RequestMapping("share/")
@RequiredArgsConstructor
class FileDownloadController(
    val oAuthApiService: OAuthApiService
) {

    private val logger: Logger = LoggerFactory.getLogger(this::class.java)
    private val shareFolderPath = System.getProperty("user.home") + "/Share"


    @GetMapping("files")
    fun filePage(
        @RequestParam(defaultValue = "") dir: String,
        @CookieValue("access_token") token: String?,
    ): ModelAndView {
        val page = ModelAndView("FilePage")
        if (dir.contains("..")) {
            throw IllegalArgumentException("Invalid directory")
        }

        if (token == null) {
            return ModelAndView("redirect:/oauth/login")
        }
        oAuthApiService.getUserInfo(token)?.let {
            logger.info("filePage user={}", it)
            page.addObject("userName", it.username)
        } ?: return ModelAndView("redirect:/oauth/login")

        val fileList = File(shareFolderPath, dir).listFiles()
            ?.sortedWith(compareBy({ !it.isDirectory }, { it.name }))
            ?.map {
                FileObj(
                    it.name,
                    it.length(),
                    it.isDirectory
                )
            } ?: emptyList()
        page.addObject("fileList", fileList)
            .addObject("directory", dir)

        return page
    }

    @GetMapping("download")
    fun downloadFile(
        @RequestParam fileName: String,
        @CookieValue("access_token") token: String?,
        response: HttpServletResponse
    ) {
        if (fileName.contains("..")) {
            throw IllegalArgumentException("Invalid directory")
        }
        if (token == null) {
            response.sendRedirect("/oauth/login")
            return
        }
        val user = oAuthApiService.getUserInfo(token)
        if (user == null) {
            response.sendRedirect("/oauth/login")
            return
        }

        logger.info("user={} download file={}", user, fileName)
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
}