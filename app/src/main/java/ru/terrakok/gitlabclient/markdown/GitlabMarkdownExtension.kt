package ru.terrakok.gitlabclient.markdown

enum class GitlabMarkdownExtension {
    LABEL;

    companion object {
        fun byString(value: String): GitlabMarkdownExtension? = values().firstOrNull { it.toString() == value }
    }
}