package ru.terrakok.gitlabclient.markdown.label

import org.commonmark.parser.Parser
import org.junit.Before
import org.junit.Test
import ru.terrakok.gitlabclient.entity.Label
import ru.terrakok.gitlabclient.markdown.GitlabExtensionsDelimiterProcessor
import ru.terrakok.gitlabclient.markdown.GitlabMarkdownExtension

class GitlabExtensionsDelimiterProcessorTest {

    private lateinit var decorator: LabelDecorator
    private lateinit var parser: Parser
    private lateinit var labels: List<Label>

    @Before
    fun setUp() {
        labels = listOf(LABEL)
        decorator = LabelDecorator(listOf(LABEL_STR))
        parser = with(Parser.Builder()) {
            customDelimiterProcessor(
                GitlabExtensionsDelimiterProcessor(
                    mapOf(
                        GitlabMarkdownExtension.LABEL to LabelExtensionProcessor(labels)
                    )
                )
            )
            build()
        }
    }

    @Test
    fun `should replace decorated label extension with label node`() {
        val parsed = parser.parse(decorator.decorate("~$LABEL_STR"))
        assert(parsed.firstChild.firstChild == LabelNode(LABEL))
    }

    companion object {
        const val LABEL_STR = "single"
        val LABEL = Label(
            id = 0,
            name = "single",
            color = "#fff",
            description = null,
            openIssuesCount = 0,
            closedIssuesCount = 0,
            openMergeRequestsCount = 0,
            subscribed = false,
            priority = null
        )

    }
}
