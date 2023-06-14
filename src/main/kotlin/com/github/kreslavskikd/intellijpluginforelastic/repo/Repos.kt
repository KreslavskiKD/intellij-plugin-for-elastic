package com.github.kreslavskikd.intellijpluginforelastic.repo

object InfoRepo {

    var elasticAddress: String = "http://localhost"

    var logsDir: String = "./logs"

    var query: String = ""

    var lastResult: String = ""
}

object Settings {
    var selectedQueryType = QueryType.QUERY_PARAMS
    var savingLogsType = SavingLogsType.FILE_IN_DIR
}

object Constants {
    const val ELASTIC_PORT = "9200"

    const val queryBaseStart = """{"query": """
    const val queryBaseEnd = "}"
}

enum class QueryType {
    QUERY_STRING,
    QUERY_PARAMS,
    JSON,
}
enum class SavingLogsType {
    SCRATCH_FILE,
    FILE_IN_DIR,
}