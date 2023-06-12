package com.github.kreslavskikd.intellijpluginforelastic.toolWindow

import com.github.kreslavskikd.intellijpluginforelastic.PluginBundle
import com.github.kreslavskikd.intellijpluginforelastic.repo.Constants
import com.github.kreslavskikd.intellijpluginforelastic.repo.InfoRepo
import com.github.kreslavskikd.intellijpluginforelastic.repo.QueryType
import com.github.kreslavskikd.intellijpluginforelastic.services.ElasticProjectService
import com.intellij.openapi.components.service
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.DialogWrapper
import com.intellij.ui.components.JBTextField
import com.intellij.ui.dsl.builder.MutableProperty
import com.intellij.ui.dsl.builder.panel
import com.intellij.ui.dsl.gridLayout.HorizontalAlign
import javax.swing.JTextArea

class QueryDialogWrapper(project: Project) : DialogWrapper(true) {
    private val queryBodyField = JTextArea()
    private val statusLabel = JBTextField(PluginBundle.message("label", "?"))

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
        }.bind (MutableProperty({InfoRepo.selectedQueryType }, {InfoRepo.selectedQueryType = it} ), QueryType::class.java)

        row("Query body") {
            cell(queryBodyField)
                .horizontalAlign(HorizontalAlign.FILL)
        }
        row {
            button("Save Query") {
                InfoRepo.query = queryBodyField.text
            }
        }
        row("Run Query") {
            button(PluginBundle.message("button_download_logs")){

                val prepareQuery = if (InfoRepo.selectedQueryType == QueryType.JSON) {
                    Constants.queryBaseStart + InfoRepo.query + Constants.queryBaseEnd
                } else {
                    InfoRepo.query
                }
                val result = service.downloadLogs(
                    baseUrl = "${InfoRepo.elasticAddress}:${Constants.ELASTIC_PORT}",
                    outputDir = InfoRepo.logsDir,
                    query = prepareQuery,
                    queryType = InfoRepo.selectedQueryType
                )
                if (!result.startsWith("failed")) {
                    statusLabel.text = "Done"
                } else {
                    statusLabel.text = "Error occurred"
                }
            }
        }
        row("Status") {
            cell(statusLabel)
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
