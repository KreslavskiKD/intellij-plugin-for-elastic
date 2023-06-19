package com.github.kreslavskikd.intellijpluginforelastic.settings

import com.github.kreslavskikd.intellijpluginforelastic.repo.PluginSettings
import com.intellij.openapi.options.SearchableConfigurable
import com.intellij.ui.dsl.builder.panel
import com.intellij.ui.dsl.gridLayout.HorizontalAlign
import javax.swing.JComponent
import javax.swing.JTextField


class PluginSettingsConfigurable : SearchableConfigurable {
    private val elasticAddressField = JTextField()
    private val outputDirectoryField = JTextField()

    override fun createComponent(): JComponent {
        val settings = PluginSettings.getInstance().state

        elasticAddressField.text = settings.elasticAddress
        outputDirectoryField.text = settings.logsDirectory

        return panel {
            row("Your Elastic address") {
                cell(elasticAddressField)
                    .horizontalAlign(HorizontalAlign.FILL)
            }

            row("Output directory for logs") {
                cell(outputDirectoryField)
                    .horizontalAlign(HorizontalAlign.FILL)
            }
        }
    }


    override fun isModified(): Boolean {
        val settings = PluginSettings.getInstance().state

        val elasticAddress = elasticAddressField.text
        val logsDirectory = outputDirectoryField.text

        return elasticAddress != settings.elasticAddress || logsDirectory != settings.logsDirectory
    }

    override fun apply() {
        val newState = PluginSettings.State(
            elasticAddress = elasticAddressField.text,
            logsDirectory = outputDirectoryField.text,
        )

        val settings = PluginSettings.getInstance()
        settings.loadState(newState)
    }

    override fun getDisplayName()
        = "Elastic Logs Downloader"

    override fun getId()
        = "com.github.kreslavskikd.intellijpluginforelastic.settings.PluginSettingsConfigurable"

    override fun reset() {
        val settings = PluginSettings.getInstance().state
        elasticAddressField.text = settings.elasticAddress
        outputDirectoryField.text = settings.logsDirectory

        super.reset()

    }
}