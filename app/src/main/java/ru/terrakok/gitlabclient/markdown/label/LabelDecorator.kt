package ru.terrakok.gitlabclient.markdown.label

import ru.terrakok.gitlabclient.markdown.GitlabMarkdownExtension
import ru.terrakok.gitlabclient.markdown.MarkdownDecorator

class LabelDecorator(
    val supportedLabels: List<String>
) : MarkdownDecorator {
    override fun decorate(markdown: String): String = LabelType
        .values()
        .fold(markdown) { acc, type ->
            type.regex.replace(acc) { matchResult ->
                val value = matchResult.groupValues[1]
                if (supportedLabels.contains(value)) {
                    "%%%%%${GitlabMarkdownExtension.LABEL}_${type.name}_$value%%%%%"
                } else {
                    matchResult.groupValues[0]
                }
            }
        }
}