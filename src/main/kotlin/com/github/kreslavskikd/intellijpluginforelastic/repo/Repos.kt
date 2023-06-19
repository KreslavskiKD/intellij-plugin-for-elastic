package com.github.kreslavskikd.intellijpluginforelastic.repo

object InfoRepo {
    var query: String = ""

    var selectedQueryType = QueryType.QUERY_PARAMS
    var savingLogsType = SavingLogsType.FILE_IN_DIR
}

object Constants {
    const val ELASTIC_PORT = "9200"

    const val queryBaseStart = """{"query": """
    const val queryBaseEnd = "}"

    const val DEFAULT_ADDRESS: String = "http://localhost"
    const val DEFAULT_LOGS_DIR: String = "./logs"
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