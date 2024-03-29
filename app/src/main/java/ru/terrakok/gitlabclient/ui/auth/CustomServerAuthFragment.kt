package ru.terrakok.gitlabclient.ui.auth

import android.content.Context
import android.os.Bundle
import android.support.design.widget.BottomSheetDialogFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.URLUtil
import android.widget.Toast
import kotlinx.android.synthetic.main.fragment_custom_server_auth.*
import ru.terrakok.gitlabclient.BuildConfig
import ru.terrakok.gitlabclient.R


/**
 * @author Konstantin Tskhovrebov (aka terrakok) on 17.09.17.
 */
class CustomServerAuthFragment : BottomSheetDialogFragment() {
    private lateinit var listener: OnClickListener

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        listener = parentFragment as OnClickListener
    }

    override fun onCreateView(
            inflater: LayoutInflater?,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? = inflater?.inflate(R.layout.fragment_custom_server_auth, container)

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (serverPathValue.text.isEmpty()) {
            serverPathValue.setText(BuildConfig.ORIGIN_GITLAB_ENDPOINT)
        }

        loginButton.setOnClickListener { login() }
        cancelButton.setOnClickListener { dismiss() }
    }

    private fun login() {
        val url = serverPathValue.text.toString()
        val token = privateTokenValue.text.toString()

        if (token.isEmpty()) {
            Toast.makeText(this.context, getString(R.string.empty_token), Toast.LENGTH_SHORT).show()
            return
        }

        if (URLUtil.isValidUrl(url)) {
            listener.customLogin.invoke(url, token)
            dismiss()
        } else {
            Toast.makeText(this.context, getString(R.string.invalid_server_url), Toast.LENGTH_SHORT).show()
        }
    }

    interface OnClickListener {
        val customLogin: (url: String, token: String) -> Unit
    }
}