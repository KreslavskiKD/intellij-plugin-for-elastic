package com.github.kreslavskikd.intellijpluginforelastic.toolWindow
import com.github.kreslavskikd.intellijpluginforelastic.repo.InfoRepo
import com.intellij.openapi.ui.DialogWrapper
import com.intellij.ui.layout.panel
import javax.swing.*


class PreferencesDialogWrapper : DialogWrapper(true) {
    private val elasticAddressField = JTextField()
    private val outputDirectoryField = JTextField()

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
    }

    fun getElasticAddress(): String {
        return elasticAddressField.text
    }

    fun getOutputDirectory(): String {
        return outputDirectoryField.text
    }

    override fun doOKAction() {
        InfoRepo.apply {
            elasticAddress = getElasticAddress()
            logsDir = getOutputDirectory()
        }
        super.doOKAction()
    }
}
