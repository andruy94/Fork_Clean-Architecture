/*
* Modifications copyright (C) 2018 https://github.com/alorma/GitDiffTextView
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
* http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/

package ru.terrakok.gitlabclient.ui.global

import android.graphics.*
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.TextUtils
import android.text.style.ForegroundColorSpan
import android.text.style.LineBackgroundSpan
import android.widget.TextView

class MergeRequestChangeViewController(
    private val view: TextView
) {

    private val additionColor: Int = Color.parseColor("#CCFFCC")
    private val deletionColor: Int = Color.parseColor("#FFDDDD")
    private val additionTextColor: Int = Color.TRANSPARENT
    private val deletionTextColor: Int = Color.TRANSPARENT

    private var addedLineCount = 0
    private var deletedLineCount = 0

    fun setText(text: CharSequence) {
        if (!TextUtils.isEmpty(text)) {
            val diff = text.toString()
            val split = diff.split("\\r?\\n|\\r".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()

            if (split.isNotEmpty()) {
                val builder = SpannableStringBuilder()
                val lines = split.size

                for (i in 0 until lines) {
                    var token = split[i]
                    if (!token.startsWith("@@")) {
                        if (i < lines - 1) {
                            token += "\n"
                        }

                        val firstChar = token[0]
                        var color = Color.TRANSPARENT
                        var textColor = Color.TRANSPARENT

                        when (firstChar) {
                            '+' -> {
                                color = additionColor
                                textColor = additionTextColor
                                addedLineCount++
                            }
                            '-' -> {
                                color = deletionColor
                                textColor = deletionTextColor
                                deletedLineCount++
                            }
                        }

                        val spannableDiff = SpannableString(token)
                        // Span for line color (where transparent is considered as default)
                        if (color != Color.TRANSPARENT) {
                            val span = DiffLineSpan(color, view.paddingLeft)
                            spannableDiff.setSpan(span, 0, token.length, SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE)
                        }
                        // Span for text color (where transparent is considered as default)
                        if (textColor != Color.TRANSPARENT) {
                            val span = ForegroundColorSpan(textColor)
                            spannableDiff.setSpan(span, 0, token.length, SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE)
                        }

                        builder.append(spannableDiff)
                    }
                }
                view.text = builder
            }
        } else {
            view.text = text
        }
    }

    fun getAddedLineCount() = addedLineCount

    fun getDeletedLineCount() = deletedLineCount

    fun release() {
        addedLineCount = 0
        deletedLineCount = 0
    }

    private inner class DiffLineSpan(
        private val color: Int,
        private val padding: Int
    ) : LineBackgroundSpan {

        private val mTmpRect = Rect()

        override fun drawBackground(
            c: Canvas,
            p: Paint,
            left: Int,
            right: Int,
            top: Int,
            baseline: Int,
            bottom: Int,
            text: CharSequence,
            start: Int,
            end: Int,
            lnum: Int
        ) {
            // expand canvas bounds by padding
            val clipBounds = c.clipBounds
            clipBounds.inset(-padding, 0)
            c.clipRect(clipBounds, Region.Op.REPLACE)

            val paintColor = p.color
            p.color = color
            mTmpRect.set(left - padding, top, right + padding, bottom)
            c.drawRect(mTmpRect, p)
            p.color = paintColor
        }
    }
}