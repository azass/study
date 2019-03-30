package com.example.study.app.abs

import android.content.Context
import android.support.v7.widget.AppCompatEditText
import android.text.Html
import android.util.AttributeSet
import android.view.ActionMode
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import com.example.study.R
import com.example.study.StudyUtils

class CardEditText @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
    AppCompatEditText(context, attrs, defStyleAttr) {

    var textQ = ""
    var isWritable = false

    init {
        customSelectionActionModeCallback = object : ActionMode.Callback {
            override fun onActionItemClicked(mode: ActionMode?, item: MenuItem?): Boolean {
                when (item!!.itemId) {
                    R.id.action_ok -> {
                        if (isWritable) {
                            textQ = StudyUtils.highlight(text.toString(), selectionStart, selectionEnd)
                            setText(textQ, TextView.BufferType.SPANNABLE)
                        } else {
                            textQ = StudyUtils.highlight(textQ, selectionStart, selectionEnd)
                            val txt = StudyUtils.showHilight(textQ)
                            setText(Html.fromHtml(txt, Html.FROM_HTML_MODE_COMPACT), TextView.BufferType.SPANNABLE)
                        }
                    }
//                    R.id.action_cancel -> {
//                        mode!!.finish()
//                        return true
//                    }
                }
                return false
            }

            override fun onCreateActionMode(mode: ActionMode?, menu: Menu?): Boolean {
                menu!!.removeItem(android.R.id.copy)
                menu!!.removeItem(android.R.id.cut)
                menu!!.removeItem(android.R.id.paste)
                menu!!.removeItem(android.R.id.shareText)
                menu!!.removeItem(16908319)

                mode!!.menuInflater.inflate(R.menu.highlight_menu, menu!!)
                return true
            }

            override fun onPrepareActionMode(mode: ActionMode?, menu: Menu?): Boolean {
                menu!!.removeItem(android.R.id.copy)
                menu!!.removeItem(android.R.id.cut)
                menu!!.removeItem(android.R.id.paste)
                menu!!.removeItem(android.R.id.shareText)
                menu!!.removeItem(16908319)

                return true
            }

            override fun onDestroyActionMode(mode: ActionMode?) {
                // nothing
            }
        }
    }
}
