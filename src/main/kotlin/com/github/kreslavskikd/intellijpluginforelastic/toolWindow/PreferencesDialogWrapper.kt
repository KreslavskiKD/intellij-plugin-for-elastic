package com.github.kreslavskikd.intellijpluginforelastic.toolWindow
import com.github.kreslavskikd.intellijpluginforelastic.repo.InfoRepo
import com.intellij.openapi.ui.DialogWrapper
import javax.swing.*


class PreferencesDialogWrapper : DialogWrapper(true) {
    private var contentPane: JPanel
    private var buttonOK: JButton
    private var buttonCancel: JButton

    private var addressField: JTextField
    private var logsDirField: JTextField
    private var queryField: JTextField


    init {
        init()
        title = "Preferences"

        contentPane = JPanel()
        buttonOK = JButton()
        addressField = JTextField()
        buttonCancel = JButton()
        logsDirField = JTextField()
        queryField = JTextField()
        isModal = true
    }

    override fun createCenterPanel(): JComponent {
        return contentPane
    }

    override fun doOKAction() {
        if (addressField.text != "") InfoRepo.elasticAddress = addressField.text
        if (logsDirField.text != "") InfoRepo.logsDir = logsDirField.text
        if (queryField.text != "") InfoRepo.query = queryField.text
        super.doOKAction()
    }
}