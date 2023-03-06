package com.anymind.pos.util

import org.springframework.core.io.ClassPathResource
import org.springframework.core.io.Resource
import org.springframework.core.io.support.EncodedResource
import org.springframework.util.FileCopyUtils
import java.nio.charset.StandardCharsets

object DatabaseUtil {
    fun getSqlFileStatements(sqlFilePath: String): List<String> {
        val res: Resource = ClassPathResource(sqlFilePath)
        val encRes = EncodedResource(res, StandardCharsets.UTF_8)
        return FileCopyUtils.copyToString(encRes.reader).split("\n").filter { it.isNotEmpty() }
    }


}