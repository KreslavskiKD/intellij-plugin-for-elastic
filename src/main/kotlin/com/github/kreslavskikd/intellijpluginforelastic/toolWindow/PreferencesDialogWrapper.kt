package com.github.kreslavskikd.intellijpluginforelastic.toolWindow
import com.github.kreslavskikd.intellijpluginforelastic.repo.InfoRepo
import com.intellij.openapi.ui.DialogWrapper
import com.intellij.ui.layout.panel
import javax.swing.*


class PreferencesDialogWrapper : DialogWrapper(true) {
    private val elasticAddressField = JTextField()
    private val outputDirectoryField = JTextField()
    private val queryBodyField = JTextArea()

    init {
        title = "Preferences"

        init()
    }

    override fun createCenterPanel() = panel {
        row("Your Elastic address") {
            elasticAddressField(growX)
        }
        row("Output directory for logs") {
            outputDirectoryField(growX)
        }
        row("Query body as JSON") {
            queryBodyField(growX)
        }
    }

    fun getElasticAddress(): String {
        return elasticAddressField.text
    }

    fun getOutputDirectory(): String {
        return outputDirectoryField.text
    }

    fun getQueryBody(): String {
        return queryBodyField.text
    }

    override fun doOKAction() {
        InfoRepo.apply {
            elasticAddress = getElasticAddress()
            logsDir = getOutputDirectory()
            query = getQueryBody()
        }
        super.doOKAction()
    }
}
