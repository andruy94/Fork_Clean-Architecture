package ru.terrakok.gitlabclient.model.data.server.interceptor

import okhttp3.Interceptor
import okhttp3.Response
import ru.terrakok.gitlabclient.model.data.auth.AuthHolder

/**
 * @author Konstantin Tskhovrebov (aka terrakok) on 23.04.17.
 */
class AuthHeaderInterceptor(private val authData: AuthHolder) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        var request = chain.request()
        authData.getAuthToken()?.let {
            request = request.newBuilder().addHeader("Authorization", "Bearer " + it).build()
        }
        return chain.proceed(request)
    }
}