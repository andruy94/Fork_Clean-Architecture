package ru.terrakok.gitlabclient.extension

import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.support.annotation.DrawableRes
import android.support.annotation.LayoutRes
import android.support.v4.app.Fragment
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.URLUtil
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.BitmapImageViewTarget
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import org.joda.time.DateTimeZone
import org.joda.time.format.DateTimeFormat
import retrofit2.HttpException
import ru.terrakok.cicerone.Router
import ru.terrakok.gitlabclient.R
import ru.terrakok.gitlabclient.Screens
import ru.terrakok.gitlabclient.entity.app.develop.LicenseType
import ru.terrakok.gitlabclient.entity.app.target.*
import ru.terrakok.gitlabclient.entity.event.EventAction
import ru.terrakok.gitlabclient.entity.todo.TodoAction
import ru.terrakok.gitlabclient.model.system.ResourceManager
import ru.terrakok.gitlabclient.model.system.flow.FlowRouter
import timber.log.Timber
import java.io.IOException
import java.util.*

/**
 * @author Konstantin Tskhovrebov (aka terrakok). Date: 03.03.17
 */
fun Resources.color(colorRes: Int) =
        if (Build.VERSION.SDK_INT >= 23) {
            this.getColor(colorRes, null)
        } else {
            this.getColor(colorRes)
        }

fun Disposable.addTo(compositeDisposable: CompositeDisposable) {
    compositeDisposable.add(this)
}

fun ViewGroup.inflate(@LayoutRes layoutRes: Int, attachToRoot: Boolean = false): View {
    return LayoutInflater.from(context).inflate(layoutRes, this, attachToRoot)
}

fun View.visible(visible: Boolean) {
    this.visibility = if (visible) View.VISIBLE else View.GONE
}

fun Throwable.userMessage(resourceManager: ResourceManager) = when (this) {
    is HttpException -> when (this.code()) {
        304 -> resourceManager.getString(R.string.not_modified_error)
        400 -> resourceManager.getString(R.string.bad_request_error)
        401 -> resourceManager.getString(R.string.unauthorized_error)
        403 -> resourceManager.getString(R.string.forbidden_error)
        404 -> resourceManager.getString(R.string.not_found_error)
        405 -> resourceManager.getString(R.string.method_not_allowed_error)
        409 -> resourceManager.getString(R.string.conflict_error)
        422 -> resourceManager.getString(R.string.unprocessable_error)
        500 -> resourceManager.getString(R.string.server_error_error)
        else -> resourceManager.getString(R.string.unknown_error)
    }
    is IOException -> resourceManager.getString(R.string.network_error)
    else -> resourceManager.getString(R.string.unknown_error)
}

private val DATE_FORMAT = DateTimeFormat.forPattern("dd.MM.yyyy")
fun Date.humanTime(resources: Resources): String {
    val localMillis = DateTimeZone.getDefault().convertUTCToLocal(this.time)
    val timeDelta = (System.currentTimeMillis() - localMillis) / 1000L
    val timeStr =
            if (timeDelta < 60) {
                resources.getString(R.string.time_sec, timeDelta)
            } else if (timeDelta < 60 * 60) {
                resources.getString(R.string.time_min, timeDelta / 60)
            } else if (timeDelta < 60 * 60 * 24) {
                resources.getString(R.string.time_hour, timeDelta / (60 * 60))
            } else if (timeDelta < 60 * 60 * 24 * 7) {
                resources.getString(R.string.time_day, timeDelta / (60 * 60 * 24))
            } else {
                return DATE_FORMAT.print(localMillis)
            }

    return resources.getString(R.string.time_ago, timeStr)
}

fun TextView.showTextOrHide(str: String?) {
    this.text = str
    this.visible(!str.isNullOrBlank())
}

fun Fragment.tryOpenLink(link: String?, basePath: String? = "https://google.com/search?q=") {
    if (link != null) {
        try {
            startActivity(Intent(
                    Intent.ACTION_VIEW,
                    when {
                        URLUtil.isValidUrl(link) -> Uri.parse(link)
                        else -> Uri.parse(basePath + link)
                    }
            ))
        } catch (e: Exception) {
            Timber.e("tryOpenLink error: $e")
            startActivity(Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("https://google.com/search?q=$link")
            ))
        }
    }
}

fun Fragment.shareText(text: String?) {
    text?.let {
        startActivity(Intent.createChooser(
                Intent(Intent.ACTION_SEND).apply {
                    type = "text/plain"
                    putExtra(Intent.EXTRA_TEXT, text)
                },
                getString(R.string.share_to)
        ))
    }
}

fun EventAction.getHumanName(resources: Resources) = when (this) {
    EventAction.UPDATED -> resources.getString(R.string.event_action_updated)
    EventAction.REOPENED -> resources.getString(R.string.event_action_reopened)
    EventAction.PUSHED_TO -> resources.getString(R.string.event_action_pushed_to)
    EventAction.PUSHED_NEW -> resources.getString(R.string.event_action_pushed_new)
    EventAction.PUSHED -> resources.getString(R.string.event_action_pushed)
    EventAction.LEFT -> resources.getString(R.string.event_action_left)
    EventAction.OPENED -> resources.getString(R.string.event_action_opened)
    EventAction.DESTROYED -> resources.getString(R.string.event_action_destroyed)
    EventAction.DELETED -> resources.getString(R.string.event_action_deleted)
    EventAction.EXPIRED -> resources.getString(R.string.event_action_expired)
    EventAction.MERGED -> resources.getString(R.string.event_action_merged)
    EventAction.CLOSED -> resources.getString(R.string.event_action_closed)
    EventAction.ACCEPTED -> resources.getString(R.string.event_action_accepted)
    EventAction.COMMENTED -> resources.getString(R.string.event_action_commented)
    EventAction.COMMENTED_ON -> resources.getString(R.string.event_action_commented_on)
    EventAction.JOINED -> resources.getString(R.string.event_action_joined)
    EventAction.CREATED -> resources.getString(R.string.event_action_created)
}

@DrawableRes
fun TargetHeaderIcon.getIcon() = when (this) {
    TargetHeaderIcon.CREATED -> R.drawable.ic_event_created_24dp
    TargetHeaderIcon.JOINED -> R.drawable.ic_event_joined_24dp
    TargetHeaderIcon.COMMENTED -> R.drawable.ic_event_commented_24dp
    TargetHeaderIcon.MERGED -> R.drawable.ic_event_merged_24dp
    TargetHeaderIcon.CLOSED -> R.drawable.ic_event_closed_24dp
    TargetHeaderIcon.DESTROYED -> R.drawable.ic_event_destroyed_24dp
    TargetHeaderIcon.EXPIRED -> R.drawable.ic_event_expired_24dp
    TargetHeaderIcon.LEFT -> R.drawable.ic_event_left_24dp
    TargetHeaderIcon.REOPENED -> R.drawable.ic_event_reopened_24dp
    TargetHeaderIcon.PUSHED -> R.drawable.ic_event_pushed_24dp
    TargetHeaderIcon.UPDATED -> R.drawable.ic_event_updated_24dp
    TargetHeaderIcon.NONE -> R.drawable.ic_event_created_24dp
}

fun TodoAction.getHumanName(resources: Resources): String = when (this) {
    TodoAction.APPROVAL_REQUIRED -> resources.getString(R.string.todo_action_approval_required)
    TodoAction.ASSIGNED -> resources.getString(R.string.todo_action_assigned)
    TodoAction.BUILD_FAILED -> resources.getString(R.string.todo_action_build_failed)
    TodoAction.DIRECTLY_ADDRESSED -> resources.getString(R.string.todo_action_directly_addressed)
    TodoAction.MARKED -> resources.getString(R.string.todo_action_marked)
    TodoAction.MENTIONED -> resources.getString(R.string.todo_action_mentioned)
}

fun TargetHeaderTitle.getHumanName(resources: Resources) = when (this) {
    is TargetHeaderTitle.Event -> {
        "$userName ${action.getHumanName(resources)} $targetName ${resources.getString(R.string.at)} $projectName"
    }
    is TargetHeaderTitle.Todo -> {
        val actionName = action.getHumanName(resources)
        val author = if (isAuthorCurrentUser) {
            resources.getString(R.string.you).capitalize()
        } else {
            authorUserName
        }
        val assignee = if (isAssigneeCurrentUser) {
            if (isAuthorCurrentUser) {
                resources.getString(R.string.yourself)
            } else {
                resources.getString(R.string.you)
            }
        } else {
            assigneeUserName
        }

        when (action) {
            TodoAction.ASSIGNED -> {
                "$author $actionName $targetName ${resources.getString(R.string.at)} $projectName ${resources.getString(R.string.to)} $assignee"
            }
            TodoAction.DIRECTLY_ADDRESSED,
            TodoAction.MENTIONED -> {
                "$author $actionName $assignee ${resources.getString(R.string.on)} $targetName ${resources.getString(R.string.at)} $projectName"
            }
            TodoAction.MARKED -> {
                "$author $actionName ${resources.getString(R.string.for_str)} $targetName ${resources.getString(R.string.at)} $projectName"
            }
            else -> {
                Timber.e("Unsupported template for todo action=$actionName.")
                "$author $actionName $targetName $assignee ${resources.getString(R.string.at)}"
            }
        }
    }
}

fun LicenseType.getHumanName(resources: Resources) = when (this) {
    LicenseType.MIT -> resources.getString(R.string.library_license_MIT)
    LicenseType.APACHE_2 -> resources.getString(R.string.library_license_APACHE_2)
    LicenseType.CUSTOM -> resources.getString(R.string.library_license_CUSTOM)
    LicenseType.NONE -> resources.getString(R.string.library_license_NONE)
}

fun TargetBadgeStatus.getHumanName(resources: Resources) = when (this) {
    TargetBadgeStatus.OPENED -> resources.getString(R.string.target_status_opened)
    TargetBadgeStatus.CLOSED -> resources.getString(R.string.target_status_closed)
    TargetBadgeStatus.MERGED -> resources.getString(R.string.target_status_merged)
}

fun TargetBadgeStatus.getBadgeColors(resources: Resources) = when (this) {
    TargetBadgeStatus.OPENED -> Pair(resources.color(R.color.green), resources.color(R.color.lightGreen))
    TargetBadgeStatus.CLOSED -> Pair(resources.color(R.color.red), resources.color(R.color.lightRed))
    TargetBadgeStatus.MERGED -> Pair(resources.color(R.color.blue), resources.color(R.color.lightBlue))
}

fun Fragment.sendEmail(email: String?) {
    if (email != null) {
        startActivity(Intent.createChooser(
                Intent(Intent.ACTION_SENDTO, Uri.fromParts("mailto", email, null)),
                null
        ))
    }
}

fun ImageView.loadRoundedImage(
        url: String?,
        ctx: Context? = null
) {
    Glide.with(ctx ?: context)
            .load(url)
            .asBitmap()
            .centerCrop()
            .into(object : BitmapImageViewTarget(this) {
                override fun onLoadStarted(placeholder: Drawable?) {
                    setImageResource(R.drawable.default_img)
                }

                override fun onLoadFailed(e: java.lang.Exception?, errorDrawable: Drawable?) {
                    setImageResource(R.drawable.default_img)
                }

                override fun setResource(resource: Bitmap?) {
                    resource?.let {
                        RoundedBitmapDrawableFactory.create(view.resources, it).run {
                            this.isCircular = true
                            setImageDrawable(this)
                        }
                    }
                }
            })
}

fun TargetHeader.openInfo(router: FlowRouter) {
    when(target) {
        AppTarget.PROJECT -> {
            router.startFlow(Screens.PROJECT_FLOW, targetId)
        }
        AppTarget.USER -> {
            router.startFlow(Screens.USER_FLOW, targetId)
        }
        AppTarget.MERGE_REQUEST -> {
            internal?.let { targetInternal ->
                router.startFlow(
                        Screens.MR_FLOW,
                        Pair(targetInternal.projectId, targetInternal.targetIid)
                )
            }
        }
        AppTarget.ISSUE -> {
            internal?.let { targetInternal ->
                router.startFlow(
                        Screens.MR_FLOW,
                        Pair(targetInternal.projectId, targetInternal.targetIid)
                )
            }
        }
    }
}