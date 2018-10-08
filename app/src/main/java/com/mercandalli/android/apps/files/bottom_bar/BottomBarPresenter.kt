package com.mercandalli.android.apps.files.bottom_bar

import android.os.Bundle
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import com.mercandalli.android.apps.files.developer.DeveloperManager
import com.mercandalli.android.apps.files.theme.ThemeManager

class BottomBarPresenter(
        private val screen: BottomBarContract.Screen,
        private val themeManager: ThemeManager,
        private val developerManager: DeveloperManager,
        @ColorInt private val selectedColor: Int,
        @ColorInt private val notSelectedColor: Int
) : BottomBarContract.UserAction {

    private var selectedSection: Int = SECTION_UNDEFINED
    private val themeListener = createThemeListener()
    private val developerModeListener = createDeveloperModeListener()

    init {
        selectFile()
    }

    override fun onAttached() {
        themeManager.registerThemeListener(themeListener)
        developerManager.registerDeveloperModeListener(developerModeListener)
        syncWithCurrentTheme()
        syncWithDeveloperMode()
    }

    override fun onDetached() {
        themeManager.unregisterThemeListener(themeListener)
        developerManager.unregisterDeveloperModeListener(developerModeListener)
    }

    override fun onSaveInstanceState(saveState: Bundle) {
        saveState.putInt("BUNDLE_KEY_SECTION", selectedSection)
    }

    override fun onRestoreInstanceState(state: Bundle) {
        val section = state.getInt("BUNDLE_KEY_SECTION")
        when (section) {
            SECTION_FILE -> selectFile()
            SECTION_NOTE -> selectNote()
            SECTION_SETTINGS -> selectSettings()
            else -> selectFile()
        }
    }

    override fun onFileClicked() {
        selectFile()
        screen.notifyListenerFileClicked()
    }

    override fun onOnlineClicked() {
        selectOnline()
        screen.notifyListenerOnlineClicked()
    }

    override fun onNoteClicked() {
        selectNote()
        screen.notifyListenerNoteClicked()
    }

    override fun onSettingsClicked() {
        selectSettings()
        screen.notifyListenerSettingsClicked()
    }

    private fun selectFile() {
        selectedSection = SECTION_FILE
        screen.setFileIconColor(selectedColor)
        screen.setOnlineIconColor(notSelectedColor)
        screen.setNoteIconColor(notSelectedColor)
        screen.setSettingsIconColor(notSelectedColor)
    }

    private fun selectOnline() {
        selectedSection = SECTION_ONLINE
        screen.setFileIconColor(notSelectedColor)
        screen.setOnlineIconColor(selectedColor)
        screen.setNoteIconColor(notSelectedColor)
        screen.setSettingsIconColor(notSelectedColor)
    }

    private fun selectNote() {
        selectedSection = SECTION_NOTE
        screen.setFileIconColor(notSelectedColor)
        screen.setOnlineIconColor(notSelectedColor)
        screen.setNoteIconColor(selectedColor)
        screen.setSettingsIconColor(notSelectedColor)
    }

    private fun selectSettings() {
        selectedSection = SECTION_SETTINGS
        screen.setFileIconColor(notSelectedColor)
        screen.setOnlineIconColor(notSelectedColor)
        screen.setNoteIconColor(notSelectedColor)
        screen.setSettingsIconColor(selectedColor)
    }

    private fun syncWithCurrentTheme() {
        val theme = themeManager.getTheme()
        screen.setSectionFileTextColorRes(theme.textPrimaryColorRes)
        screen.setSectionOnlineTextColorRes(theme.textPrimaryColorRes)
        screen.setSectionNoteTextColorRes(theme.textPrimaryColorRes)
        screen.setSectionSettingsTextColorRes(theme.textPrimaryColorRes)
    }

    private fun syncWithDeveloperMode(developerMode: Boolean = developerManager.isDeveloperMode()) {
        if (developerMode) {
            screen.showOnlineSection()
        } else {
            screen.hideOnlineSection()
        }
    }

    private fun createThemeListener() = object : ThemeManager.ThemeListener {
        override fun onThemeChanged() {
            syncWithCurrentTheme()
        }
    }

    private fun createDeveloperModeListener() = object : DeveloperManager.DeveloperModeListener {
        override fun onDeveloperModeChanged() {
            syncWithDeveloperMode()
        }
    }

    companion object {
        private const val SECTION_UNDEFINED = 0
        private const val SECTION_FILE = 1
        private const val SECTION_ONLINE = 2
        private const val SECTION_NOTE = 3
        private const val SECTION_SETTINGS = 4
    }
}