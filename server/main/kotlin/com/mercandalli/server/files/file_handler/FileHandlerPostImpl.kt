package com.mercandalli.server.files.file_handler

import com.mercandalli.sdk.files.api.File
import com.mercandalli.server.files.file_repository.FileRepository
import com.mercandalli.server.files.log.LogManager
import com.mercandalli.sdk.files.api.online.response_json.ServerResponse
import com.mercandalli.sdk.files.api.online.response_json.ServerResponseFile
import org.json.JSONObject

class FileHandlerPostImpl(
        private val fileRepository: FileRepository,
        private val logManager: LogManager
) : FileHandlerPost {

    override fun post(body: String): String {
        logManager.d(TAG, "post(body: $body)")
        val fileJsonObject = JSONObject(body)
        val file = File.fromJson(fileJsonObject)
        fileRepository.put(file)
        return ServerResponseFile.create(
                file,
                "File inserted into the repository"
        ).toJsonString()
    }

    companion object {
        private const val TAG = "FileHandlerPost"
    }
}