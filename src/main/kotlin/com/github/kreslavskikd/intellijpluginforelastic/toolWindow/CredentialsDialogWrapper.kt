package com.github.kreslavskikd.intellijpluginforelastic.toolWindow
import com.intellij.openapi.ui.DialogWrapper
import javax.swing.*


class CredentialsDialogWrapper : DialogWrapper(true) {
    private var contentPane: JPanel
    private var buttonOK: JButton
    private var buttonCancel: JButton

    private var adressField: JTextField
    private var loginField: JTextField
    private var passwordField: JPasswordField


    init {
        init()
        title = "Credentials"

        contentPane = JPanel()
        buttonOK = JButton()
        adressField = JTextField()
        buttonCancel = JButton()
        loginField = JTextField()
        passwordField = JPasswordField()
        isModal = true
    }

    override fun createCenterPanel(): JComponent {
        return contentPane
    }

    val username: String
        get() = loginField.text
    val password: CharArray
        get() = passwordField.password

    override fun doOKAction() {
        // Do any necessary validation of the username and password fields here
        super.doOKAction()
    }
}