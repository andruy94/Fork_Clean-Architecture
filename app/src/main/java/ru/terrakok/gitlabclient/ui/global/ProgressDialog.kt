package ru.terrakok.gitlabclient.ui.global

import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ProgressBar
import ru.terrakok.gitlabclient.R

/**
 * @author Konstantin Tskhovrebov (aka terrakok). Date: 03.04.17
 */
class ProgressDialog : DialogFragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_FRAME, R.style.ProgressDialogStyle)
        isCancelable = false
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?)
            = ProgressBar(context)
}