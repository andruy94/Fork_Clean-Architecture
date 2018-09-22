package ru.terrakok.gitlabclient.ui.drawer

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentTransaction
import android.support.v4.view.GravityCompat
import com.arellomobile.mvp.MvpView
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.drawer_flow_fragment.*
import ru.terrakok.cicerone.Navigator
import ru.terrakok.cicerone.NavigatorHolder
import ru.terrakok.cicerone.Router
import ru.terrakok.cicerone.android.SupportFragmentNavigator
import ru.terrakok.cicerone.commands.Command
import ru.terrakok.gitlabclient.R
import ru.terrakok.gitlabclient.Screens
import ru.terrakok.gitlabclient.extension.setLaunchScreen
import ru.terrakok.gitlabclient.presentation.drawer.NavigationDrawerView
import ru.terrakok.gitlabclient.presentation.global.GlobalMenuController
import ru.terrakok.gitlabclient.presentation.launch.DrawerFlowPresenter
import ru.terrakok.gitlabclient.toothpick.DI
import ru.terrakok.gitlabclient.toothpick.module.FlowNavigationModule
import ru.terrakok.gitlabclient.toothpick.module.GlobalMenuModule
import ru.terrakok.gitlabclient.ui.about.AboutFragment
import ru.terrakok.gitlabclient.ui.global.BaseFragment
import ru.terrakok.gitlabclient.ui.main.MainFlowFragment
import ru.terrakok.gitlabclient.ui.projects.ProjectsContainerFragment
import toothpick.Toothpick
import javax.inject.Inject

class DrawerFlowFragment : BaseFragment(), MvpView {
    @Inject
    lateinit var menuController: GlobalMenuController

    @Inject
    lateinit var navigatorHolder: NavigatorHolder

    private var menuStateDisposable: Disposable? = null

    override val layoutRes = R.layout.drawer_flow_fragment

    private val currentFragment
        get() = childFragmentManager.findFragmentById(R.id.mainContainer) as? BaseFragment

    private val drawerFragment
        get() = childFragmentManager.findFragmentById(R.id.navDrawerContainer) as? NavigationDrawerFragment

    @InjectPresenter
    lateinit var presenter: DrawerFlowPresenter

    @ProvidePresenter
    fun providePresenter(): DrawerFlowPresenter {
        return Toothpick
            .openScope(DI.DRAWER_FLOW_SCOPE)
            .getInstance(DrawerFlowPresenter::class.java)
    }

    private val navigator: Navigator by lazy {
        object : SupportFragmentNavigator(childFragmentManager, R.id.mainContainer) {

            override fun applyCommands(commands: Array<out Command>?) {
                super.applyCommands(commands)
                updateNavDrawer()
            }

            override fun exit() {
                presenter.onExit()
            }

            override fun createFragment(screenKey: String, data: Any?): Fragment? =
                Screens.createFragment(screenKey, data)

            override fun showSystemMessage(message: String?) {}

            override fun setupFragmentTransactionAnimation(
                command: Command?,
                currentFragment: Fragment?,
                nextFragment: Fragment?,
                fragmentTransaction: FragmentTransaction
            ) {
                //fix incorrect order lifecycle callback of MainFlowFragment
                fragmentTransaction.setReorderingAllowed(true)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        initScope()
        super.onCreate(savedInstanceState)

        if (childFragmentManager.fragments.isEmpty()) {
            childFragmentManager
                .beginTransaction()
                .replace(R.id.navDrawerContainer, NavigationDrawerFragment())
                .commitNow()

            navigator.setLaunchScreen(Screens.MAIN_FLOW)
        }
    }

    private fun initScope() {
        val scope = Toothpick.openScopes(DI.SERVER_SCOPE, DI.DRAWER_FLOW_SCOPE)
        scope.installModules(
            FlowNavigationModule(scope.getInstance(Router::class.java)),
            GlobalMenuModule()
        )
        Toothpick.inject(this@DrawerFlowFragment, scope)
    }

    override fun onResume() {
        super.onResume()
        menuStateDisposable = menuController.state.subscribe { openNavDrawer(it) }
        navigatorHolder.setNavigator(navigator)
    }

    override fun onPause() {
        navigatorHolder.removeNavigator()
        menuStateDisposable?.dispose()
        super.onPause()
    }

    //region nav drawer
    private fun openNavDrawer(open: Boolean) {
        if (open) drawerLayout.openDrawer(GravityCompat.START)
        else drawerLayout.closeDrawer(GravityCompat.START)
    }

    private fun updateNavDrawer() {
        childFragmentManager.executePendingTransactions()

        drawerFragment?.let { drawerFragment ->
            currentFragment?.let {
                when (it) {
                    is MainFlowFragment -> drawerFragment.onScreenChanged(NavigationDrawerView.MenuItem.ACTIVITY)
                    is ProjectsContainerFragment -> drawerFragment.onScreenChanged(NavigationDrawerView.MenuItem.PROJECTS)
                    is AboutFragment -> drawerFragment.onScreenChanged(NavigationDrawerView.MenuItem.ABOUT)
                }
            }
        }
    }
    //endregion

    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            openNavDrawer(false)
        } else {
            currentFragment?.onBackPressed() ?: presenter.onExit()
        }
    }
}