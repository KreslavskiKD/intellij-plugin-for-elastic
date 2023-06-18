package com.github.kreslavskikd.intellijpluginforelastic.actions

import com.github.kreslavskikd.intellijpluginforelastic.dialogs.QueryDialogWrapper
import com.github.kreslavskikd.intellijpluginforelastic.repo.InfoRepo
import com.github.kreslavskikd.intellijpluginforelastic.repo.PluginSettings
import com.github.kreslavskikd.intellijpluginforelastic.repo.SavingLogsType
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
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.DataContext
import com.intellij.openapi.actionSystem.LangDataKeys
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
import java.io.File
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Paths

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.io.path.ExperimentalPathApi
import kotlin.io.path.absolutePathString

class ShowQueryDialogAction : AnAction() {

    @OptIn(ExperimentalPathApi::class)
    override fun actionPerformed(e: AnActionEvent) {
        val dialog = QueryDialogWrapper(e.project!!)
        dialog.showAndGet()

        if (InfoRepo.savingLogsType == SavingLogsType.FILE_IN_DIR) {
            val settings = PluginSettings.getInstance().state
            File(settings.logsDirectory).mkdirs()
            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd-HH-mm")
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
            thisLogger().info("data logs stored to: " + outputFile.absolutePathString())
        } else if (InfoRepo.savingLogsType == SavingLogsType.SCRATCH_FILE) {

            val project = e.project ?: return
            val context = createContext(e, project)
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
    fun createContext(e: AnActionEvent, project: Project): ScratchFileCreationHelper.Context {
        val context = ScratchFileCreationHelper.Context()
        context.text = StringUtil.notNullize(InfoRepo.lastResult)

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
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd-HH-mm")
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