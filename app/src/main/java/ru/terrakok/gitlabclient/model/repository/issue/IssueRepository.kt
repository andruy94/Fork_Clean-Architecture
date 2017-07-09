package ru.terrakok.gitlabclient.model.repository.issue

import ru.terrakok.gitlabclient.model.data.server.GitlabApi
import ru.terrakok.gitlabclient.model.system.SchedulersProvider
import javax.inject.Inject

/**
 * @author Konstantin Tskhovrebov (aka terrakok) on 14.06.17.
 */
class IssueRepository @Inject constructor(private val api: GitlabApi,
                                          private val schedulers: SchedulersProvider) {

    fun getMyIssues(page: Int, pageSize: Int = 20) =
            api.getMyIssues(page, pageSize)
                    .subscribeOn(schedulers.io())
                    .observeOn(schedulers.ui())
}