package com.example.study.app.abs

import android.content.Context
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.RecyclerView
import android.text.Html
import android.text.Html.FROM_HTML_MODE_COMPACT
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.PopupMenu
import android.widget.TextView
import android.widget.Toast
import com.example.study.DateUtils
import com.example.study.R
import com.example.study.StudyUtils
import com.example.study.model.abs.AbsWriterOfLog
import com.example.study.model.abs.BlockItem
import com.example.study.persistence.BlockStudyDatabase

class BlockSheetItemAdapter(
    private val activity: AppCompatActivity,
    private val context: Context,
    private val blockItem: BlockItem
) : RecyclerView.Adapter<BlockSheetItemAdapter.BlockContentViewHolder>() {

    private val openTime = System.currentTimeMillis()
    private var startTime = System.currentTimeMillis()
    private var endTime = 0L
    private var restCount = blockItem.contents!!.size
    private var correctCount = 0
    private var execTime = 0

    private val inflater = LayoutInflater.from(context)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BlockContentViewHolder {
        val blockContentLayout = inflater.inflate(R.layout.block_content, parent, false)

        readyHintButton(blockContentLayout)
        readyAnswerButton(blockContentLayout)

        val viewHolder = BlockContentViewHolder(blockContentLayout)
        return viewHolder
    }

    fun readyHintButton(blockContentLayout: View): View {
        val hintButton = blockContentLayout.findViewById<Button>(R.id.hint)
        hintButton.setOnClickListener {
            val seqNo = blockContentLayout.findViewById<TextView>(R.id.seqNo).text as String
            val hint = blockItem.contents!![Integer.parseInt(seqNo)].hint
            if (hint != "") {
                Toast.makeText(context, hint, Toast.LENGTH_LONG).show()
            }
        }
        return hintButton
    }

    fun readyAnswerButton(blockContentLayout: View) {
        val answerButton = blockContentLayout.findViewById<Button>(R.id.answer)
        val correctButton = blockContentLayout.findViewById<Button>(R.id.correct)
        val incorrectButton = blockContentLayout.findViewById<Button>(R.id.incorrect)

        correctButton.visibility = View.INVISIBLE
        incorrectButton.visibility = View.INVISIBLE

        // 正解ボタン押下
        correctButton.setOnClickListener {
            val seqNo = blockContentLayout.findViewById<TextView>(R.id.seqNo).text as String
            val db = BlockStudyDatabase(context).writableDatabase
            val recordKeeper = AbsWriterOfLog(db, blockItem)
            // 解答結果を記録する
            recordKeeper.insertCardAnswerLog(seqNo.toInt(), 1, startTime, endTime)
            execTime = execTime + ((endTime - startTime)/1000).toInt()

            // ボタン制御
            correctButton.isClickable = false
            incorrectButton.visibility = View.INVISIBLE

            ++correctCount

            postAnswer(recordKeeper)
            db.close()
        }

        // 不正解ボタン押下
        incorrectButton.setOnClickListener {
            val seqNo = blockContentLayout.findViewById<TextView>(R.id.seqNo).text.toString()
            val db = BlockStudyDatabase(context).writableDatabase
            val recordKeeper = AbsWriterOfLog(db, blockItem)
            // 解答結果を記録する
            recordKeeper.insertCardAnswerLog(seqNo.toInt(), 0, startTime, endTime)
            execTime = execTime + ((endTime - startTime)/1000).toInt()

            // ボタン制御
            correctButton.visibility = View.INVISIBLE
            incorrectButton.isClickable = false

            postAnswer(recordKeeper)
            db.close()
        }

        answerButton.setOnClickListener {
            endTime = System.currentTimeMillis()
            // 答えの表示
            val seqNo = blockContentLayout.findViewById<TextView>(R.id.seqNo).text as String
            val body = blockContentLayout.findViewById<TextView>(R.id.body)

            var txt = StudyUtils.openAnswer(blockItem.contents!![Integer.parseInt(seqNo)].Q)

            body.text = Html.fromHtml(txt, FROM_HTML_MODE_COMPACT)
            answerButton.visibility = View.INVISIBLE
            correctButton.visibility = View.VISIBLE
            incorrectButton.visibility = View.VISIBLE

            activity.supportActionBar?.setHomeButtonEnabled(false)
        }
    }

    // 解答後処理
    fun postAnswer(absWriterOfLog: AbsWriterOfLog) {

        --restCount

        if (restCount == 0) {
            absWriterOfLog.register(correctCount, openTime, endTime, execTime)

            activity.supportActionBar?.setHomeButtonEnabled(true)

        } else {
            startTime = System.currentTimeMillis()
        }
    }

    override fun getItemCount() = blockItem.contents!!.size

    override fun onBindViewHolder(holder: BlockContentViewHolder, position: Int) {
        val blockCard = blockItem.contents!![position]
        val cardAnswerLog = blockItem.cardAnswerLogs[position]

        var txt = StudyUtils.showHilight(blockCard.Q)

        holder.body.text = Html.fromHtml(txt, FROM_HTML_MODE_COMPACT)
        holder.no.text = position.toString()
        holder.cardOption.setOnClickListener {
            val popup = PopupMenu(context, holder.cardOption)
            popup.inflate(R.menu.card_option)
            popup.setOnMenuItemClickListener {
                when (it.itemId) {
                    R.id.editorQ -> {
                        activity.supportFragmentManager!!
                            .beginTransaction()
                            .replace(
                                R.id.root_layout,
                                CardEditorFragment.newInstance(blockItem, blockCard),
                                "cardeditor"
                            )
                            .addToBackStack(null)
                            .commit()
                    }
                }
                true
            }
            popup.show()
        }

        // 前回結果
        holder.lastPartExecDate.text =
            if (cardAnswerLog.endTime == null) "-" else cardAnswerLog.endTime.substring(0, 10)
        holder.lastPartLapTime.text =
            if (cardAnswerLog.elapsedTime == null) "-" else DateUtils.formatTime(cardAnswerLog.elapsedTime)
        holder.lastPartResult.text =
            if (cardAnswerLog.result == null) "-" else if (cardAnswerLog.result == 1) "⭕️" else "❌"
    }

    class BlockContentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val no = itemView.findViewById<TextView>(R.id.seqNo)
        // 問題
        val body = itemView.findViewById<TextView>(R.id.body)
        val cardOption = itemView.findViewById<TextView>(R.id.cardOptions)
        //
        val lastPartExecDate = itemView.findViewById<TextView>(R.id.lastPartExecDate)
        val lastPartLapTime = itemView.findViewById<TextView>(R.id.lastPartLapTime)
        val lastPartResult = itemView.findViewById<TextView>(R.id.lastPartResult)
    }
}