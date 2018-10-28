package ru.terrakok.gitlabclient.presentation.mergerequest.changes

import com.arellomobile.mvp.InjectViewState
import ru.terrakok.gitlabclient.entity.mergerequest.MergeRequestChange
import ru.terrakok.gitlabclient.model.interactor.mergerequest.MergeRequestInteractor
import ru.terrakok.gitlabclient.presentation.global.BasePresenter
import ru.terrakok.gitlabclient.presentation.global.ErrorHandler
import ru.terrakok.gitlabclient.toothpick.PrimitiveWrapper
import ru.terrakok.gitlabclient.toothpick.qualifier.MergeRequestId
import ru.terrakok.gitlabclient.toothpick.qualifier.ProjectId
import javax.inject.Inject

/**
 * Created by Konstantin Tskhovrebov (aka @terrakok) on 12.02.18.
 */
@InjectViewState
class MergeRequestChangesPresenter @Inject constructor(
    @ProjectId projectIdWrapper: PrimitiveWrapper<Long>,
    @MergeRequestId mrIdWrapper: PrimitiveWrapper<Long>,
    private val mrInteractor: MergeRequestInteractor,
    private val errorHandler: ErrorHandler
) : BasePresenter<MergeRequestChangesView>() {

    private val projectId = projectIdWrapper.value
    private val mrId = mrIdWrapper.value

    private val changes = arrayListOf<MergeRequestChange>()
    private var isEmptyError: Boolean = false

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()

        mrInteractor
            .getMergeRequestChanges(projectId, mrId)
            .doOnSubscribe { viewState.showEmptyProgress(true) }
            .doAfterTerminate { viewState.showEmptyProgress(false) }
            .subscribe(
                {
                    if (it.isNotEmpty()) {
                        changes.addAll(it)
                        viewState.showChanges(true, it)
                    } else {
                        viewState.showChanges(false, it)
                        viewState.showEmptyView(true)
                    }
                },
                {
                    isEmptyError = true
                    errorHandler.proceed(it, { viewState.showEmptyError(true, it) })
                }
            )
            .connect()
    }

    fun refreshChanges() {
        mrInteractor
            .getMergeRequestChanges(projectId, mrId)
            .doOnSubscribe {
                if (isEmptyError) {
                    viewState.showEmptyError(false, null)
                    isEmptyError = false
                }
                if (changes.isEmpty()) {
                    viewState.showEmptyView(false)
                }
                if (changes.isNotEmpty()) {
                    viewState.showRefreshProgress(true)
                } else {
                    viewState.showEmptyProgress(true)
                }
            }
            .subscribe(
                {
                    if (changes.isNotEmpty()) {
                        viewState.showRefreshProgress(false)
                    } else {
                        viewState.showEmptyProgress(false)
                    }
                    changes.clear()
                    if (it.isNotEmpty()) {
                        changes.addAll(it)
                        viewState.showChanges(true, it)
                    } else {
                        viewState.showChanges(false, it)
                        viewState.showEmptyView(true)
                    }
                },
                {
                    if (changes.isNotEmpty()) {
                        viewState.showRefreshProgress(false)
                    } else {
                        viewState.showEmptyProgress(false)
                    }
                    errorHandler.proceed(
                        it,
                        {
                            if (changes.isNotEmpty()) {
                                viewState.showMessage(it)
                            } else {
                                isEmptyError = true
                                viewState.showEmptyError(true, it)
                            }
                        }
                    )
                }
            )
            .connect()
    }
}