package com.github.kreslavskikd.intellijpluginforelastic.util

import com.intellij.notification.NotificationGroupManager
import com.intellij.notification.NotificationType
import com.intellij.openapi.project.Project


object Notifier {
    fun notifyError(
        project: Project,
        content: String,
    ) {
        NotificationGroupManager.getInstance()
            .getNotificationGroup("Custom Notification Group")
            .createNotification(content, NotificationType.ERROR)
            .notify(project)
    }
}