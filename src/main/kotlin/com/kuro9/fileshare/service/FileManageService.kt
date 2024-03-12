package com.kuro9.fileshare.service

import com.kuro9.fileshare.entity.FileInfo
import com.kuro9.fileshare.entity.Session
import com.kuro9.fileshare.repository.FileAuthRepository
import com.kuro9.fileshare.repository.FileInfoRepository
import org.springframework.stereotype.Service
import java.io.File

@Service
class FileManageService(
    private val fileInfo: FileInfoRepository,
    private val fileAuth: FileAuthRepository
) {

    fun getFileList(parentPath: String): List<FileInfo> {
        return fileInfo.findAllByParentPath(parentPath)
    }

    fun isFileExist(parentPath: String, fileName: String): Boolean {
        return fileInfo.existsByFullPath("$parentPath/$fileName");
    }

    fun saveFile(file: File, user: Session) {
        if (!file.exists()) throw IllegalArgumentException("file is not exist")
        fileInfo.save(FileInfo.toFileInfo(file, user.discordId))
    }
}