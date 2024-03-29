package ru.terrakok.gitlabclient.ui.projects

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentPagerAdapter
import kotlinx.android.synthetic.main.fragment_my_issues_container.*
import ru.terrakok.cicerone.Router
import ru.terrakok.gitlabclient.R
import ru.terrakok.gitlabclient.presentation.global.GlobalMenuController
import ru.terrakok.gitlabclient.presentation.projects.ProjectsListPresenter
import ru.terrakok.gitlabclient.toothpick.DI
import ru.terrakok.gitlabclient.ui.global.BaseFragment
import toothpick.Toothpick
import javax.inject.Inject


class ProjectsContainerFragment : BaseFragment() {
    @Inject lateinit var menuController: GlobalMenuController
    @Inject lateinit var router: Router

    override val layoutRes = R.layout.fragment_projects_container

    private val adapter: ProjectsPagesAdapter by lazy { ProjectsPagesAdapter() }

    override fun onCreate(savedInstanceState: Bundle?) {
        Toothpick.inject(this, Toothpick.openScope(DI.MAIN_ACTIVITY_SCOPE))
        super.onCreate(savedInstanceState)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        toolbar.setNavigationOnClickListener { menuController.open() }
        viewPager.adapter = adapter
    }

    override fun onBackPressed() {
        router.exit()
    }

    private inner class ProjectsPagesAdapter : FragmentPagerAdapter(childFragmentManager) {
        private val pages = listOf<Fragment>(
                ProjectsListFragment.newInstance(ProjectsListPresenter.MAIN_PROJECTS),
                ProjectsListFragment.newInstance(ProjectsListPresenter.MY_PROJECTS),
                ProjectsListFragment.newInstance(ProjectsListPresenter.STARRED_PROJECTS)
        )
        private val pageTitles = listOf<String>(
                getString(R.string.all_projects_title),
                getString(R.string.my_projects_title),
                getString(R.string.starred_projects_title)
        )

        override fun getItem(position: Int) = pages[position]

        override fun getCount() = pages.size

        override fun getPageTitle(position: Int) = pageTitles[position]
    }
}