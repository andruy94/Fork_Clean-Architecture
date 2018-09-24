package ru.terrakok.gitlabclient.markdown

import org.commonmark.node.Node

interface ExtensionProcessor {
    fun process(args: String): Node?
}