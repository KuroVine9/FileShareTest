package com.kuro9.fileshare.repository

import com.kuro9.fileshare.entity.FileInfo
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface FileInfoRepository : JpaRepository<FileInfo, String> {
}