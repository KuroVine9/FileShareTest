package com.kuro9.fileshare.controller

import jakarta.servlet.http.HttpServletResponse
import lombok.RequiredArgsConstructor
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Controller
import org.springframework.util.FileCopyUtils
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.servlet.ModelAndView
import java.io.File
import java.io.FileInputStream


@Controller
@RequestMapping("share/")
@RequiredArgsConstructor
class FileDownloadController {

    private val logger: Logger = LoggerFactory.getLogger(this::class.java)

    @GetMapping("files")
    fun filePage(): ModelAndView {
        logger.info("entry")
        val page = ModelAndView("FilePage")
        val home = System.getProperty("user.home")
        val fileList = File("${home}/Share").list()
        page.addObject("fileList", fileList)

        return page
    }

    @GetMapping("files/{fileName}")
    fun downloadFile(
        @PathVariable fileName: String,
        response: HttpServletResponse
    ) {
        val home = System.getProperty("user.home")
        val f = File("${home}/Share/", fileName)

        with(response) {
            contentType = "application/download"
            setContentLength(f.length().toInt())
            setHeader("Content-disposition", "attachment;filename=\"$fileName\"")
        }
        val os = response.outputStream

        val fis = FileInputStream(f)
        FileCopyUtils.copy(fis, os)
        fis.close()
        os.close()
    }
}