package com.example.study

import android.content.ContentValues
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
import android.widget.Button
import android.widget.TextView
import java.io.Serializable

class BlockItemFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView

    private val openTime = System.currentTimeMillis()
    private var startTime = System.currentTimeMillis()
    private var endTime = 0L
    private var restCount = 0
    private var correctCount = 0

    companion object {
        private const val BLOCK_ITEM = "BLOCK_ITEM"

        fun newInstance(blockItem: BlockItem): BlockItemFragment {
            val args = Bundle()
            args.putSerializable(BLOCK_ITEM, blockItem as Serializable)
            val fragment = BlockItemFragment()
            fragment.arguments = args
            return fragment
        }
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val fragment_block_item = inflater.inflate(R.layout.fragment_block_item, container,false)

        recyclerView = fragment_block_item.findViewById(R.id.blockItem)

        val ctx = context ?: return fragment_block_item

        val layoutManager = LinearLayoutManager(ctx, LinearLayoutManager.VERTICAL, false)
        recyclerView.layoutManager = layoutManager

        // RecyclerView区切線
        recyclerView.addItemDecoration(DividerItemDecoration(ctx, DividerItemDecoration.VERTICAL))

        val blockItem = arguments?.getSerializable(BLOCK_ITEM) as BlockItem

        restCount = blockItem.contents.size

        (activity as AppCompatActivity).supportActionBar?.title = blockItem.sheetTitle

        // block_sheetのシートタイトル
        fragment_block_item.findViewById<TextView>(R.id.sheettitle).text = blockItem.subtitle

        // fragment_block_item と block_content を結びつける
        val adapter = BlockItemAdapter(ctx, blockItem)
        recyclerView.adapter = adapter

        return fragment_block_item
    }

    internal inner class BlockItemAdapter(
        private val context: Context,
        private val blockItem: BlockItem) : RecyclerView.Adapter<BlockContentViewHolder>() {

        private val inflater = LayoutInflater.from(context)

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BlockContentViewHolder {
            val block_content = inflater.inflate(R.layout.block_content, parent, false)

            val answer = block_content.findViewById<Button>(R.id.answer)
            val correct = block_content.findViewById<Button>(R.id.correct)
            val incorrect = block_content.findViewById<Button>(R.id.incorrect)

            correct.visibility = View.INVISIBLE
            incorrect.visibility = View.INVISIBLE

            answer.setOnClickListener {
                endTime = System.currentTimeMillis()

                // 答えの表示
                val seqNo = block_content.findViewById<TextView>(R.id.seqNo).text as String
                val body = block_content.findViewById<TextView>(R.id.body)
                body.text = blockItem.contents[Integer.parseInt(seqNo)].A

                answer.visibility = View.INVISIBLE
                correct.visibility = View.VISIBLE
                incorrect.visibility = View.VISIBLE

                (activity as AppCompatActivity).supportActionBar?.setHomeButtonEnabled(false)
            }

            // 正解ボタン押下
            correct.setOnClickListener {
                val elapsedTime = endTime - startTime
                print("correct elapsedTime::")
                println(elapsedTime)
                val seqNo = block_content.findViewById<TextView>(R.id.seqNo).text as String

                // 解答結果を記録する
                insertRecordDetail(seqNo, "1")

                // ボタン制御
                correct.isClickable = false
                incorrect.visibility = View.INVISIBLE

                ++correctCount

                postAnswer()
            }

            // 不正解ボタン押下
            incorrect.setOnClickListener {
                val elapsedTime = endTime - startTime
                print("incorrect elapsedTime::")
                println(elapsedTime)
                val seqNo = block_content.findViewById<TextView>(R.id.seqNo).text as String

                // 解答結果を記録する
                insertRecordDetail(seqNo, "0")

                // ボタン制御
                correct.visibility = View.INVISIBLE
                incorrect.isClickable = false

                postAnswer()
            }

            val viewHolder = BlockContentViewHolder(block_content)

            return viewHolder
        }

        // 解答後処理
        fun postAnswer() {

            --restCount

            if (restCount == 0) {
                insertRecord()
                registerRecentResult()
                (activity as AppCompatActivity).supportActionBar?.setHomeButtonEnabled(true)

            } else {
                startTime = System.currentTimeMillis()
            }
        }

        fun insertRecord() {
            val record = ContentValues().apply {
                put("blockNo", blockItem.blockNo)
                put("itemNo", blockItem.itemNo)
                put("accuracyRate", 100 * correctCount / blockItem.contents.size)
                put("startTime", openTime)
                put("endTime", endTime)
                put("elapsedTime", endTime - openTime)
            }
            val db = BlockStudyDatabase(context).writableDatabase
            db.insert("Records", null, record)
            db.close()
        }

        fun insertRecordDetail(seqNo: String, result: String) {
            val record = ContentValues().apply {
                put("blockNo", blockItem.blockNo)
                put("itemNo", blockItem.itemNo)
                put("seqNo", seqNo)
                put("result", result)
                put("startTime", startTime)
                put("endTime", endTime)
                put("elapsedTime", endTime - startTime)
            }
            val db = BlockStudyDatabase(context).writableDatabase
            db.insert("RecordDetails", null, record)
            db.close()
        }

        // 抽象化ブロックシートの各項目の最新学習結果を登録する
        fun registerRecentResult() {
            val db = BlockStudyDatabase(context).writableDatabase

            db.query(
                "RecentResults",
                arrayOf("correctTimes", "status", "beginTime"),
                "blockNo = ? and itemNo = ?",
                arrayOf(blockItem.blockNo, blockItem.itemNo),
                null,
                null,
                null,
                null
            ).use { c ->
                val isCorrect = (correctCount == blockItem.contents.size)

                // 最新学習結果を更新する
                if (c.moveToFirst()) {
                    var correctTimes = c.getInt(c.getColumnIndex("correctTimes"))
                    var status = c.getInt(c.getColumnIndex("status"))
                    var beginTime = c.getLong(c.getColumnIndex("beginTime"))
                    var answerResult = 0

                    // 全正解の場合
                    if (isCorrect) {
                        correctTimes++
                        answerResult = 1
                        if (status < 4 && outOfTerm(status, endTime - beginTime)) status++
                        beginTime = if (status == 0) endTime else beginTime
                    } else {
                        status = if (status == 4 || outOfTerm(status, endTime - beginTime)) 0 else status
                    }

                    val update = ContentValues().apply {
                        put("correctTimes", correctTimes)
                        put("status", status)
                        put("answerResult", answerResult)
                        put("beginTime", beginTime)
                        put("updateTime", endTime)
                    }
                    db.update(
                        "RecentResults",
                        update,
                        "blockNo = ? and itemNo = ?",
                        arrayOf(blockItem.blockNo, blockItem.itemNo)
                    )

                } else {
                    val correctTimes = if (isCorrect) 1 else 0
                    val status = if (isCorrect) 1 else 0
                    val answerResult = if (isCorrect) 1 else 0

                    // レコードがない場合、追加する
                    val record = ContentValues().apply {
                        put("blockNo", blockItem.blockNo)
                        put("itemNo", blockItem.itemNo)
                        put("correctTimes", correctTimes)
                        put("status", status)
                        put("answerResult", answerResult)
                        put("beginTime", endTime)
                        put("updateTime", endTime)
                    }
                    db.insert("RecentResults", null, record)
                }
            }
            db.close()
        }

        override fun getItemCount() = blockItem.contents.size

        override fun onBindViewHolder(holder: BlockContentViewHolder, position: Int) {
            val content = blockItem.contents[position]
            holder.body.text = content.Q
            holder.no.text = position.toString()
        }
    }

    internal inner class BlockContentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val no = itemView.findViewById<TextView>(R.id.seqNo)
        val body = itemView.findViewById<TextView>(R.id.body)
    }
}