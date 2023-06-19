package com.github.kreslavskikd.intellijpluginforelastic.dialogs

import com.github.kreslavskikd.intellijpluginforelastic.PluginBundle
import com.github.kreslavskikd.intellijpluginforelastic.repo.*
import com.github.kreslavskikd.intellijpluginforelastic.services.ElasticProjectService
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.components.service
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.DialogWrapper
import com.intellij.ui.components.JBTextArea
import com.intellij.ui.dsl.builder.MutableProperty
import com.intellij.ui.dsl.builder.panel
import com.intellij.ui.dsl.gridLayout.HorizontalAlign
import javax.swing.JTextArea

class QueryDialogWrapper(project: Project, private val event: AnActionEvent) : DialogWrapper(true) {
    private val queryBodyField = JTextArea()
    private val statusLabel = JBTextArea(PluginBundle.message("label", "?"))

    private val service = project.service<ElasticProjectService>()

    init {
        title = "Query"
        queryBodyField.text = InfoRepo.query

        init()
    }

    override fun createCenterPanel() = panel {
        buttonsGroup ("Select query type") {
            row {
                radioButton("Query as http parameters string", QueryType.QUERY_STRING)
            }
            row {
                radioButton("Query as http parameters in format \"key=value\\n\"", QueryType.QUERY_PARAMS)
            }
            row {
                radioButton("Query body as JSON", QueryType.JSON)
            }
        }.bind (MutableProperty({ InfoRepo.selectedQueryType }, { InfoRepo.selectedQueryType = it } ), QueryType::class.java)

        row("Query body") {
            cell(queryBodyField)
                .horizontalAlign(HorizontalAlign.FILL)
        }
        row {
            button("Save Query") {
                InfoRepo.query = queryBodyField.text
            }
        }

        buttonsGroup ("Select how to save") {
            row {
                radioButton("In a scratch file", SavingLogsType.SCRATCH_FILE)
            }
            row {
                radioButton("In logs directory", SavingLogsType.FILE_IN_DIR)
            }
        }.bind (MutableProperty({ InfoRepo.savingLogsType }, { InfoRepo.savingLogsType = it} ), SavingLogsType::class.java)

        row("Run Query") {
            button(PluginBundle.message("button_download_logs")){

                val prepareQuery = if (InfoRepo.selectedQueryType == QueryType.JSON) {
                    Constants.queryBaseStart + InfoRepo.query + Constants.queryBaseEnd
                } else {
                    InfoRepo.query
                }

                val settings = PluginSettings.getInstance().state

                val result = service.downloadLogsAndStore(
                    baseUrl = "${settings.elasticAddress}:${Constants.ELASTIC_PORT}",
                    howToSave = InfoRepo.savingLogsType,
                    query = prepareQuery,
                    queryType = InfoRepo.selectedQueryType,
                    event = event,
                )

                statusLabel.text = result
            }
        }
        row("Status") {
            cell(statusLabel).horizontalAlign(HorizontalAlign.FILL)
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
