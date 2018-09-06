package ru.terrakok.gitlabclient.ui.project

import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import kotlinx.android.synthetic.main.fragment_project_info.*
import ru.noties.markwon.Markwon
import ru.terrakok.gitlabclient.R
import ru.terrakok.gitlabclient.entity.Project
import ru.terrakok.gitlabclient.entity.Visibility
import ru.terrakok.gitlabclient.extension.loadRoundedImage
import ru.terrakok.gitlabclient.extension.showSnackMessage
import ru.terrakok.gitlabclient.extension.visible
import ru.terrakok.gitlabclient.presentation.project.info.ProjectInfoPresenter
import ru.terrakok.gitlabclient.presentation.project.info.ProjectInfoView
import ru.terrakok.gitlabclient.toothpick.DI
import ru.terrakok.gitlabclient.ui.global.BaseFragment
import toothpick.Toothpick

/**
 * @author Konstantin Tskhovrebov (aka terrakok) on 27.04.17.
 */
class ProjectInfoFragment : BaseFragment(), ProjectInfoView {

    override val layoutRes = R.layout.fragment_project_info

    @InjectPresenter
    lateinit var presenter: ProjectInfoPresenter

    @ProvidePresenter
    fun providePresenter(): ProjectInfoPresenter =
        Toothpick
            .openScopes(DI.PROJECT_FLOW_SCOPE)
            .getInstance(ProjectInfoPresenter::class.java)

    override fun showProject(project: Project, mdReadme: CharSequence) {
        (parentFragment as? ToolbarConfigurator)?.apply {
            setTitle(project.name)
            setShareUrl(project.webUrl)
        }
        titleTextView.text = project.nameWithNamespace
        descriptionTextView.text = project.description
        starsTextView.text = project.starCount.toString()
        forksTextView.text = project.forksCount.toString()

        avatarImageView.loadRoundedImage(project.avatarUrl, context)
        iconImageView.setBackgroundResource(R.drawable.circle)
        iconImageView.setImageResource(
            when (project.visibility) {
                Visibility.PRIVATE -> R.drawable.ic_lock_white_18dp
                Visibility.INTERNAL -> R.drawable.ic_security_white_24dp
                else -> R.drawable.ic_globe_18dp
            }
        )

        Markwon.setText(readmeTextView, mdReadme)
    }

    override fun showProgress(show: Boolean) {
        showProgressDialog(show)
        projectInfoLayout.visible(!show)
    }

    override fun showMessage(message: String) {
        showSnackMessage(message)
    }

    interface ToolbarConfigurator {
        fun setTitle(title: String)
        fun setShareUrl(url: String?)
    }
}