package com.example.study.app.abs

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.example.study.*
import com.example.study.model.abs.BlockCard
import com.example.study.model.abs.BlockItem
import com.example.study.persistence.abs.BlockCardDao
import com.example.study.persistence.BlockStudyDatabase
import java.io.Serializable

class CardEditorFragment: Fragment() {

    private lateinit var blockCard: BlockCard

    companion object {
        fun newInstance(blockItem: BlockItem, blockContent: BlockCard): CardEditorFragment {
            val args = Bundle()
            args.putSerializable(BLOCK_ITEM, blockItem as Serializable)
            args.putSerializable(BLOCK_CARD, blockContent as Serializable)
            val fragment = CardEditorFragment()
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        (activity as AppCompatActivity).supportActionBar?.title = "抽象化ブロックシート編集"
        val view = inflater.inflate(R.layout.fragment_card_editor, container, false)

        val blockItem = arguments?.getSerializable(BLOCK_ITEM) as BlockItem
        blockCard = arguments?.getSerializable(BLOCK_CARD) as BlockCard

        val editQ = view.findViewById<EditText>(R.id.editQ) as CardEditText
        editQ.textQ = blockCard.Q
        editQ.setKeyListener(null)
        editQ.setTextIsSelectable(true)
        var txt = StudyUtils.showHilight(blockCard.Q)
        editQ.setText(Html.fromHtml(txt, Html.FROM_HTML_MODE_COMPACT), TextView.BufferType.SPANNABLE)

        val editHint = view.findViewById<EditText>(R.id.editHint)
        editHint.setText(blockCard.hint, TextView.BufferType.SPANNABLE)

        val saveQButton = view.findViewById<Button>(R.id.saveQ)
        saveQButton.setOnClickListener {
            val editQ = view.findViewById<CardEditText>(R.id.editQ)

            if (editQ.isWritable) {
                blockCard.Q = editQ.text.toString()
            } else {
                blockCard.Q = StudyUtils.replaceHighlight(editQ.textQ)
            }
            editQ.isWritable = false

            val db = BlockStudyDatabase(context!!).writableDatabase
            val blockCardDao = BlockCardDao(db)
            blockCardDao.updateQ(blockCard)
            db.close()
            Toast.makeText(context, "保存しました", Toast.LENGTH_SHORT).show()

            editQ.textQ = blockCard.Q
            editQ.setKeyListener(null)
            editQ.setTextIsSelectable(true)
            var txt = StudyUtils.showHilight(blockCard.Q)
            editQ.setText(Html.fromHtml(txt, Html.FROM_HTML_MODE_COMPACT), TextView.BufferType.SPANNABLE)
            editQ.setBackgroundColor(getResources().getColor(R.color.background_material_light))
        }

        val cancelQButton = view.findViewById<Button>(R.id.cancelQ)
        cancelQButton.setOnClickListener {
            var txt = StudyUtils.showHilight(blockCard.Q)

            val editQ = view.findViewById<CardEditText>(R.id.editQ)
            editQ.setKeyListener(null)
            editQ.setTextIsSelectable(true)
            editQ.isWritable = false
            editQ.setText(Html.fromHtml(txt, Html.FROM_HTML_MODE_COMPACT), TextView.BufferType.SPANNABLE)
            editQ.setBackgroundColor(getResources().getColor(R.color.background_material_light))
        }

        val writeQButton = view.findViewById<Button>(R.id.writeQ)
        writeQButton.setOnClickListener {
            val editQ = view.findViewById<CardEditText>(R.id.editQ)
            editQ.setKeyListener(EditText(context!!.applicationContext).keyListener)
            editQ.isWritable = true
            editQ.setText(blockCard.Q, TextView.BufferType.SPANNABLE)
            editQ.setBackgroundColor(getResources().getColor(R.color.colorNavIcon))
        }

        val saveHintButton = view.findViewById<Button>(R.id.saveHint)
        saveHintButton.setOnClickListener {
            closeKeyboard(activity!!)
            val editHint= view.findViewById<EditText>(R.id.editHint)
            blockCard.hint = editHint.text.toString()
            val db = BlockStudyDatabase(context!!).writableDatabase
            val blockCardDao = BlockCardDao(db)
            blockCardDao.updateHint(blockCard)
            db.close()
            Toast.makeText(context, "保存しました", Toast.LENGTH_SHORT).show()
        }

        val cancelHintButton = view.findViewById<Button>(R.id.cancelHint)
        cancelHintButton.setOnClickListener {
            val editHint = view.findViewById<EditText>(R.id.editHint)
            editHint.setText(blockCard.hint, TextView.BufferType.SPANNABLE)
        }

        return view
    }
}