package com.kuro9.fileshare.repository

import com.kuro9.fileshare.entity.FileAuth
import com.kuro9.fileshare.entity.FileAuthId
import org.springframework.data.jpa.repository.JpaRepository

interface FileAuthRepository : JpaRepository<FileAuth, FileAuthId>