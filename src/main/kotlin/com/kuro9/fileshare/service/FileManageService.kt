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
        return fileInfo.existsByFullPath("$parentPath/$fileName")
    }

    fun isFileExist(fullPath: String): Boolean {
        return fileInfo.existsByFullPath(fullPath)
    }

    fun saveFile(file: File, user: Session) {
        if (!file.exists()) throw IllegalArgumentException("file is not exist")
        fileInfo.save(FileInfo.toFileInfo(file, user.discordId))
    }

    /**
     * 유저 접근권한 확인
     * @param type : 테스트 권한. [FileAuth.Type.NONE]은 허용하지 않음
     */
    fun checkUserAccessibility(path: String, user: Session, type: FileAuth.Type): Boolean {
        if (type == FileAuth.Type.NONE) throw IllegalArgumentException("Type.NONE is not allowed in this method")
        val fileInfo = fileInfo.findById(path)
        // TODO 폴더의 접근권한을 어떻게 할지 생각해 보아야 함. 현재는 루트 폴더가 db에 정보가 없어 무조건 false 리턴
        if (fileInfo.isEmpty) return false // 파일이 없다면 false
        if (fileInfo.get().ownerId == user.discordId) return true   // 파일의 owner라면 true

        val result = fileAuth.findById(FileAuthId(path, user.discordId))
        if (result.isEmpty) return false
        val resultType = result.get().type
        return when (type) {
            FileAuth.Type.READ -> resultType.isReadable()
            FileAuth.Type.WRITE -> resultType.isWritable()
            FileAuth.Type.RW -> resultType.isFullAccess()
            else -> throw IllegalArgumentException("Type.NONE is not allowed in this method")
        }
    }
}