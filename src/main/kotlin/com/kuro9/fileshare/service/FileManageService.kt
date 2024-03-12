package com.kuro9.fileshare.service

import com.kuro9.fileshare.entity.FileAuth
import com.kuro9.fileshare.entity.FileAuthId
import com.kuro9.fileshare.entity.FileInfo
import com.kuro9.fileshare.entity.Session
import com.kuro9.fileshare.repository.FileAuthRepository
import com.kuro9.fileshare.repository.FileInfoRepository
import org.springframework.data.repository.findByIdOrNull
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

    fun getFile(fullPath: String): FileInfo? {
        return fileInfo.findByIdOrNull(fullPath)
    }

    fun isFileExist(parentPath: String, fileName: String): Boolean {
        return fileInfo.existsByFullPath("$parentPath/$fileName");
    }

    fun isFileExist(fullPath: String): Boolean {
        return fileInfo.existsByFullPath(fullPath)
    }

    fun saveFile(file: File, user: Session) {
        if (!file.exists()) throw IllegalArgumentException("file is not exist")
        fileInfo.save(FileInfo.toFileInfo(file, user.discordId))
    }

    fun isUserAccessible(path: String, user: Session): Boolean {
        val fileInfo = fileInfo.findById(path)
        if(fileInfo.isEmpty) return false
        if(fileInfo.get().ownerId == user.discordId) return true

        val result = fileAuth.findById(FileAuthId(path, user.discordId))
        if(result.isEmpty) return false
        return result.get().type == FileAuth.Type.INCLUDE
    }
}