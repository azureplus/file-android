package com.mercandalli.android.sdk.files.api.online

import com.mercandalli.sdk.files.api.FileRenameManager
import com.mercandalli.sdk.files.api.MediaScanner
import kotlinx.coroutines.experimental.Dispatchers
import kotlinx.coroutines.experimental.GlobalScope
import kotlinx.coroutines.experimental.android.Main
import kotlinx.coroutines.experimental.launch

internal class FileOnlineRenameManagerAndroid(
        private val fileOnlineApi: FileOnlineApi,
        private val mediaScanner: MediaScanner
) : FileRenameManager {

    override fun rename(path: String, fileName: String) {
        val ioFile = java.io.File(path)
        val parentPath = ioFile.parentFile.absolutePath
        val ioFileOutput = java.io.File(parentPath, fileName)
        val outputPath = ioFileOutput.absolutePath
        GlobalScope.launch(Dispatchers.Default) {
            val renameSucceeded = fileOnlineApi.rename(path, fileName)
            GlobalScope.launch(Dispatchers.Main) {
                if (!renameSucceeded) {
                    return@launch
                }
                mediaScanner.refresh(path)
                mediaScanner.refresh(outputPath)
                mediaScanner.refresh(parentPath)
            }
        }
    }
}