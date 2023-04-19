package com.github.kreslavskikd.intellijpluginforelastic.services

import com.intellij.openapi.components.Service
import com.intellij.openapi.diagnostic.thisLogger
import com.intellij.openapi.project.Project
import java.io.File

import java.util.*

@Service(Service.Level.PROJECT)
class KibanaProjectService(project: Project) {

    fun downloadLogs(baseUrl: String, outputDir: String): String {
        val endpoint = "/_search"

        val headers = mapOf("Content-Type" to "application/json")
        val data = """{"query": {"match_all": {}}}"""

        try {
            val response = khttp.get(baseUrl + endpoint, headers = headers, data = data)
            File(outputDir).mkdirs()
            val outputFile = File("$outputDir/data.log")
            outputFile.writeText(response.text)
            thisLogger().info("data logs stored to: " + outputFile.absolutePath)
            return response.text
        } catch (e: Exception) {
            thisLogger().warn("failed to load data:")
            e.cause?.let { thisLogger().warn(it) }
            e.message?.let { thisLogger().warn(it) }
        }
        return ""
    }
}
