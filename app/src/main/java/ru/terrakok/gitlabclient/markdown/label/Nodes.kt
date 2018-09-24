package ru.terrakok.gitlabclient.markdown.label

import org.commonmark.node.CustomNode
import org.commonmark.node.Delimited
import ru.terrakok.gitlabclient.entity.Label
import ru.terrakok.gitlabclient.markdown.GitlabExtensionsDelimiterProcessor

data class LabelNode(
    val label: Label
) : CustomNode(), Delimited {
    override fun getOpeningDelimiter(): String =
        GitlabExtensionsDelimiterProcessor.DELIMITER_STRING

    override fun getClosingDelimiter(): String =
        GitlabExtensionsDelimiterProcessor.DELIMITER_STRING

}