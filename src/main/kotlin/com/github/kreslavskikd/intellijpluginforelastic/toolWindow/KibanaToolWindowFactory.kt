package com.github.kreslavskikd.intellijpluginforelastic.toolWindow

import com.intellij.openapi.components.service
import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import com.intellij.ui.components.JBLabel
import com.intellij.ui.components.JBPanel
import com.intellij.ui.content.ContentFactory
import com.github.kreslavskikd.intellijpluginforelastic.MyBundle
import com.github.kreslavskikd.intellijpluginforelastic.services.KibanaProjectService
import javax.swing.JButton


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
            val label = JBLabel(MyBundle.message("label", "?"))

            add(JButton(MyBundle.message("button_send_request")).apply {
                addActionListener {
                    label.text = MyBundle.message("label", service.sendKibanaRequest())
                }
            })
            add(label)

        }
    }
}
