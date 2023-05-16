package com.github.kreslavskikd.intellijpluginforelastic.toolWindow

import com.github.kreslavskikd.intellijpluginforelastic.MyBundle
import com.github.kreslavskikd.intellijpluginforelastic.repo.Constants
import com.github.kreslavskikd.intellijpluginforelastic.repo.InfoRepo
import com.github.kreslavskikd.intellijpluginforelastic.services.KibanaProjectService
import com.intellij.openapi.components.service
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.DialogWrapper
import com.intellij.ui.components.JBTextField
import com.intellij.ui.layout.panel
import javax.swing.JButton
import javax.swing.JTextArea

class QueryDialogWrapper(project: Project) : DialogWrapper(true) {
    private val queryBodyField = JTextArea()
    private var runQueryBtn = JButton()
    private val statusLabel = JBTextField(MyBundle.message("label", "?"))

    private val service = project.service<KibanaProjectService>()

    init {
        title = "Query"

        init()
    }

    override fun createCenterPanel() = panel {
        row("Query body as JSON") {
            queryBodyField(growX)
        }
        row("Run Query") {
            button(MyBundle.message("button_download_logs")){
                val result = service.downloadLogs(
                    baseUrl = "${InfoRepo.elasticAddress}:${Constants.ELASTIC_PORT}",
                    outputDir = InfoRepo.logsDir,
                    query = InfoRepo.query,
                )
                if (result != "") {
                    statusLabel.text = "Done"
                } else {
                    statusLabel.text = "Error occured"
                }
            }
        }
        row("Status") {
            statusLabel(growX)
        }
    }

    fun getQueryBody(): String {
        return queryBodyField.text
    }

    override fun doOKAction() {
        InfoRepo.apply {
            query = getQueryBody()
        }
        super.doOKAction()
    }
}
