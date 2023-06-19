package com.github.kreslavskikd.intellijpluginforelastic.repo

import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.Service
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage

@Service
@State(name = "MyPluginSettings", storages = [Storage("ElasticLogsDownloaderSettings.xml")])
class PluginSettings : PersistentStateComponent<PluginSettings.State> {
    data class State(
        var elasticAddress: String = Constants.DEFAULT_ADDRESS,
        var logsDirectory: String = Constants.DEFAULT_LOGS_DIR,
    )

    private var state: State = State()

    companion object {
        fun getInstance(): PluginSettings {
            return com.intellij.openapi.components.ServiceManager.getService(PluginSettings::class.java)
        }
    }

    override fun getState(): State {
        return state
    }

    override fun loadState(state: State) {
        this.state = state
    }
}