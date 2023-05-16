package com.github.kreslavskikd.intellijpluginforelastic.actions

import com.github.kreslavskikd.intellijpluginforelastic.repo.InfoRepo
import com.github.kreslavskikd.intellijpluginforelastic.toolWindow.QueryDialogWrapper
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent

class ShowQueryDialogAction : AnAction(){
    override fun actionPerformed(e: AnActionEvent) {
        val dialog = QueryDialogWrapper(e.project!!)
        dialog.showAndGet()

        val queryBody = dialog.getQueryBody()

        InfoRepo.apply {
            query = queryBody
        }
    }
}