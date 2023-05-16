package com.github.kreslavskikd.intellijpluginforelastic.actions

import com.github.kreslavskikd.intellijpluginforelastic.repo.InfoRepo
import com.github.kreslavskikd.intellijpluginforelastic.toolWindow.PreferencesDialogWrapper
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent

class ShowDialogAction : AnAction() {
    override fun actionPerformed(e: AnActionEvent) {
        val dialog = PreferencesDialogWrapper()
        dialog.showAndGet()

        // Access the entered values
        val elasticAddressLoc = dialog.getElasticAddress()
        val outputDirectory = dialog.getOutputDirectory()
        val queryBody = dialog.getQueryBody()

        InfoRepo.apply {
            elasticAddress = elasticAddressLoc
            logsDir = outputDirectory
            query = queryBody
        }
    }
}