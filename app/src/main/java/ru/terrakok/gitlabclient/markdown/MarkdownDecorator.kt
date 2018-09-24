package ru.terrakok.gitlabclient.markdown

interface MarkdownDecorator {
    fun decorate(markdown: String): String
}