package com.mercandalli.android.sdk.files.api.online

import android.content.Context
import com.mercandalli.android.sdk.files.api.FileModule
import com.mercandalli.android.sdk.files.api.MediaScanner
import com.mercandalli.android.sdk.files.api.MediaScannerAndroid
import com.mercandalli.sdk.files.api.FileDeleteManager
import com.mercandalli.sdk.files.api.FileManager
import com.mercandalli.sdk.files.api.online.FileOnlineLoginRepository
import com.mercandalli.sdk.files.api.online.FileOnlineModule

class FileOnlineAndroidModule(
        private val context: Context,
        private val fileOnlineApiNetwork: FileOnlineApiNetwork
) {

    private val mediaScanner: MediaScanner by lazy {
        val addOn = object : MediaScannerAndroid.AddOn {
            override fun refreshSystemMediaScanDataBase(path: String) {
            }
        }
        MediaScannerAndroid(addOn)
    }

    private val fileOnlineModule by lazy { FileOnlineModule() }
    private val fileOnlineLoginRepository by lazy { createFileOnlineLoginRepository() }
    private val fileOnlineLoginManager by lazy { createFileOnlineLoginManager() }

    private val fileOnlineApi by lazy {
        createFileOnlineApi()
    }

    fun createFileOnlineManager(): FileManager {
        val fileManager = FileOnlineManagerAndroid(
                fileOnlineApi
        )
        mediaScanner.setListener(object : MediaScanner.RefreshListener {
            override fun onContentChanged(path: String) {
                fileManager.refresh(path)
            }
        })
        return fileManager
    }

    fun createFileOnlineLoginManager() = fileOnlineModule.createFileOnlineLoginManager(
            fileOnlineLoginRepository
    )

    fun createFileOnlineUploadManager(): FileOnlineUploadManager {
        return FileOnlineUploadManagerImpl(
                fileOnlineApi,
                mediaScanner
        )
    }

    fun createFileOnlineDeleteManager(): FileDeleteManager {
        return FileOnlineDeleteManagerAndroid(
                fileOnlineApi,
                mediaScanner
        )
    }

    private fun createFileOnlineLoginRepository(): FileOnlineLoginRepository {
        val sharedPreferences = context.getSharedPreferences(
                FileOnlineLoginRepositoryImpl.PREFERENCE_NAME,
                Context.MODE_PRIVATE
        )
        return FileOnlineLoginRepositoryImpl(
                sharedPreferences
        )
    }

    private fun createFileOnlineApi(): FileOnlineApi {
        return FileOnlineApiImpl(
                fileOnlineApiNetwork,
                fileOnlineLoginManager
        )
    }
}