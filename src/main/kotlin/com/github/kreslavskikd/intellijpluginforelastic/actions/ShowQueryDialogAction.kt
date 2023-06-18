package com.github.kreslavskikd.intellijpluginforelastic.actions

import com.github.kreslavskikd.intellijpluginforelastic.dialogs.QueryDialogWrapper
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent

class ShowQueryDialogAction : AnAction() {

    override fun actionPerformed(e: AnActionEvent) {
        val dialog = QueryDialogWrapper(e.project!!, e)
        dialog.showAndGet()
    }
}