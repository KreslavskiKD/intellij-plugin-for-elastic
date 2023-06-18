package com.github.kreslavskikd.intellijpluginforelastic.services

import com.github.kreslavskikd.intellijpluginforelastic.repo.InfoRepo
import com.github.kreslavskikd.intellijpluginforelastic.repo.PluginSettings
import com.github.kreslavskikd.intellijpluginforelastic.repo.QueryType
import com.github.kreslavskikd.intellijpluginforelastic.repo.SavingLogsType
import com.github.kreslavskikd.intellijpluginforelastic.util.ParameterStringBuilder
import com.intellij.featureStatistics.FeatureUsageTracker
import com.intellij.ide.scratch.LRUPopupBuilder
import com.intellij.ide.scratch.ScratchFileCreationHelper
import com.intellij.ide.scratch.ScratchFileService
import com.intellij.ide.scratch.ScratchRootType
import com.intellij.ide.util.PsiNavigationSupport
import com.intellij.idea.ActionsBundle
import com.intellij.ideolog.fileType.LogFileType
import com.intellij.ideolog.fileType.LogLanguage
import com.intellij.lang.Language
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.DataContext
import com.intellij.openapi.actionSystem.LangDataKeys
import com.intellij.openapi.components.Service
import com.intellij.openapi.diagnostic.thisLogger
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.io.FileUtil
import com.intellij.openapi.util.text.StringUtil
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiManager
import com.intellij.psi.util.PsiUtilCore
import com.intellij.util.ArrayUtil
import com.intellij.util.Consumer
import org.jetbrains.annotations.NotNull
import java.io.*
import java.net.HttpURLConnection
import java.net.URL
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Paths
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.io.path.ExperimentalPathApi
import kotlin.io.path.absolutePathString


@Service(Service.Level.PROJECT)
class ElasticProjectService(private val project: Project) {

    fun downloadLogsAndStore(
        baseUrl: String,
        howToSave: SavingLogsType,
        query: String,
        queryType: QueryType,
        event: AnActionEvent
    ): String {
        val endpoint = "/_search"

        val headers = mapOf("Content-Type" to "application/json")

        val url = URL(baseUrl + endpoint)
        val con: HttpURLConnection = url.openConnection() as HttpURLConnection
        con.requestMethod = "GET"
        con.setRequestProperty("Content-Type", "application/json")
        con.connectTimeout = 15000
        con.readTimeout = 15000


        when (queryType) {
            QueryType.QUERY_PARAMS -> {
                return try {
                    val parameters: MutableMap<String, String> = HashMap()

                    val lines = query.split("\n")
                    for (line in lines) {
                        val kv = line.split("=")
                        parameters[kv[0]] = kv[1]
                    }

                    con.doOutput = true
                    val out = DataOutputStream(con.outputStream)
                    out.writeBytes(ParameterStringBuilder.getParamsString(parameters))
                    out.flush()
                    out.close()
                    val status = con.responseCode

                    val streamReader = if (status > 299) {
                        InputStreamReader(con.errorStream)
                    } else {
                        InputStreamReader(con.inputStream)
                    }

                    val responseStream = BufferedReader(
                        streamReader
                    )

                    storeData(howToSave, event, responseStream)

                    responseStream.close()
                    return "success"
                } catch (e: Exception) {
                    thisLogger().warn("failed to load data:")
                    e.cause?.let { thisLogger().warn(it) }
                    e.message?.let { thisLogger().warn(it) }
                    ("failed to load data:\n" + (e.cause ?: "") + " " + (e.message ?: ""))
                } finally {
                    con.disconnect()
                }
            }
            QueryType.QUERY_STRING -> {
                return try {
                    con.doOutput = true
                    val out = DataOutputStream(con.outputStream)
                    out.writeBytes(query)
                    out.flush()
                    out.close()
                    val status = con.responseCode
                    val streamReader = if (status > 299) {
                        InputStreamReader(con.errorStream)
                    } else {
                        InputStreamReader(con.inputStream)
                    }

                    val responseStream = BufferedReader(
                        streamReader
                    )

                    storeData(howToSave, event, responseStream)

                    responseStream.close()
                    return "success"
                } catch (e : Exception) {
                    thisLogger().warn("failed to load data:")
                    e.cause?.let { thisLogger().warn(it) }
                    e.message?.let { thisLogger().warn(it) }
                    ("failed to load data:\n" + (e.cause ?: "") + " " + (e.message ?: ""))
                } finally {
                    con.disconnect();
                }
            }
            QueryType.JSON -> { // todo rewrite without khttp usage if possible
                return try {
                    val response = khttp.get(baseUrl + endpoint, headers = headers, data = query)

                    response.text
                } catch (e: Exception) {
                    thisLogger().warn("failed to load data:")
                    e.cause?.let { thisLogger().warn(it) }
                    e.message?.let { thisLogger().warn(it) }
                    ("failed to load data:\n" + (e.cause ?: "") + " " + (e.message ?: ""))
                }
            }
        }
    }

    @OptIn(ExperimentalPathApi::class)
    fun storeData(howToSave: SavingLogsType, e: AnActionEvent, streamToReadFrom: BufferedReader) {

        if (howToSave == SavingLogsType.FILE_IN_DIR) {
            val settings = PluginSettings.getInstance().state
            File(settings.logsDirectory).mkdirs()
            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd-HH-mm-ss")
            val current = LocalDateTime.now().format(formatter)
            val rootPath = Paths.get("/").toAbsolutePath()
            val directoryPath = Paths.get(e.project!!.basePath + File.separator + settings.logsDirectory).normalize()
            thisLogger().info("directoryPath: " + directoryPath)
            if (Files.notExists(rootPath.resolve(directoryPath))) {
                Files.createDirectory(rootPath.resolve(directoryPath))
            }
            val outputFile = Paths.get(e.project!!.basePath + File.separator + settings.logsDirectory + File.separator + "elastic-$current-data.log").normalize()
            thisLogger().info("directoryPath: " + outputFile)
            Files.createFile(outputFile)

            Files.write(outputFile, InfoRepo.lastResult.toByteArray(StandardCharsets.UTF_8))

            val bufferedWriter = Files.newBufferedWriter(outputFile, StandardCharsets.UTF_8, )

            var inputLine: String?
            inputLine = streamToReadFrom.readLine()
            while (inputLine != null) {
                bufferedWriter.write(inputLine)
                inputLine = streamToReadFrom.readLine()
            }

            thisLogger().info("data logs stored to: " + outputFile.absolutePathString())
        } else if (howToSave == SavingLogsType.SCRATCH_FILE) {

            val project = e.project ?: return
            val context = createContext(e, streamToReadFrom)
            val consumer = Consumer { l: Language? ->
                context.language = l
                ScratchFileCreationHelper.EXTENSION.forLanguage(context.language).prepareText(
                    project, context, DataContext.EMPTY_CONTEXT
                )
                doCreateNewScratch(project, context)
            }

            if (context.language != null) {
                consumer.consume(context.language)
            } else {
                LRUPopupBuilder.forFileLanguages(
                    project,
                    ActionsBundle.message("action.NewScratchFile.text.with.new"),
                    null,
                    consumer
                ).showCenteredInCurrentWindow(project)
            }
        }
    }

    @NotNull
    fun createContext(e: AnActionEvent, streamToReadFrom: BufferedReader): ScratchFileCreationHelper.Context {
        val context = ScratchFileCreationHelper.Context()

        var inputLine: String?
        val content = StringBuffer()
        inputLine = streamToReadFrom.readLine()
        while (inputLine != null) {
            content.append(inputLine)
            inputLine = streamToReadFrom.readLine()
        }

        context.text = content.toString()

        if (context.text.isNotEmpty()) {
            context.language = LogLanguage
        }
        context.ideView = e.getData(LangDataKeys.IDE_VIEW)
        return context
    }

    private fun doCreateNewScratch(project: Project, context: ScratchFileCreationHelper.Context): PsiFile? {
        FeatureUsageTracker.getInstance().triggerFeatureUsed("scratch")
        val language = Objects.requireNonNull(context.language)
        if (context.fileExtension == null) {
            val fileType = LogFileType
            context.fileExtension = fileType.defaultExtension
        }
        ScratchFileCreationHelper.EXTENSION.forLanguage(language).beforeCreate(project, context)
        val dir =
            if (context.ideView != null) PsiUtilCore.getVirtualFile(ArrayUtil.getFirstElement(context.ideView.directories)) else null
        val rootType = if (dir == null) null else ScratchFileService.findRootType(dir)
        val relativePath = (if (rootType !== ScratchRootType.getInstance()) "" else FileUtil.getRelativePath(
            ScratchFileService.getInstance().getRootPath(rootType), dir!!.path, '/'
        ))!!
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd-HH-mm-ss")
        val current = LocalDateTime.now().format(formatter)
        val fileName = (if (StringUtil.isEmpty(relativePath)) "" else "$relativePath/") + "elastic-$current-data.log"
        val file = ScratchRootType.getInstance().createScratchFile(
            project, fileName, language, context.text, context.createOption
        )
            ?: return null
        PsiNavigationSupport.getInstance().createNavigatable(project, file, context.caretOffset).navigate(true)
        val psiFile = PsiManager.getInstance(project).findFile(file)
        if (context.ideView != null && psiFile != null) {
            context.ideView.selectElement(psiFile)
        }
        return psiFile
    }
}
