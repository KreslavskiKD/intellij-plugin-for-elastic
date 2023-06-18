package com.github.kreslavskikd.intellijpluginforelastic.actions

import com.github.kreslavskikd.intellijpluginforelastic.dialogs.PreferencesDialogWrapper
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent

class ShowPreferencesDialogAction : AnAction() {
    override fun actionPerformed(e: AnActionEvent) {
        val dialog = e.project?.let { PreferencesDialogWrapper(it) }
        dialog?.showAndGet()
    }
}