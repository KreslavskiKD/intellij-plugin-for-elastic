package com.github.kreslavskikd.intellijpluginforelastic

import com.intellij.testFramework.TestDataPath
import com.intellij.testFramework.fixtures.BasePlatformTestCase

@TestDataPath("\$CONTENT_ROOT/src/test/testData")
class PluginTest : BasePlatformTestCase() {

    override fun getTestDataPath() = "src/test/testData/rename"
}
