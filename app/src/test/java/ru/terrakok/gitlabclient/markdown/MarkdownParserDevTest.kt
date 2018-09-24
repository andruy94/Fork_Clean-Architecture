package ru.terrakok.gitlabclient.markdown

import org.commonmark.parser.Parser
import org.junit.Before
import org.junit.Test

class MarkdownParserDevTest {

    lateinit var parser: Parser

    @Before
    fun setUp() {
        parser = Parser.builder().build()
    }

    @Test
    fun `should parse labels`() {
        val node = parser.parse(LABELS_LINE)
        node.firstChild
    }

    companion object {
        const val LABELS_LINE = "~\"Test 2\" ~bug ~white ~\"Русский лейбл\" ~\"лейбол\" ~7988370"
    }

}