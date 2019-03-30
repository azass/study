package com.example.study.app.abs

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.example.study.DateUtils
import com.example.study.R
import com.example.study.StudyUtils
import com.example.study.model.abs.AbsAnswerLog
import com.example.study.persistence.BlockStudyDatabase
import com.example.study.persistence.abs.AbsAnswerLogDao
import com.example.study.persistence.abs.AbsRecentResultsDao

class BlockSheetItemAnswerLogFragment : Fragment() {
    private lateinit var recyclerView: RecyclerView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        // アクションバーのタイトル
        (activity as AppCompatActivity).supportActionBar?.title = "解答履歴"
        // ビュー
        val view = inflater.inflate(R.layout.fragment_blocksheet_item_answer_log, container, false)
        val ctx = context ?: return view
        // AbsAnswerLogのレジーインスタンシエーション
        val blockSheetManager = (ctx as BlockSheetListener).getBlockSheetManager()
        val blockSheet = blockSheetManager.selectedBlockSheet
        val blockItem = blockSheetManager.getSelectedBlockItem()
        val blockNo = blockSheet!!.blockNo
        val itemNo = blockItem.itemNo

        val db = BlockStudyDatabase(context!!).readableDatabase
        val answerLogDao = AbsAnswerLogDao(db)
        val recentResultsDao = AbsRecentResultsDao(db)
        val recentResult = recentResultsDao.select(blockNo, itemNo)
        val absAnswerLogList =
            answerLogDao.find("blockNo = ? and itemNo = ?", arrayOf(blockNo, itemNo.toString()), "endTime")

        // 画面のタイトル
        // block_sheetのシートタイトル
        view.findViewById<TextView>(R.id.sheetTitle).text = blockNo + " " + blockSheet.title
        // block_sheetのサブタイトル
        view.findViewById<TextView>(R.id.itemTitle).text = itemNo.toString() + ". " + blockItem.subtitle

        /* 前回 */
        // 開始時間
        view.findViewById<TextView>(R.id.beginTime).text =
            if (recentResult != null) DateUtils.formatDateAndTime(recentResult.beginTime) else "-"
        // 前回時間
        view.findViewById<TextView>(R.id.updateTime).text =
            if (recentResult != null) DateUtils.formatDateAndTime(recentResult.updateTime) else "-"
        // 正解回数
        view.findViewById<TextView>(R.id.correctTimes).text =
            if (recentResult != null) recentResult.correctTimes.toString() else "-"
        // ステータス
        val absStatus = view.findViewById<TextView>(R.id.absStatus)
        absStatus.text = if (recentResult != null) StudyUtils.getStatusLabel(recentResult.status) else "-"

        // 各カテゴリーの抽象化ブロックシートリスト表示
        recyclerView = view.findViewById(R.id.answerLogList)
        recyclerView.layoutManager = LinearLayoutManager(ctx, LinearLayoutManager.VERTICAL, false)
        recyclerView.addItemDecoration(DividerItemDecoration(ctx, DividerItemDecoration.VERTICAL)) // 枠線
        recyclerView.adapter = BlockSheetItemAnswerLogAdapter(ctx, absAnswerLogList)
        return view
    }
    // アダプタクラス
    internal inner class BlockSheetItemAnswerLogAdapter(
        private val context: Context,
        private val absAnswerLogList: List<AbsAnswerLog>) : RecyclerView.Adapter<AbsAnswerLogViewHolder>() {

        private val inflater = LayoutInflater.from(context)

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AbsAnswerLogViewHolder {
            val view = inflater.inflate(R.layout.abs_answer_log_row, parent, false)
            val viewHolder = AbsAnswerLogViewHolder(view)
            return viewHolder
        }

        override fun getItemCount() = absAnswerLogList.size

        override fun onBindViewHolder(holder: AbsAnswerLogViewHolder, position: Int) {
            val absAnswerLog = absAnswerLogList[position]
            holder.answerDateTime.text = absAnswerLog.endTime
            holder.answerTime.text = DateUtils.formatTime(absAnswerLog.elapsedTime * 1000)
            holder.answeerAccuracyRate.text = absAnswerLog.accuracyRate.toString() + "%"
        }
    }
    // ビューホルダー
    internal inner class AbsAnswerLogViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val answerDateTime = itemView.findViewById<TextView>(R.id.answer_date_time)
        val answerTime = itemView.findViewById<TextView>(R.id.ansewer_time)
        val answeerAccuracyRate = itemView.findViewById<TextView>(R.id.answer_correct_times)
    }
}