package com.github.kreslavskikd.intellijpluginforelastic.dialogs

import com.github.kreslavskikd.intellijpluginforelastic.repo.InfoRepo
import com.github.kreslavskikd.intellijpluginforelastic.repo.SavingLogsType
import com.github.kreslavskikd.intellijpluginforelastic.repo.Settings
import com.github.kreslavskikd.intellijpluginforelastic.util.Notifier
import com.github.kreslavskikd.intellijpluginforelastic.util.isValidUrl
import com.intellij.openapi.diagnostic.thisLogger
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.DialogWrapper
import com.intellij.ui.dsl.builder.MutableProperty
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
        elasticAddressField.text = InfoRepo.elasticAddress
        outputDirectoryField.text = InfoRepo.logsDir

        init()
    }

    override fun createCenterPanel() = panel {
        row("Your Elastic address") {
            cell(elasticAddressField)
                .horizontalAlign(HorizontalAlign.RIGHT)
        }

        buttonsGroup ("Select how to save") {
            row {
                radioButton("In a scratch file", SavingLogsType.SCRATCH_FILE)
            }
            row {
                radioButton("In logs directory", SavingLogsType.FILE_IN_DIR)
            }
        }.bind (MutableProperty({ Settings.savingLogsType }, { Settings.savingLogsType = it} ), SavingLogsType::class.java)


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
        InfoRepo.apply {
            val tempAddress = getElasticAddress()
            if (isValidUrl(tempAddress)) {
                elasticAddress = tempAddress
                thisLogger().info("elasticAddress is now $elasticAddress")
            } else {
                Notifier.notifyError(project,"$tempAddress is not a valid URL")
                return
            }
            logsDir = getOutputDirectory()
        }
        super.doOKAction()
    }
}
