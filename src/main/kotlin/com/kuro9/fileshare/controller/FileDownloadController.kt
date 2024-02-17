package com.kuro9.fileshare.controller

import com.kuro9.fileshare.dataclass.FileObj
import jakarta.servlet.http.HttpServletResponse
import lombok.RequiredArgsConstructor
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Controller
import org.springframework.util.FileCopyUtils
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.servlet.ModelAndView
import java.io.File
import java.io.FileInputStream


@Controller
@RequestMapping("share/")
@RequiredArgsConstructor
class FileDownloadController {

    private val logger: Logger = LoggerFactory.getLogger(this::class.java)
    private val shareFolderPath = System.getProperty("user.home") + "/Share"


    @GetMapping("files")
    fun filePage(@RequestParam(defaultValue = "") dir: String): ModelAndView {
        logger.info("entry")
        if (dir.contains("..")) {
            throw IllegalArgumentException("Invalid directory")
        }

        val page = ModelAndView("FilePage")
        val fileList = File(shareFolderPath, dir).listFiles()
            ?.sortedWith(compareBy({ !it.isDirectory }, { it.name}))
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
                    response: HttpServletResponse
                ) {
                    if (fileName.contains("..")) {
                        throw IllegalArgumentException("Invalid directory")
                    }

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
}