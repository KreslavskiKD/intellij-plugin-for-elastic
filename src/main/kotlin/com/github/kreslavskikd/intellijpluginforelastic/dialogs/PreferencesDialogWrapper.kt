package com.github.kreslavskikd.intellijpluginforelastic.dialogs

import com.github.kreslavskikd.intellijpluginforelastic.repo.PluginSettings
import com.github.kreslavskikd.intellijpluginforelastic.util.Notifier
import com.github.kreslavskikd.intellijpluginforelastic.util.isValidUrl
import com.intellij.openapi.diagnostic.thisLogger
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.DialogWrapper
import com.intellij.ui.dsl.builder.panel
import com.intellij.ui.dsl.gridLayout.HorizontalAlign
import javax.swing.*


class PreferencesDialogWrapper(
    private val project: Project,
) : DialogWrapper(true) {
    private val elasticAddressField = JTextField()
    private val outputDirectoryField = JTextField()

    init {
        title = "Preferences"
        val curState = PluginSettings.getInstance().state

        elasticAddressField.text = curState.elasticAddress
        outputDirectoryField.text = curState.logsDirectory

        init()
    }

    override fun createCenterPanel() = panel {
        row("Your Elastic address") {
            cell(elasticAddressField)
                .horizontalAlign(HorizontalAlign.RIGHT)
        }

        row("Output directory for logs") {
            cell(outputDirectoryField)
                .horizontalAlign(HorizontalAlign.RIGHT)
        }
    }

    fun getElasticAddress(): String {
        return elasticAddressField.text
    }

    fun getOutputDirectory(): String {
        return outputDirectoryField.text
    }

    override fun doOKAction() {
        val settings = PluginSettings.getInstance()

        val tempAddr: String

        val tempAddress = getElasticAddress()
        if (isValidUrl(tempAddress)) {
            tempAddr = tempAddress
            thisLogger().info("elasticAddress is now $tempAddr")
        } else {
            Notifier.notifyError(project,"$tempAddress is not a valid URL")
            return
        }
        val tempLD: String = getOutputDirectory()

        settings.loadState(PluginSettings.State(
            elasticAddress = tempAddr,
            logsDirectory = tempLD,
        ))

        super.doOKAction()
    }
}
