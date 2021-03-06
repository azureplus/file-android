package com.mercandalli.android.sdk.files.api

import com.mercandalli.sdk.files.api.FileDeleteManager
import com.mercandalli.sdk.files.api.MediaScanner

class FileDeleteManagerAndroid(
    private val mediaScanner: MediaScanner
) : FileDeleteManager {

    private val listeners = ArrayList<FileDeleteManager.FileDeleteCompletionListener>()

    override fun delete(path: String) {
        val ioFile = java.io.File(path)
        val parentPath = ioFile.parentFile.absolutePath
        val deleteSucceeded = if (ioFile.isDirectory) {
            deleteDirectory(ioFile)
        } else {
            ioFile.delete()
        }
        mediaScanner.refresh(path)
        mediaScanner.refresh(parentPath)
        for (listener in listeners) {
            listener.onFileDeletedCompleted(path, deleteSucceeded)
        }
    }

    override fun registerFileDeleteCompletionListener(listener: FileDeleteManager.FileDeleteCompletionListener) {
        if (listeners.contains(listener)) {
            return
        }
        listeners.add(listener)
    }

    override fun unregisterFileDeleteCompletionListener(listener: FileDeleteManager.FileDeleteCompletionListener) {
        listeners.remove(listener)
    }

    companion object {

        /**
         * Deletes a directory recursively.
         *
         * @param ioDirectory directory to delete
         */
        @JvmStatic
        fun deleteDirectory(ioDirectory: java.io.File): Boolean {
            if (ioDirectory.isDirectory) {
                val children = ioDirectory.list() ?: return ioDirectory.delete()
                for (str in children) {
                    val success = deleteDirectory(java.io.File(ioDirectory, str))
                    if (!success) {
                        return false
                    }
                }
            }
            // The directory is now empty so delete it
            return ioDirectory.delete()
        }
    }
}
