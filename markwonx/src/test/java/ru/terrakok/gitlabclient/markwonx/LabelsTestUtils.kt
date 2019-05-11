package ru.terrakok.gitlabclient.markwonx

import ru.terrakok.gitlabclient.markwonx.label.LabelDescription
import ru.terrakok.gitlabclient.markwonx.label.LabelType

object LabelsTestUtils {
    val INEXISTENT = TestLabel(LabelType.SINGLE, LabelDescription(0, "inexistent", "#000000"))
    val SINGLE = TestLabel(LabelType.SINGLE, LabelDescription(0, "single", "#000000"))
    val SINGLE_CYRILLIC = TestLabel(LabelType.SINGLE, LabelDescription(1, "РусскийОдноСлово", "#111111"))
    val MULTIPLE = TestLabel(LabelType.MULTIPLE, LabelDescription(2, "Multiword english", "#222222"))
    val ID = TestLabel(LabelType.ID, LabelDescription(3, "Test ID Label", "#333333"))

    val EXISTENT_LABELS by lazy { listOf(SINGLE, SINGLE_CYRILLIC, MULTIPLE, ID) }

    fun getLabelContent(label: TestLabel): String {
        return when (label.type) {
            LabelType.ID -> label.label.id.toString()
            else -> label.label.name
        }
    }

    fun makeLabel(label: TestLabel): String {
        val content = getLabelContent(label)
        return when (label.type) {
            LabelType.MULTIPLE -> "~\"$content\""
            else -> "~$content"
        }
    }

    fun decorateForTest(label: TestLabel): Any? {
        return "${GitlabExtensionsDelimiterProcessor.DELIMITER_START}LABEL_${label.type}_${getLabelContent(label)}${GitlabExtensionsDelimiterProcessor.DELIMITER_START}"
    }

}