package com.mercandalli.sdk.files.api

class FileSortManagerImpl : FileSortManager {

    override fun sort(files: List<File>): List<File> {
        return files.sortedWith(compareBy {
            it.name
        })
    }

    override fun sortIoFiles(ioFiles: List<java.io.File>): List<java.io.File> {
        return ioFiles.sortedWith(compareBy {
            it.name
        })
    }

    override fun sortIoFiles(ioFiles: Array<out java.io.File>): Array<out java.io.File> {
        return ioFiles.sortedWith(compareBy {
            it.name
        }).toTypedArray()
    }
}
