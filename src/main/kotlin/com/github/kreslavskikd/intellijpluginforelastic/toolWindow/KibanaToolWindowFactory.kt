package com.github.kreslavskikd.intellijpluginforelastic.toolWindow

import com.github.kreslavskikd.intellijpluginforelastic.MyBundle
import com.github.kreslavskikd.intellijpluginforelastic.repo.Constants
import com.github.kreslavskikd.intellijpluginforelastic.repo.InfoRepo
import com.github.kreslavskikd.intellijpluginforelastic.services.KibanaProjectService
import com.intellij.openapi.components.service
import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import com.intellij.ui.components.JBPanel
import com.intellij.ui.components.JBTextField
import com.intellij.ui.content.ContentFactory
import javax.swing.JButton
import javax.swing.JTextArea


class KibanaToolWindowFactory : ToolWindowFactory {

    private val contentFactory = ContentFactory.SERVICE.getInstance()

    override fun createToolWindowContent(project: Project, toolWindow: ToolWindow) {
        val kibanaToolWindow = KibanaToolWindow(toolWindow)
        val content = contentFactory.createContent(kibanaToolWindow.getContent(), null, false)
        toolWindow.contentManager.addContent(content)
    }

    override fun shouldBeAvailable(project: Project) = true

    class KibanaToolWindow(toolWindow: ToolWindow) {

        private val service = toolWindow.project.service<KibanaProjectService>()

        fun getContent() = JBPanel<JBPanel<*>>().apply {
            val label = JTextArea()

            add(JButton(MyBundle.message("button_download_logs")).apply {
                addActionListener {
                    label.text = MyBundle.message("label", service.downloadLogs(
                        baseUrl = "${InfoRepo.elasticAddress}:${Constants.ELASTIC_PORT}",
                        outputDir = InfoRepo.logsDir,
                        query = InfoRepo.query,
                    ))
                }
            })

            add(JButton(MyBundle.message("button_get_info")).apply{
                addActionListener {
                    if (PreferencesDialogWrapper().showAndGet()) {
                        // user pressed OK
                    }
                }
            })

            add(label)
        }
    }
}
