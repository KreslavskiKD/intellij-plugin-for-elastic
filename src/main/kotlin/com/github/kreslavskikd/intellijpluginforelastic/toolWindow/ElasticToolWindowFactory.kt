package com.github.kreslavskikd.intellijpluginforelastic.toolWindow

import com.github.kreslavskikd.intellijpluginforelastic.PluginBundle
import com.github.kreslavskikd.intellijpluginforelastic.dialogs.PreferencesDialogWrapper
import com.github.kreslavskikd.intellijpluginforelastic.dialogs.QueryDialogWrapper
import com.github.kreslavskikd.intellijpluginforelastic.services.ElasticProjectService
import com.intellij.openapi.components.service
import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import com.intellij.ui.components.JBPanel
import com.intellij.ui.content.ContentFactory
import javax.swing.JButton
import javax.swing.JTextArea


class ElasticToolWindowFactory : ToolWindowFactory {

    private val contentFactory = ContentFactory.SERVICE.getInstance()

    override fun createToolWindowContent(project: Project, toolWindow: ToolWindow) {
        val elasticToolWindow = ElasticToolWindow(toolWindow)
        val content = contentFactory.createContent(elasticToolWindow.getContent(), null, false)
        toolWindow.contentManager.addContent(content)
    }

    override fun shouldBeAvailable(project: Project) = true

    class ElasticToolWindow(private val toolWindow: ToolWindow) {

        private val service = toolWindow.project.service<ElasticProjectService>()

        fun getContent() = JBPanel<JBPanel<*>>().apply {
            val label = JTextArea()

            add(JButton(PluginBundle.message("button_download_logs")).apply {
                addActionListener {
                    if (QueryDialogWrapper(toolWindow.project).showAndGet()) {

                    }
                }
            })

            add(JButton(PluginBundle.message("button_get_info")).apply{
                addActionListener {
                    if (PreferencesDialogWrapper(toolWindow.project).showAndGet()) {
                        // user pressed OK
                    }
                }
            })

            add(label)
        }
    }
}
