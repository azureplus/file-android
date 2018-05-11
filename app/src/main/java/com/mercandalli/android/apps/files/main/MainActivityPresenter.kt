package com.mercandalli.android.apps.files.main

import android.os.Bundle
import android.os.Environment
import com.mercandalli.sdk.files.api.FileCreatorManager

class MainActivityPresenter(
        private val screen: MainActivityContract.Screen,
        private val fileCreatorManager: FileCreatorManager
) : MainActivityContract.UserAction {

    private var selectedPath: String? = null
    private var selectedSection: Int = SECTION_UNDEFINED

    override fun onRestoreInstanceState(savedInstanceState: Bundle?) {
        if (savedInstanceState == null) {
            selectFile()
            return
        }
        val section = savedInstanceState.getInt("section", SECTION_UNDEFINED)
        when (section) {
            SECTION_FILE -> selectFile()
            SECTION_NOTE -> selectNote()
            SECTION_SETTINGS -> selectSettings()
            else -> selectFile()
        }
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        if (outState == null) {
            return
        }
        outState.putInt("section", selectedSection)
    }

    override fun onFileSectionClicked() {
        selectFile()
    }

    override fun onNoteSectionClicked() {
        selectNote()
    }

    override fun onSettingsSectionClicked() {
        selectSettings()
    }

    override fun onToolbarDeleteClicked() {
        screen.deleteNote()
    }

    override fun onToolbarShareClicked() {
        screen.shareNote()
    }

    override fun onToolbarAddClicked() {
        screen.showFileCreationSelection()
    }

    override fun onFileCreationConfirmed(fileName: String) {
        val path = if (selectedPath == null) Environment.getExternalStorageDirectory().absolutePath
        else selectedPath
        fileCreatorManager.create(path!!, fileName)
    }

    override fun onSelectedFilePathChanged(path: String?) {
        selectedPath = path
    }

    private fun selectFile() {
        selectedSection = SECTION_FILE
        screen.showFileView()
        screen.hideNoteView()
        screen.hideSettingsView()
        screen.hideToolbarDelete()
        screen.hideToolbarShare()
        screen.showToolbarAdd()
    }

    private fun selectNote() {
        selectedSection = SECTION_NOTE
        screen.hideFileView()
        screen.showNoteView()
        screen.hideSettingsView()
        screen.showToolbarDelete()
        screen.showToolbarShare()
        screen.hideToolbarAdd()
    }

    private fun selectSettings() {
        selectedSection = SECTION_SETTINGS
        screen.hideFileView()
        screen.hideNoteView()
        screen.showSettingsView()
        screen.hideToolbarDelete()
        screen.hideToolbarShare()
        screen.hideToolbarAdd()
    }

    companion object {
        private const val SECTION_UNDEFINED = 0
        private const val SECTION_FILE = 1
        private const val SECTION_NOTE = 2
        private const val SECTION_SETTINGS = 3
    }
}