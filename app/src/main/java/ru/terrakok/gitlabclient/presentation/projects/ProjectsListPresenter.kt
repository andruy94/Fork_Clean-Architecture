package ru.terrakok.gitlabclient.presentation.projects

import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter
import io.reactivex.disposables.Disposable
import ru.terrakok.cicerone.Router
import ru.terrakok.gitlabclient.Screens
import ru.terrakok.gitlabclient.extension.userMessage
import ru.terrakok.gitlabclient.model.interactor.projects.MainProjectsListInteractor
import ru.terrakok.gitlabclient.model.system.ResourceManager
import ru.terrakok.gitlabclient.toothpick.PrimitiveWrapper
import ru.terrakok.gitlabclient.toothpick.qualifier.ProjectListMode
import timber.log.Timber
import javax.inject.Inject

/**
 * @author Konstantin Tskhovrebov (aka terrakok). Date: 30.03.17
 */

@InjectViewState
class ProjectsListPresenter @Inject constructor(
        @ProjectListMode private val modeWrapper: PrimitiveWrapper<Int>,
        private val router: Router,
        private val mainProjectsListInteractor: MainProjectsListInteractor,
        private val resourceManager: ResourceManager
) : MvpPresenter<ProjectsListView>() {

    companion object {
        const val MAIN_PROJECTS = 0
        const val MY_PROJECTS = 1
        const val STARRED_PROJECTS = 2
    }

    private val mode = modeWrapper.value
    private var currentPage = 0
    private var disposable: Disposable? = null

    override fun onFirstViewAttach() {
        requestFirstPage()
    }

    private fun requestProjects(page: Int) {
        Timber.d("requestProjects: $page")

        if (page == 1) {
            disposable?.dispose()
            disposable = null
        }

        if (disposable == null) {
            disposable = getProjectsSingle(page)
                    .doOnSubscribe { if (page == 1) viewState.showProgress(true) else viewState.showPageProgress(true) }
                    .doOnEvent { _, _ -> if (page == 1) viewState.showProgress(false) else viewState.showPageProgress(false) }
                    .doOnEvent { _, _ -> disposable = null }
                    .subscribe(
                            { projects ->
                                Timber.d("getProjects: ${projects.size}")
                                if (projects.isNotEmpty()) {
                                    currentPage = page
                                    if (page == 1) viewState.clearData()
                                    viewState.setNewData(projects)
                                }
                            },
                            { error ->
                                Timber.e("getProjects: $error")
                                viewState.showMessage(error.userMessage(resourceManager))
                            }
                    )
        }
    }

    private fun getProjectsSingle(page: Int) = when (mode) {
        STARRED_PROJECTS -> mainProjectsListInteractor.getStarredProjects(page)
        MY_PROJECTS -> mainProjectsListInteractor.getMyProjects(page)
        else -> mainProjectsListInteractor.getMainProjects(page)
    }

    override fun onDestroy() {
        disposable?.dispose()
    }

    fun requestFirstPage() = requestProjects(1)
    fun requestNextPage() = requestProjects(currentPage + 1)

    fun onProjectClicked(id: Long) = router.navigateTo(Screens.PROJECT_SCREEN, id)
    fun onBackPressed() = router.exit()
}