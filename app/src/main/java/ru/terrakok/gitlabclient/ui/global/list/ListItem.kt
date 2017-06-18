package ru.terrakok.gitlabclient.ui.global.list

import ru.terrakok.gitlabclient.entity.common.Issue
import ru.terrakok.gitlabclient.entity.common.Project

/**
 * @author Konstantin Tskhovrebov (aka terrakok) on 18.06.17.
 */
sealed class ListItem {
    class ProgressItem : ListItem()
    class ProjectItem(val project: Project) : ListItem()
    class IssueItem(val issue: Issue) : ListItem()
}