package ru.terrakok.gitlabclient.toothpick.provider

import android.content.Context
import okhttp3.OkHttpClient
import ru.noties.markwon.SpannableConfiguration
import ru.noties.markwon.UrlProcessor
import ru.noties.markwon.il.AsyncDrawableLoader
import ru.noties.markwon.spans.SpannableTheme
import ru.terrakok.gitlabclient.R
import ru.terrakok.gitlabclient.extension.color
import ru.terrakok.gitlabclient.model.system.SchedulersProvider
import ru.terrakok.gitlabclient.presentation.global.MarkDownConverter
import java.net.URI
import java.util.concurrent.Executors
import javax.inject.Inject
import javax.inject.Provider

/**
 * Created by Konstantin Tskhovrebov (aka @terrakok) on 28.02.18.
 */
class MarkDownConverterProvider @Inject constructor(
    private val context: Context,
    private val httpClient: OkHttpClient,
    private val schedulers: SchedulersProvider
) : Provider<MarkDownConverter> {

    private val spannableTheme
        get() = SpannableTheme
            .builderWithDefaults(context)
            .codeBackgroundColor(context.color(R.color.beige))
            .build()
    private val asyncDrawableLoader
        get() = AsyncDrawableLoader.builder()
            .client(httpClient)
            .executorService(Executors.newCachedThreadPool())
            .resources(context.resources)
            .build()

    private val hostHolder = MarkDownConverter.ImageHostHolder("")
    private val urlProcessor = UrlProcessor { destination ->
        try {
            if (URI(destination).scheme.isNotEmpty()) return@UrlProcessor destination
        } catch (e: Exception) {
        }
        return@UrlProcessor hostHolder.url + destination
    }

    private val spannableConfig
        get() = SpannableConfiguration.builder(context)
            .asyncDrawableLoader(asyncDrawableLoader)
            .urlProcessor(urlProcessor)
            .theme(spannableTheme)
            .build()

    override fun get() = MarkDownConverter(
        spannableConfig,
        hostHolder,
        schedulers
    )
}